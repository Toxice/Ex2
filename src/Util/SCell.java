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
    public SCell(String s) {
        // Add your code here
        setData(s);
    }

    public SCell(String s, Ex2Sheet sheet) {
        this.Sheet = sheet;
        setData(s);
    }

    public SCell() {
        this(Ex2Utils.EMPTY_CELL);
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
        if (!Character.isLetter(s.charAt(0)) || nums.length() > 2 || nums.length() != 1) {
            return false;
        }
        return true;
    }

    public static boolean isCoord(String s) {
        if (isNumber(s)) {
            return false;
        }
        String nums = s.substring(1); // number from 0~99
        if (!Character.isLetter(s.charAt(0)) || nums.length() > 2) {
            return false;
        }
        return true;
    }

    public static boolean isForm(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        // Check that the formula starts with '='
        if (!text.startsWith("=")) {
            return false;
        }

        // Remove the '=' character at the beginning for further checks
        text = text.substring(1).replaceAll("\\s", "");

        // Check if it's a single cell reference (like "A0")
        if (isCoordinate(text)) {
            return true;
        }

        // If it's just a single letter without a number (like "A"), it's invalid
        if (text.length() == 1 && Character.isLetter(text.charAt(0))) {
            return false;
        }

        // Check for invalid characters
        if (!text.matches("[0-9a-zA-Z+\\-*/().]*")) {
            return false;
        }

        // Split the formula into tokens for validation
        String[] tokens = text.split("(?=[+\\-*/()])|(?<=[+\\-*/()])");
        boolean expectingOperand = true;

        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            if (expectingOperand) {
                // For operands, check if it's a number, valid cell reference, or opening parenthesis
                if (token.matches("\\d+") ||
                        (isCoordinate(token)) ||
                        token.equals("(")) {
                    expectingOperand = false;
                } else {
                    return false;
                }
            } else {
                if (token.matches("[+\\-*/]") || token.equals(")")) {
                    expectingOperand = true;
                } else {
                    return false;
                }
            }
        }

        return !expectingOperand;
    }

    /**
     * Processes a formula by replacing cell references with their values
     * @param formula The formula string (e.g., "=1+A0")
     * @return The processed formula with cell references replaced by their values
     */
    public String processFormula(String formula) {
        if (formula.startsWith("=")) {
            formula = formula.substring(1).trim();
        }

        StringBuilder processedFormula = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isLetter(c)) {
                // Start of a potential cell reference
                currentToken.append(c);
                // Look ahead for numbers that complete the cell reference
                while (i + 1 < formula.length() && Character.isDigit(formula.charAt(i + 1))) {
                    currentToken.append(formula.charAt(i + 1));
                    i++;
                }

                // Check if it's a valid cell reference
                String token = currentToken.toString();
                if (isCoordinate(token)) {
                    // Convert cell reference to coordinate
                    Coordinate coord = Coordinate.parseCell(token);
                    // Get value from the referenced cell
                    String cellValue = Sheet.eval(coord.getX(), coord.getY());
                    processedFormula.append(cellValue);
                } else {
                    processedFormula.append(token);
                }
                currentToken.setLength(0);
            } else {
                // For operators and numbers, just append them
                processedFormula.append(c);
            }
        }

        return processedFormula.toString();
    }

    public double computeForm(String text) {
        // If it's just a number after the equals sign, return it directly
        if (text.startsWith("=") && isNumber(text.substring(1))) {
            return Double.parseDouble(text.substring(1));
        }

        // Process formula by replacing cell references with their actual values
        String processedFormula = text;
        if (text.startsWith("=")) {
            processedFormula = text.substring(1).trim();
        }

        // If it's a single cell reference (like "A0")
        if (isCoordinate(processedFormula)) {
            Coordinate coord = Coordinate.parseCell(processedFormula);
            Cell referencedCell = Sheet.get(coord.getX(), coord.getY());

            // Get the raw data from the referenced cell
            String cellData = referencedCell.getData();

            // If the referenced cell contains a number, return it
            if (isNumber(cellData)) {
                return Double.parseDouble(cellData);
            }
            // If the referenced cell contains a formula, evaluate it
            else if (cellData.startsWith("=")) {
                // Create a new SCell to evaluate the formula to avoid recursion
                SCell tempCell = new SCell(cellData, Sheet);
                return tempCell.computeForm(cellData);
            }
            throw new IllegalArgumentException("Referenced cell does not contain a numeric value");
        }

        // For complex formulas containing cell references and operators
        StringBuilder processedExpr = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < processedFormula.length(); i++) {
            char c = processedFormula.charAt(i);

            if (Character.isLetter(c)) {
                // Start of potential cell reference
                currentToken.append(c);
                while (i + 1 < processedFormula.length() && Character.isDigit(processedFormula.charAt(i + 1))) {
                    currentToken.append(processedFormula.charAt(++i));
                }

                String token = currentToken.toString();
                if (isCoordinate(token)) {
                    Coordinate coord = Coordinate.parseCell(token);
                    Cell referencedCell = Sheet.get(coord.getX(), coord.getY());
                    String cellValue = referencedCell.getData();

                    if (isNumber(cellValue)) {
                        processedExpr.append(cellValue);
                    } else if (cellValue.startsWith("=")) {
                        // Create a new SCell to evaluate the formula
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

        // Evaluate the processed expression
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

    public String removeSpace(String text) {
        return text.replaceAll(" ", "");
    }

    @Override
    public int getOrder() {
        // Add your code here

        return 0;
        // ///////////////////
    }

    //@Override
    @Override
    public String toString() {
        if (getData().startsWith("=")) {
            return getData().substring(1);
        }
        else {
            return getData();
        }
    }

    @Override
    public void setData(String s) {
        line = s;

        if (s == null || s.isEmpty()) {
            type = Ex2Utils.TEXT;
            return;
        }

        if (s.startsWith("=")) {
            // Check if it's a simple self-reference
            if (s.length() > 1 && s.substring(1).trim().equals(getData())) {
                type = Ex2Utils.ERR_CYCLE_FORM;
                return;
            }

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

    @Override
    public void setType(int t) {
        type = t;
        if (t == 1) {
            type = Ex2Utils.TEXT;
        } else if (t == 2) {
            type = Ex2Utils.NUMBER;
        } else if (t == 3) {
            type = Ex2Utils.FORM;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }
    }

    @Override
    public void setOrder(int t) {
        // Add your code here

    }

        private static double evaluate(String expression) {
            return evaluateExpression(expression.replaceAll("\\s+", ""), 0, expression.length());
        }

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

        private static boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/';
        }

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

    private static boolean hasBalancedParentheses(String expr) {
        int count = 0;
        for (char ch : expr.toCharArray()) {
            if (ch == '(') {
                count++;
            } else if (ch == ')') {
                count--;
                if (count < 0) {
                    return false;
                }
            }
        }
        return count == 0;
    }
}


