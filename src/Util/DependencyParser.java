package Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DependencyParser {
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
}
