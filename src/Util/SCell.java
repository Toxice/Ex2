package Util;// Add your documentation below:

import java.util.ArrayList;
import java.util.List;

public class SCell implements Cell {
    public Ex2Sheet Sheet;
    //public static Ex2Sheet Sheet = new Ex2Sheet();
    private String line;
    private int type;
    // Add your code here
    private int order;    // The cell's dependency order

    /**
     * Creates a new cell with the specified content.
     * @param s The initial content of the cell
     */
    public SCell(String s) {
        setData(s);
    }

    /**
     * Creates a new cell with the specified content and parent sheet.
     * @param s The initial content of the cell
     * @param sheet The parent spreadsheet containing this cell
     */
    public SCell(String s, Ex2Sheet sheet) {
        this.Sheet = sheet;
        setData(s);
    }

    public SCell() {
        this(Ex2Utils.EMPTY_CELL);
    }

    public SCell(int x, int y, Ex2Sheet sheet) {
        this.Sheet = sheet;
        String data = Sheet.get(x,y).getData();
        setData(data);
    }

    /**
     * Check's if the given String is made only from Numbers
     * @param text a given String
     * @return true iff the String contains only digits
     */
    public static boolean isNumber(String text) {
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /** Check's if the given String is a Text
     * @param text a given String
     * @return false if Text starts with "=" or if Text is made only from Numbers, True iff Text doesn't contain any digits or the equals sign
     */
    public static boolean isText(String text) {
        if (text.startsWith("=")) {
            return false;
        }
        if (isNumber(text) || isCoordinate(text)) {
            return false;
        }
        return true;
    }

    /**
     * Check's if the given String is a Coordinate (Cell)
     * @param s a given String
     * @return true iff the String start's with a Big Letter and the Rest is a number from 0 to 99
     */
    public static boolean isCoordinate(String s) { //A00
        if (isNumber(s)) {
            return false;
        }
        String nums = s.substring(1); // number from 0~99
        if (!Character.isLetter(s.charAt(0)) || nums.length() >= 3 || nums.length() < 1) {
            return false;
        }
        return true;
    }

    /**
     * Validates if a string represents a valid formula.
     * A valid formula must:
     * - Start with '='
     * - Contain valid cell references, numbers, operators (+,-,*,/), and/or balanced parentheses
     * - Not end with an operator
     * - Have valid syntax for mathematical expressions
     *
     * @param text The string to validate
     * @return true if the string is a valid formula, false otherwise
     */
    public static boolean isForm(String text) {
        // Basic null and empty checks
        if (text == null || text.isEmpty()) {
            return false;
        }

        // Must start with '='
        if (!text.startsWith("=")) {
            return false;
        }

        // Remove '=' and trim spaces
        String formula = text.substring(1).replaceAll("\\s+", "");

        // Formula can't be empty after '='
        if (formula.isEmpty()) {
            return false;
        }

        // If it's just a number after =, that's valid
        if (isNumber(formula)) {
            return true;
        }

        // If it's just a cell reference after =, that's valid
        if (isCoordinate(formula)) {
            return true;
        }

        // Single letter without number is invalid
        if (formula.length() == 1 && Character.isLetter(formula.charAt(0))) {
            return false;
        }

        // Check for balanced parentheses
        if (!hasBalancedParentheses(formula)) {
            return false;
        }

        // Check formula structure
        return validateFormula(formula);
    }

    /**
     * Helper method to validate the structure of a formula after the '=' sign.
     * Ensures the formula follows proper mathematical expression syntax by:
     * - Checking for valid operands (numbers or cell references)
     * - Validating operator placement
     * - Verifying parentheses structure
     * - Ensuring no invalid sequences (like two operators in a row)
     *
     * @param formula The formula string without the leading '=' to validate
     * @return true if the formula structure is valid, false otherwise
     */
    private static boolean validateFormula(String formula) {
        // Split into tokens preserving operators
        String[] tokens = formula.split("(?<=[-+*/()])|(?=[-+*/()])");
        boolean expectingOperand = true;
        int parenthesesCount = 0;

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            if (expectingOperand) {
                // Valid operands: numbers, cell references, or open parenthesis
                if (isNumber(token) || isCoordinate(token) || token.equals("(")) {
                    expectingOperand = false;
                    if (token.equals("(")) {
                        parenthesesCount++;
                        expectingOperand = true;
                    }
                } else {
                    return false;
                }
            } else {
                // Valid operators: +, -, *, /, or close parenthesis
                if (token.matches("[-+*/]")) {
                    expectingOperand = true;
                } else if (token.equals(")")) {
                    parenthesesCount--;
                    expectingOperand = false;
                } else {
                    return false;
                }
            }
        }

        // Formula shouldn't end with an operator or open parenthesis
        return !expectingOperand && parenthesesCount == 0;
    }

