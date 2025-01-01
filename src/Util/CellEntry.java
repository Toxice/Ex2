package Util;// Add your documentation below:

public class CellEntry  implements Index2D {
    private int x;
    private int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellEntry() {

    }

    @Override
    public boolean isValid() {
        boolean ans = (x >= 0) && (y>= 0) && (y < Ex2Utils.HEIGHT) && (x < Ex2Utils.WIDTH);
        return ans;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }


    /**
     * Check's if the given String is made only from Numbers
     * @param text a given String
     * @return true iff the String contains only digits
     */
    public boolean isNumber(String text) {
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
    public boolean isText(String text) {
        if (text.startsWith("=")) {
            return false;
        }
        if (isNumber(text)) {
            return false;
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
    public boolean isForm(String text) {
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
        if (!text.matches("[0-9+\\-*/().]*")) {
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
        // Remove the '=' character at the beginning if it exists
        if (text.startsWith("=")) {
            text = text.substring(1);
        }

        // Start parsing the expression
        return evaluateExpression(text);
    }

    /**
     * Evaluates a mathematical expression.
     * Handles parentheses and arithmetic operations recursively.
     *
     * @param expression the string representing the mathematical expression
     * @return the evaluated numeric result of the expression
     */
    private static double evaluateExpression(String expression) {
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
    private static double evaluateSimpleExpression(String expression) {
        double result = 0.0;
        char operator = '+';
        int currentIndex = 0;

        while (currentIndex < expression.length()) {
            // Find the next operator
            int nextOperatorIndex = findNextOperator(expression, currentIndex);

            // If there's no operator, treat the remaining string as a number
            if (nextOperatorIndex == -1) {
                result = applyOperator(result, operator, Double.parseDouble(expression.substring(currentIndex)));
                break;
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
}
