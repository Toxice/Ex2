package Util;// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    // Add your code here

    public SCell(String s) {
        // Add your code here
        setData(s);
    }

    public SCell() {

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
    }

    @Override
    public void setOrder(int t) {
        // Add your code here

    }

        public static double computeForm(String text) {
            // Remove initial ':=' if present
            if (text.startsWith("=")) {
                text = text.substring(1);
            }
            return evaluate(text);
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

    public boolean isForm(String text) {
        // Check if the string starts with '='
        if (!text.startsWith("=")) {
            return false;
        }

        // Remove the leading '=' for validation
        text = text.substring(1);

        // Check if the formula is valid
        return isValidFormula(text.replaceAll("\\s+", ""));
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

    private static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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