    /**
     * Checks if a string expression has properly balanced parentheses.
     * Balanced parentheses means that:
     * - Each '(' has a matching ')'
     * - The closing parentheses come after their corresponding opening parentheses
     * - No closing parenthesis appears before its matching opening parenthesis
     *
     * Examples:
     * - "(A1+B2)" -> true
     * - "((2+3)*(4+5))" -> true
     * - "(A1" -> false
     * - "A1)" -> false
     * - ")(A1)" -> false
     * - "(A1))" -> false
     *
     * @param expr The expression to check for balanced parentheses
     * @return true if parentheses are properly balanced, false otherwise
     */
    private static boolean hasBalancedParentheses(String expr) {
        int count = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count < 0) {
                    return false;
                }
            }
        }
        return count == 0;
    }

    /**
     * Evaluates a mathematical formula and returns its numeric result.
     * This method handles:
     * - Simple numbers (e.g., "=5")
     * - Cell references (e.g., "=A1")
     * - Mathematical expressions (e.g., "=2+3*4")
     * - Complex formulas with nested references (e.g., "=A1+B2*C3")
     *
     * Special cases and error handling:
     * - If the cell has ERR_CYCLE_FORM type, throws IllegalArgumentException
     * - If a referenced cell contains non-numeric data, throws IllegalArgumentException
     * - If a formula creates a self-reference, throws IllegalArgumentException with "Self-reference detected"
     * - Division by zero throws ArithmeticException
     *
     * Operator precedence follows standard mathematical rules:
     * 1. Parentheses
     * 2. Multiplication and Division (left to right)
     * 3. Addition and Subtraction (left to right)
     *
     * Examples:
     * - computeForm("=5") -> 5.0
     * - computeForm("=2+3") -> 5.0
     * - computeForm("=2*3+4") -> 10.0
     * - computeForm("=(2+3)*4") -> 20.0
     * - computeForm("=A1") -> value of cell A1
     *
     * @param text The formula to evaluate, starting with "="
     * @return The computed numeric result as a double
     */
    public double computeForm(String text) {
        // First check for cyclic reference
        if (type == Ex2Utils.ERR_CYCLE_FORM) {
            throw new IllegalArgumentException("Cyclic reference detected");
        }

        // If it's just a number after the equals sign, return it directly
        if (text.startsWith("=") && isNumber(text.substring(1))) {
            return Double.parseDouble(text.substring(1));
        }

        // Process formula by replacing cell references with their actual values
        String processedFormula = text;
        if (text.startsWith("=")) {
            processedFormula = text.substring(1).trim();
        }

        // Find current cell coordinates
        CellEntry thisCell = null;
        for (int x = 0; x < Sheet.width(); x++) {
            for (int y = 0; y < Sheet.height(); y++) {
                if (Sheet.get(x, y) == this) {
                    thisCell = new CellEntry(x, y);
                    break;
                }
            }
            if (thisCell != null) break;
        }

        // If it's a single cell reference (like "A0")
        if (isCoordinate(processedFormula)) {
            Coordinate coord = Coordinate.parseCell(processedFormula);

            // Check for self-reference
            if (thisCell != null &&
                    coord.getX() == thisCell.getX() &&
                    coord.getY() == thisCell.getY()) {
                throw new IllegalArgumentException("Self-reference detected");
            }

            Cell referencedCell = Sheet.get(coord.getX(), coord.getY());
            String cellData = referencedCell.getData();

            if (isNumber(cellData)) {
                return Double.parseDouble(cellData);
            } else if (cellData.startsWith("=")) {
                SCell tempCell = new SCell(cellData, Sheet);
                return tempCell.computeForm(cellData);
            }
            throw new IllegalArgumentException("Referenced cell does not contain a numeric value");
        }

        // Rest of the computeForm implementation remains the same...
        // (Previous implementation for handling complex formulas)

        // For complex formulas containing cell references and operators
        StringBuilder processedExpr = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < processedFormula.length(); i++) {
            char c = processedFormula.charAt(i);

            if (Character.isLetter(c)) {
                currentToken.append(c);
                while (i + 1 < processedFormula.length() && Character.isDigit(processedFormula.charAt(i + 1))) {
                    currentToken.append(processedFormula.charAt(++i));
                }

                String token = currentToken.toString();
                if (isCoordinate(token)) {
                    Coordinate coord = Coordinate.parseCell(token);

                    // Check for self-reference in complex formulas
                    if (thisCell != null &&
                            coord.getX() == thisCell.getX() &&
                            coord.getY() == thisCell.getY()) {
                        throw new IllegalArgumentException("Self-reference detected");
                    }

                    Cell referencedCell = Sheet.get(coord.getX(), coord.getY());
                    String cellValue = referencedCell.getData();

                    if (isNumber(cellValue)) {
                        processedExpr.append(cellValue);
                    } else if (cellValue.startsWith("=")) {
                        SCell tempCell = new SCell(cellValue, Sheet);
                        processedExpr.append(tempCell.computeForm(cellValue));
                    } else {
                        throw new IllegalArgumentException("Referenced cell does not contain a numeric value");
                    }
                } else {
                    processedExpr.append(token);
                }
                currentToken.setLength(0);
            } else {
                processedExpr.append(c);
            }
        }

        return evaluateExpression(processedExpr.toString());
    }



    /**
     * Evaluates a mathematical expression.
     * Handles parentheses and arithmetic operations recursively.
     *
     * @param expression the string representing the mathematical expression
     * @return the evaluated numeric result of the expression
     */
    private double evaluateExpression(String expression) {
        // Remove any spaces
        expression = expression.replaceAll("\\s", "");

        // If the expression has parentheses, evaluate inside the parentheses first
        if (expression.contains("(")) {
            int openIndex = expression.lastIndexOf("(");
            int closeIndex = expression.indexOf(")", openIndex);

            // Recursively evaluate the part inside parentheses
            String subExpression = expression.substring(openIndex + 1, closeIndex);
            double subResult = evaluateExpression(subExpression);

            // Replace the sub-expression with the result
            expression = expression.substring(0, openIndex) + subResult + expression.substring(closeIndex + 1);
            return evaluateExpression(expression); // Re-evaluate the modified expression
        }

        // If there are no parentheses, perform arithmetic operations
        return evaluateSimpleExpression(expression);
    }

    /**
     * Evaluates an expression containing only addition and subtraction.
     * Splits expression into terms and handles multiplication chains within each term.
     *
     * @param expression mathematical expression with + and - operations
     * @return calculated result
     */
    private double evaluateSimpleExpression(String expression) {
        // First split on addition and subtraction
        List<String> terms = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '+' || expression.charAt(i) == '-') {
                terms.add(expression.substring(start, i));
                operators.add(expression.charAt(i));
                start = i + 1;
            }
        }
        terms.add(expression.substring(start)); // Add last term

        // Evaluate each term (handling multiplication and division first)
        List<Double> evaluatedTerms = new ArrayList<>();
        for (String term : terms) {
            evaluatedTerms.add(evaluateMultiplicationChain(term));
        }

        // Now do addition and subtraction
        double result = evaluatedTerms.get(0);
        for (int i = 0; i < operators.size(); i++) {
            double nextTerm = evaluatedTerms.get(i + 1);
            if (operators.get(i) == '+') {
                result += nextTerm;
            } else if (operators.get(i) == '-') {
                result -= nextTerm;
            }
        }

        return result;
    }

    /**
     * Evaluates a sequence of multiplication and division operations.
     * This method handles the second level of operator precedence after parentheses.
     *
     * The method:
     * 1. Splits the expression on '*' and '/' operators
     * 2. Evaluates each factor (numbers or cell references)
     * 3. Applies operations from left to right
     *
     * Example evaluations:
     * - "2*3" -> 6.0
     * - "6/2" -> 3.0
     * - "2*3*4" -> 24.0
     * - "A1*B2" -> product of values in cells A1 and B2
     * - "8/2*4" -> 16.0 (evaluated left to right)
     *
     * @param expression The mathematical expression containing only multiplication
     *                  and division operations
     * @return The result of evaluating the multiplication chain
     */
    private double evaluateMultiplicationChain(String expression) {
        // Split on multiplication and division
        List<String> factors = new ArrayList<>();
        List<Character> operators = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '*' || expression.charAt(i) == '/') {
                factors.add(expression.substring(start, i));
                operators.add(expression.charAt(i));
                start = i + 1;
            }
        }
        factors.add(expression.substring(start)); // Add last factor

        // Evaluate each factor (it might be a number or cell reference)
        List<Double> evaluatedFactors = new ArrayList<>();
        for (String factor : factors) {
            factor = factor.trim();
            if (factor.matches("\\d+(\\.\\d+)?")) {
                evaluatedFactors.add(Double.parseDouble(factor));
            } else if (isCoordinate(factor)) {
                Coordinate coordinate = Coordinate.parseCell(factor);
                String cellValue = Sheet.eval(coordinate.getX(), coordinate.getY());
                evaluatedFactors.add(Double.parseDouble(cellValue));
            } else {
                throw new IllegalArgumentException("Invalid token: " + factor);
            }
        }

        // Now do multiplication and division
        double result = evaluatedFactors.get(0);
        for (int i = 0; i < operators.size(); i++) {
            double nextFactor = evaluatedFactors.get(i + 1);
            if (operators.get(i) == '*') {
                result *= nextFactor;
            } else if (operators.get(i) == '/') {
                result /= nextFactor;
            }
        }

        return result;
    }

    /**
     * Removes Space from the String
     * @param text a String
     * @return the String without Spaces
     */
    public String removeSpace(String text) {
        return text.replaceAll(" ", "");
    }


    /**
     * Gets the Order of a SCell
     * @return the order (Depth)
     */
    @Override
    public int getOrder() {
        // Handle error cases first
        if (type == Ex2Utils.ERR_FORM_FORMAT) {
            return -2;
        }
        if (type == Ex2Utils.ERR_CYCLE_FORM) {
            return -1;
        }

        // If it's not a formula, depth is 0
        if (!getData().startsWith("=")) {
            return 0;
        }

        // Check for self-reference
        CellEntry thisCell = null;
        for (int x = 0; x < Sheet.width(); x++) {
            for (int y = 0; y < Sheet.height(); y++) {
                if (Sheet.get(x, y) == this) {
                    thisCell = new CellEntry(x, y);
                    break;
                }
            }
            if (thisCell != null) break;
        }

        // Get dependencies and check for self-reference
        List<CellEntry> dependencies = DependencyParser.parseDependencies(getData());

        // If no dependencies (like "=5"), return 1
        if (dependencies.isEmpty()) {
            return 1;
        }

        // Check for self-reference
        if (thisCell != null) {
            for (CellEntry dep : dependencies) {
                if (dep.getX() == thisCell.getX() && dep.getY() == thisCell.getY()) {
                    return -1; // Self-reference found
                }
            }
        }

        // Calculate maximum depth from dependencies
        int maxDepth = 0;
        for (CellEntry dep : dependencies) {
            if (!dep.isValid()) continue;
            Cell depCell = Sheet.get(dep.getX(), dep.getY());
            int depOrder = depCell.getOrder();
            if (depOrder == -1) {
                return -1; // Propagate cyclic reference error
            }
            maxDepth = Math.max(maxDepth, depOrder);
        }

        return 1 + maxDepth;
    }

    @Override
    public String toString() {
        if (getData().startsWith("=")) {
            return getData().substring(1);
        }
        else {
            return getData();
        }
    }

    /**
     * Sets the data content of this cell and determines its type based on the content.
     * This method analyzes the input string and sets both the content and the appropriate type.
     *
     * Type determination rules:
     * - null or empty string -> TEXT type
     * - Starts with '=' -> FORM type (if valid) or ERR_FORM_FORMAT (if invalid)
     * - Contains only digits -> NUMBER type
     * - All other cases -> TEXT type
     *
     * Special handling for formulas:
     * - Checks for self-references in formulas (e.g., cell A1 containing "=A1")
     * - Detects both direct and indirect self-references
     * - Marks cells with self-references as ERR_CYCLE_FORM type
     *
     * Examples:
     * - setData("123") -> NUMBER type
     * - setData("abc") -> TEXT type
     * - setData("=A1+B2") -> FORM type
     * - setData("=invalid!") -> ERR_FORM_FORMAT type
     * - setData("=A1") in cell A1 -> ERR_CYCLE_FORM type
     *
     * @param s The string content to set in the cell
     *        Can be:
     *        - null or empty string
     *        - A numeric string
     *        - A text string
     *        - A formula (starting with '=')
     *
     */
    @Override
    public void setData(String s) {
        line = s;

        if (s == null || s.isEmpty()) {
            type = Ex2Utils.TEXT;
            return;
        }

        if (s.startsWith("=")) {
            // Check for self-reference in the formula
            List<CellEntry> dependencies = DependencyParser.parseDependencies(s);

            // Find this cell's coordinates
            CellEntry thisCell = null;
            for (int x = 0; x < Sheet.width(); x++) {
                for (int y = 0; y < Sheet.height(); y++) {
                    if (Sheet.get(x, y) == this) {
                        thisCell = new CellEntry(x, y);
                        break;
                    }
                }
                if (thisCell != null) break;
            }

            // Check if any dependency refers to this cell
            if (thisCell != null) {
                for (CellEntry dep : dependencies) {
                    if (dep.getX() == thisCell.getX() && dep.getY() == thisCell.getY()) {
                        type = Ex2Utils.ERR_CYCLE_FORM;
                        return;
                    }
                }
            }

            // If no self-reference was found, proceed with normal type checking
            if (SCell.isForm(s)) {
                type = Ex2Utils.FORM;
            } else {
                type = Ex2Utils.ERR_FORM_FORMAT;
            }
            return;
        }

        if (SCell.isNumber(s)) {
            type = Ex2Utils.NUMBER;
            return;
        }

        type = Ex2Utils.TEXT;
    }
    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets the Type of SCell
     * @param t an int type value as defines in Util.Ex2Utils.
     */
    @Override
    public void setType(int t) {
        type = t;
        if (t == 1) {
            type = Ex2Utils.TEXT;
        } else if (t == 2) {
            type = Ex2Utils.NUMBER;
        } else if (t == 3) {
            type = Ex2Utils.FORM;
        } else if (t == -2){
            type = Ex2Utils.ERR_FORM_FORMAT;
        } else {
            type = Ex2Utils.ERR_CYCLE_FORM;
        }
    }

    /**
     * Sets the Order of a SCell
     * @param t Type of the SCell
     */
    @Override
    public void setOrder(int t) {
        // Set the order based on input parameter t
        // If cell is in error state, force order to be -1
        if (type == Ex2Utils.ERR_CYCLE_FORM || type == Ex2Utils.ERR_FORM_FORMAT) {
            this.order = -1;
        }
        // Otherwise, set to specified order
        else {
            this.order = t;
        }
    }

    /**
     * Helper method that evaluates part of an expression between given positions.
     * Handles the recursive breakdown of nested expressions.
     *
     * @param expr full expression string
     * @param start start position to evaluate from
     * @param end end position to evaluate to
     * @return calculated resulto
     */
        private static double evaluateExpression(String expr, int start, int end) {
            int lastOperator = -1;
            int parenthesesCount = 0;

            for (int i = start; i < end; i++) {
                char ch = expr.charAt(i);

                if (ch == '(') {
                    parenthesesCount++;
                } else if (ch == ')') {
                    parenthesesCount--;
                }

                if (parenthesesCount == 0 && isOperator(ch)) {
                    lastOperator = i;
                }
            }

            if (lastOperator == -1) {
                if (expr.charAt(start) == '(' && expr.charAt(end - 1) == ')') {
                    return evaluateExpression(expr, start + 1, end - 1);
                }
                return Double.parseDouble(expr.substring(start, end));
            }

            char operator = expr.charAt(lastOperator);
            double left = evaluateExpression(expr, start, lastOperator);
            double right = evaluateExpression(expr, lastOperator + 1, end);
            return applyOperator(operator, left, right);
        }

    /**
     * Checks if a character is a valid arithmetic operator (+, -, *, /).
     *
     * @param c character to check
     * @return true if character is +, -, *, or /
     */
        private static boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/';
        }

    /**
     * Applies an arithmetic operator to two numbers.
     *
     * @param operator the arithmetic operator (+, -, *, /)
     * @param left left operand
     * @param right right operand
     * @return result of operation
     */
        private static double applyOperator(char operator, double left, double right) {
            switch (operator) {
                case '+': return left + right;
                case '-': return left - right;
                case '*': return left * right;
                case '/':
                    if (right == 0) throw new ArithmeticException("Division by zero");
                    return left / right;
                default: throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

    /**
     * Validates a mathematical expression by checking number format
     * and operator placement.
     *
     * @param expr expression to validate
     * @return true if expression follows valid mathematical syntax
     */
    private static boolean isValidFormula(String expr) {
        // Base case: check if it's a valid number
        if (isNumber(expr)) {
            return true;
        }

        // Check for valid parentheses structure
        if (!hasBalancedParentheses(expr)) {
            return false;
        }

        int parenthesesCount = 0;
        int lastOperator = -1;

        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);

            if (ch == '(') {
                parenthesesCount++;
            } else if (ch == ')') {
                parenthesesCount--;
            }

            // Track the last operator outside of parentheses
            if (parenthesesCount == 0 && isOperator(ch)) {
                lastOperator = i;
            }
        }

        // If no valid operator is found, check if it is wrapped in parentheses
        if (lastOperator == -1) {
            return expr.startsWith("(") && expr.endsWith(")")
                    && isValidFormula(expr.substring(1, expr.length() - 1));
        }

        // Check left and right parts of the expression
        String left = expr.substring(0, lastOperator);
        String right = expr.substring(lastOperator + 1);

        return isValidFormula(left) && isValidFormula(right);
    }

}


