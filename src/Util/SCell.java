package Util;// Add your documentation below:

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
        if (Character.isLetter(s.charAt(0))) {
            String cord = s.substring(1);
            if (cord.length() >= 3) {
                return false;
            }
            for (char chCord : cord.toCharArray()) {
                if (!Character.isDigit(chCord)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates if the given string is a valid mathematical formula.
     * A valid formula adheres to the following rules:
     * - The formula must start with '='.
     * - Every operator (+, -, *, /) must have a number or parentheses on either side.
     * - Parentheses must be balanced and cannot be empty (e.g., "()").
     * - The formula must not contain invalid characters.
     *
     * @param text the string to validate
     * @return true if the string is a valid formula, false otherwise
     */
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

        // Check for invalid characters
        if (!text.matches("[0-9a-zA-Z+\\-*/().]*")) {
//        if (!text.matches("[0-9+\\-*/().]*")) {
            return false;
        }

        // Check for balanced parentheses and non-empty parentheses
        int parenthesesBalance = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                parenthesesBalance++;
                // Ensure the next character after '(' is valid
                if (i == text.length() - 1 || (text.charAt(i + 1) == ')')) {
                    return false; // Empty parentheses
                }
            } else if (c == ')') {
                parenthesesBalance--;
            }

            // Unbalanced if closing parentheses appear before an opening one
            if (parenthesesBalance < 0) {
                return false;
            }
        }
        if (parenthesesBalance != 0) {
            return false;
        }

        // Check that operators have valid operands on either side
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);

            // If the current character is an operator
            if (current == '+' || current == '-' || current == '*' || current == '/') {
                // Check the character before the operator
                if (i == 0 || (!Character.isDigit(text.charAt(i - 1)) && text.charAt(i - 1) != ')')) {
                    return false;
                }

                // Check the character after the operator
                if (i == text.length() - 1 || (!Character.isDigit(text.charAt(i + 1)) && text.charAt(i + 1) != '(')) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Computes the numeric value of a string expression.
     * Removes the '=' character at the beginning, if present, and evaluates the expression.
     *
     * @param text the string representing the formula to compute
     * @return the computed numeric value of the formula
     */
    public double computeForm(String text) {
        if (text.startsWith("=")) {
            text = text.substring(1).replaceAll("\\s", ""); // Remove '=' and whitespace
        }

        // Evaluate the expression
        return evaluateExpression(text); // This function parses and evaluates the formula
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
     * Evaluates a simple mathematical expression without parentheses.
     * Performs operations in sequence based on the presence of arithmetic operators.
     *
     * @param expression the string representing the simple mathematical expression
     * @return the evaluated numeric result of the simple expression
     */
    private double evaluateSimpleExpression(String expression) {
        double result = 0.0;
        char operator = '+';
        int currentIndex = 0;

        while (currentIndex < expression.length()) {
            // Find the next operator
            int nextOperatorIndex = findNextOperator(expression, currentIndex);

            // If there's no operator, treat the remaining string as a number
            if (nextOperatorIndex == -1) {
                if (expression.substring(currentIndex).matches("[0-9]")) {
                    result = applyOperator(result, operator, Double.parseDouble(expression.substring(currentIndex)));
                    break;
                }
                if (isCoordinate(expression.substring(currentIndex))) {
                    //Coordinate coordinate = Coordinate.parseCell(expression.substring(currentIndex));
                    Coordinate coordinate = Coordinate.parseCell(expression.substring(currentIndex)); // try
                    Sheet.eval();
                    String parsedNum = Sheet.eval(coordinate.getX(), coordinate.getY()); // try
                    int value = Integer.parseInt(Sheet.eval(coordinate.getX(), coordinate.getY()));
                    evaluateExpression(parsedNum); // try
                }
            }

            // Extract the current number and apply the operator
            String numberString = expression.substring(currentIndex, nextOperatorIndex).trim();
            double number = Double.parseDouble(numberString);
            result = applyOperator(result, operator, number);

            // Move to the next operator
            operator = expression.charAt(nextOperatorIndex);
            currentIndex = nextOperatorIndex + 1;
        }

        return result;
    }

    /**
     * Finds the index of the next arithmetic operator (+, -, *, /) in the given expression.
     *
     * @param expression the string containing the mathematical expression
     * @param startIndex the starting index to search for an operator
     * @return the index of the next operator, or -1 if no operator is found
     */
    private static int findNextOperator(String expression, int startIndex) {
        for (int i = startIndex; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                return i;
            }
        }
        return -1; // No operator found
    }

    /**
     * Applies a given arithmetic operator to two numeric values.
     *
     * @param result the current result
     * @param operator the operator to apply (+, -, *, /)
     * @param number the numeric value to apply the operator with
     * @return the result after applying the operator
     * @throws IllegalArgumentException if the operator is invalid
     */
    private static double applyOperator(double result, char operator, double number) {
        switch (operator) {
            case '+':
                return result + number;
            case '-':
                return result - number;
            case '*':
                return result * number;
            case '/':
                return result / number;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
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
        return getData();
    }

    @Override
    public void setData(String s) {
        // Add your code here
        line = s;
        /////////////////////
        if (Ex2Utils.Debug) {
            System.out.println("Setting data: " + s);
        }
        if (s == null || s.isEmpty() || isText(s)) {
            type = Ex2Utils.TEXT;
        } else if (SCell.isNumber(s)) {
            type = Ex2Utils.NUMBER;
        } else if (SCell.isForm(s) || SCell.isCoordinate(s)) {
            type = Ex2Utils.FORM;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }
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

//    private static boolean isNumber(String str) {
//        try {
//            Double.parseDouble(str);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
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

    public double computeForm(String text, Ex2Sheet sheet) {
        if (text.startsWith("=")) {
            text = text.substring(1).replaceAll("\\s", ""); // Remove '=' and whitespace
        }

        // Parse and replace cell references
        List<CellEntry> dependencies = DependencyParser.parseDependencies(text);
        for (CellEntry dep : dependencies) {
            Cell cell = sheet.get(dep.getX(), dep.getY());
            if (cell == null || cell.getData().isEmpty()) {
                throw new IllegalArgumentException("Referenced cell " + dep + " is empty or invalid.");
            }
            text = text.replace(dep.toString(), cell.getData());
        }

        // Evaluate the modified expression
        return evaluateExpression(text);
    }

    public boolean isReference(SCell cell) {
        String text = cell.getData();
        boolean flag = false;
        for (int i =0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                flag = isCellValid(text.substring(i, i+2));
                if (flag) {
                    return flag;
                }
                flag = isCellValid(text.substring(i, i+1));
                }
            }
        return flag;
        }

    public static boolean isCellValid(String text) {
        if (text == null) {
            return false;
        }
        if (text.length() > 3 || text.length() < 2) {
            return false;
        }
        if (Character.isDigit(text.charAt(1)) && Character.isDigit(text.charAt(2))) {
            return true;
        }
        if (text.length() == 3 && Character.isDigit(text.charAt(1)) && Character.isDigit(text.charAt(2))) {
            return true;
        }
        return false;
    }
}


