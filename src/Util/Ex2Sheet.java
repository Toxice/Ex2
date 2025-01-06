package Util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("", this);
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell cell = get(x, y);

        if (cell != null) {
            if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                return Ex2Utils.ERR_FORM;
            }
            ans = eval(x, y);
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        // Add your code here
        CellEntry entry = parseCellEntry(cords);
        if (entry != null && entry.isValid()) {
            ans = get(entry.getX(), entry.getY());
        }
        /////////////////////
        return ans;
    }

    private CellEntry parseCellEntry(String coords) {
        coords = coords.trim().toUpperCase();
        if (coords.length() < 2) return null;

        char col = coords.charAt(0);
        int x = col - 'A';
        int y;
        try {
            y = Integer.parseInt(coords.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        return new CellEntry(x, y);
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        // First check for direct self-reference
        if (s.startsWith("=")) {
            String cellRef = String.format("%s%d", Ex2Utils.ABC[x], y + 1);
            if (s.substring(1).trim().equals(cellRef)) {
                SCell newCell = new SCell(s, this);
                newCell.setType(Ex2Utils.ERR_CYCLE_FORM);
                table[x][y] = newCell;
                return;
            }
        }

        table[x][y] = new SCell(s, this);
        int[][] depths = depth();
        if (depths[x][y] == Ex2Utils.ERR) {
            table[x][y].setType(Ex2Utils.ERR_CYCLE_FORM);
        }
        eval();
    }

    @Override
    public void eval() {
        int[][] dd = depth();
        int maxDepth = 0;

        // First pass: Find maximum depth and reset error states
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (dd[x][y] > maxDepth) {
                    maxDepth = dd[x][y];
                }
                Cell cell = get(x, y);
                // Reset error states to allow for re-evaluation
                if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                    cell.setType(Ex2Utils.FORM);
                }
            }
        }

        // Create an array to store cells at each depth level
        List<List<CellEntry>> depthLevels = new ArrayList<>();
        for (int i = 0; i <= maxDepth; i++) {
            depthLevels.add(new ArrayList<>());
        }

        // Second pass: Categorize cells by depth
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                if (cell.getType() == Ex2Utils.FORM && dd[x][y] >= 0) {
                    depthLevels.get(dd[x][y]).add(new CellEntry(x, y));
                }
            }
        }

        // Third pass: Evaluate cells in order of dependency depth
        Set<String> evaluatedCells = new HashSet<>();

        // Start with depth 0 (cells with no dependencies)
        for (int depth = 0; depth <= maxDepth; depth++) {
            for (CellEntry entry : depthLevels.get(depth)) {
                String cellKey = entry.getX() + "," + entry.getY();
                if (!evaluatedCells.contains(cellKey)) {
                    try {
                        // Evaluate the cell
                        String result = eval(entry.getX(), entry.getY());
                        // If evaluation fails, mark as error and propagate
                        if (result.equals(Ex2Utils.ERR_FORM)) {
                            Cell cell = get(entry.getX(), entry.getY());
                            cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                            propagateError(entry.getX(), entry.getY());
                        }
                    } catch (Exception e) {
                        // Handle any evaluation errors
                        Cell cell = get(entry.getX(), entry.getY());
                        cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                        propagateError(entry.getX(), entry.getY());
                    }
                    evaluatedCells.add(cellKey);
                }
            }
        }
    }

    /**
     * Propagates error states to all dependent cells when an error is encountered.
     * This ensures that if a cell has an error, all cells that depend on it will also
     * show an error state.
     *
     * @param x x-coordinate of the error cell
     * @param y y-coordinate of the error cell
     */
    private void propagateError(int x, int y) {
        List<CellEntry> dependents = new ArrayList<>();
        findDependentCells(x, y, dependents);
        for (CellEntry dep : dependents) {
            Cell cell = get(dep.getX(), dep.getY());
            cell.setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

    /**
     * Finds all cells that directly or indirectly depend on the cell at the given coordinates.
     * This is used for error propagation and cycle detection.
     *
     * @param x x-coordinate of the cell
     * @param y y-coordinate of the cell
     * @param dependents List to be populated with the dependent cells
     */
    private void findDependentCells(int x, int y, List<CellEntry> dependents) {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                Cell cell = get(i, j);
                if (cell != null && cell.getType() == Ex2Utils.FORM) {
                    List<CellEntry> deps = DependencyParser.parseDependencies(cell.getData());
                    for (CellEntry dep : deps) {
                        if (dep.getX() == x && dep.getY() == y) {
                            dependents.add(new CellEntry(i, j));
                            findDependentCells(i, j, dependents); // Recursive call for transitive dependencies
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        // Add your code here
        if (!(xx <= 25 && yy <= 99)) {
            ans = false;
        }
        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()]; // Initialize the depth array
        boolean[][] visited = new boolean[width()][height()]; // Track visited cells globally

        // First, mark all cells as unvisited
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                // First reset any previous cycle errors
                if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
                    cell.setType(Ex2Utils.FORM);
                }
            }
        }

        // Calculate depths for all cells
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = calculateDepth(x, y, visited);
            }
        }

        return depths;
    }

    private int calculateDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) {
            return 0;
        }

        Cell cell = get(x, y);
        // If not a formula or empty cell, depth is 0
        if (cell == null || cell.getData().isEmpty() || cell.getType() != Ex2Utils.FORM) {
            return 0;
        }

        // Check for self-reference first
        String cellData = cell.getData();
        String cellRef = String.format("%s%d", Ex2Utils.ABC[x], y + 1);
        if (cellData.startsWith("=") && cellData.substring(1).trim().equals(cellRef)) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }

        // Check for existing dependencies in the current path
        if (visited[x][y]) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }

        visited[x][y] = true; // Mark as visited

        try {
            List<CellEntry> dependencies = DependencyParser.parseDependencies(cell.getData());
            int maxDepth = 0;

            for (CellEntry dep : dependencies) {
                if (!dep.isValid()) continue;

                // Check if dependency is a self-reference
                if (dep.getX() == x && dep.getY() == y) {
                    cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    return Ex2Utils.ERR;
                }

                int depDepth = calculateDepth(dep.getX(), dep.getY(), visited);
                if (depDepth == Ex2Utils.ERR) {
                    // Propagate cycle error
                    cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    return Ex2Utils.ERR;
                }
                maxDepth = Math.max(maxDepth, depDepth);
            }

            // Depth is 1 + max depth of dependencies
            return maxDepth + 1;
        } finally {
            visited[x][y] = false; // Unmark before returning
        }
    }

    @Override
    public void load(String fileName) throws IOException {
        // Clear existing data by creating new empty cells
        table = new SCell[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                table[i][j] = new SCell("", this);
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Skip header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // Split line by comma, but limit to 3 parts to handle optional remarks
                String[] parts = line.split(",", 3);

                // Verify line format
                if (parts.length < 3) {
                    continue; // Skip invalid lines
                }

                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    String data = parts[2].trim();

                    // Verify coordinates are valid
                    if (isIn(x, y)) {
                        set(x, y, data);
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid number format
                    continue;
                }
            }
        }

        // Re-evaluate the entire sheet after loading
        eval();
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write header
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");

            // Iterate through all cells
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x, y);
                    // Only save non-empty cells
                    if (cell != null && !cell.getData().isEmpty()) {
                        // Format: x,y,cellData
                        writer.write(String.format("%d,%d,%s\n", x, y, cell.getData()));
                    }
                }
            }
        }
        /////////////////////
    }

    /**
     * Evaluates a spreadsheet cell and returns its computed value.
     * If the cell contains a formula, it will be evaluated according to the following rules:
     * - Simple numbers are returned as is
     * - References to other cells (e.g., "A1") are evaluated recursively
     * - Formulas (e.g., "=A1+5") are computed with operator precedence
     * - Error checking is performed for invalid formulas and circular references
     *
     * @param x integer, x-coordinate of the cell
     * @param y integer, y-coordinate of the cell
     * @return The computed value as a String, or ERR_FORM/ERR_CYCLE in case of errors
     */
    @Override
    public String eval(int x, int y) {
        SCell cell = (SCell) get(x,y);

        // First check for error type
        if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
            return Ex2Utils.ERR_FORM;
        }

        if (cell.getType() == Ex2Utils.FORM) {
            String formula = cell.getData();
            try {
                double result = cell.computeForm(formula);
                return String.valueOf(result);
            } catch (Exception e) {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            }
        }
        return cell.getData();
    }

}
