package Util;

import java.util.ArrayList;
import java.util.List;

public class DependencyParser {
    Ex2Sheet sheet = new Ex2Sheet();


    /**
     * Parses a formula string and extracts all referenced cells.
     * @param formula The formula string (e.g., "=A1+B2").
     * @return A list of CellEntry objects representing the dependencies.
     */
    public static List<CellEntry> parseDependencies(String formula) {
        List<CellEntry> dependencies = new ArrayList<>();

        // Remove '=' and whitespace
        if (formula.startsWith("=")) {
            formula = formula.substring(1);
        }
        formula = formula.replaceAll("\\s+", "");

        // Extract cell references using a regex pattern
        String regex = "([A-Z]+)(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            String column = matcher.group(1);
            int row = Integer.parseInt(matcher.group(2)) - 1; // Convert 1-based to 0-based index
            int x = column.charAt(0) - 'A'; // Convert column letter to 0-based index
            dependencies.add(new CellEntry(x, row));
        }

        return dependencies;
    }

    public Coordinate parseCell(String cellName) {
        int xCord = cell2Num(cellName.charAt(0));
        String optionalNumber = cellName.substring(1);
        int yCord = Integer.parseInt(optionalNumber);
        return new Coordinate(xCord, yCord);
    }

    public static int cell2Num(char Cell) {
        return Cell - 'A';
    }

    public static int char2Int(char letter) {
        if (letter >= '0' && letter <= '9') {
            return Character.getNumericValue(letter);
        }
        return -1;

    }

    public int valueOf(String s) {
        return Integer.parseInt(s);
    }
}
