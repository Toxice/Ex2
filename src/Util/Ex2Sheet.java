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

//    @Override
//    public String value(int x, int y) {
//        String ans = Ex2Utils.EMPTY_CELL; // Default value for an empty cell
//
//        // Add your code here
//        if (!isIn(x, y)) {
//            return ans; // Return default if coordinates are invalid
//        }
//
//        Cell c = get(x, y);
//        if (c != null) {
//            // Handle different cell types
//            switch (c.getType()) {
//                case Ex2Utils.NUMBER:
//                case Ex2Utils.TEXT:
//                    ans = c.getData();
//                    break;
//                case Ex2Utils.FORM:
//                    try {
//                        double result = SCell.computeForm(c.getData());
//                        ans = String.valueOf(result);
//                    } catch (Exception e) {
//                        c.setType(Ex2Utils.ERR_FORM_FORMAT);
//                        ans = Ex2Utils.ERR_FORM;
//                    }
//                    break;
//                case Ex2Utils.ERR_FORM_FORMAT:
//                    ans = Ex2Utils.ERR_FORM;
//                    break;
//                case Ex2Utils.ERR_CYCLE_FORM:
//                    ans = Ex2Utils.ERR_CYCLE;
//                    break;
//            }
//        }
//
//        /////////////////////
//        return ans; // Return the computed or default value
//    }

//    @Override
//    public String value(int x, int y){
//        String ans = Ex2Utils.EMPTY_CELL;
//        ans = eval(x, y);
//        return ans;
//    }

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
//    @Override
//    public void set(int x, int y, String s) {
//      //  Cell c = new SCell(s);
//       // table[x][y] = c;
//        // Add your code here
//        if (!isIn(x, y)) {
//            throw new IllegalArgumentException("Invalid cell coordinates");
//            ////////////////////
//        }
////        c.setData(s); // try
//        table[x][y] = new SCell(s, this);  // Pass 'this' as the sheet reference
//       // eval();  // Re-evaluate the sheet after setting a new value
//        eval();
//    }

    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }

        table[x][y] = new SCell(s, this);
        // Calculate depth and check for cycles
        int[][] depths = depth();
        if (depths[x][y] == Ex2Utils.ERR) {
            table[x][y].setType(Ex2Utils.ERR_CYCLE_FORM);
        }
        eval();
    }
//    @Override
//    public void eval() {
//        int[][] dd = depth();
//        // Add your code here
//
//        // ///////////////////
//    }

//    @Override
//    public void eval() {
//        int[][] dd = depth();
//
//        // Create a list of cells ordered by their depth
//        List<Cell[][]> depthLevels = new ArrayList<>();
//        int maxDepth = 0;
//
//        // Find maximum depth
//        for (int x = 0; x < width(); x++) {
//            for (int y = 0; y < height(); y++) {
//                if (dd[x][y] > maxDepth) {
//                    maxDepth = dd[x][y];
//                }
//            }
//        }
//
//        // Initialize depth levels array
//        for (int i = 0; i <= maxDepth; i++) {
//            depthLevels.add(new Cell[width()][height()]);
//        }
//
//        // Sort cells by their depth and also track error cells
//        Set<String> updatedCells = new HashSet<>();
//        List<CellEntry> errorDependents = new ArrayList<>();
//
//        for (int x = 0; x < width(); x++) {
//            for (int y = 0; y < height(); y++) {
//                Cell cell = get(x, y);
//                if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT || cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
//                    // Find all cells that depend on this error cell
//                    findDependentCells(x, y, errorDependents);
//                }
//                else if (dd[x][y] >= 0 && cell.getType() == Ex2Utils.FORM) {
//                    depthLevels.get(dd[x][y])[x][y] = cell;
//                }
//            }
//        }
//
//        // First handle cells with errors
//        for (CellEntry dep : errorDependents) {
//            eval(dep.getX(), dep.getY());
//            updatedCells.add(dep.getX() + "," + dep.getY());
//        }
//
//        // Then evaluate other cells in order of dependency depth
//        for (int d = 0; d <= maxDepth; d++) {
//            Cell[][] level = depthLevels.get(d);
//            for (int x = 0; x < width(); x++) {
//                for (int y = 0; y < height(); y++) {
//                    String cellKey = x + "," + y;
//                    if (level[x][y] != null && !updatedCells.contains(cellKey)) {
//                        eval(x, y);
//                    }
//                }
//            }
//        }
//    }

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

    private void propagateError(int x, int y) {
        List<CellEntry> dependents = new ArrayList<>();
        findDependentCells(x, y, dependents);
        for (CellEntry dep : dependents) {
            Cell cell = get(dep.getX(), dep.getY());
            cell.setType(Ex2Utils.ERR_FORM_FORMAT);
        }
    }

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

//    @Override
//    public int[][] depth() {
//        int[][] ans = new int[width()][height()];
//        // Add your code here
//
//        // ///////////////////
//        return ans;
//    }

//    @Override
//    public int[][] depth() {
//        int[][] depths = new int[width()][height()]; // Initialize the depth array
//
//        for (int x = 0; x < width(); x++) {
//            for (int y = 0; y < height(); y++) {
//                depths[x][y] = calculateDepth(x, y, new boolean[width()][height()]); // Track visited cells to prevent cycles
//            }
//        }
//
//        return depths;
//    }

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

//    /**
//     * Recursively calculates the depth of a cell.
//     *
//     * @param x        The x-coordinate of the cell.
//     * @param y        The y-coordinate of the cell.
//     * @param visited  A 2D boolean array to track visited cells (to detect cycles).
//     * @return The depth of the cell or -1 if a circular dependency is detected.
//     */
//    private int calculateDepth(int x, int y, boolean[][] visited) {
//        if (!isIn(x, y)) {
//            return 0; // Return 0 for invalid cells
//        }
//
//        Cell cell = get(x, y);
//        if (cell == null || cell.getType() != Ex2Utils.FORM) {
//            return 0; // Non-form cells have a depth of 0
//        }
//
//        if (visited[x][y]) {
//            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Mark as a circular dependency
//            return -1; // Return -1 for circular dependencies
//        }
//
//        visited[x][y] = true; // Mark the cell as visited
//
//        List<CellEntry> dependencies = DependencyParser.parseDependencies(cell.getData()); // Parse dependencies
//        int maxDepth = 0;
//        for (CellEntry dep : dependencies) {
//            int depDepth = calculateDepth(dep.getX(), dep.getY(), visited); // Recursive depth calculation
//            if (depDepth == -1) {
//                return -1; // Propagate circular dependency detection
//            }
//            maxDepth = Math.max(maxDepth, depDepth);
//        }
//
//        visited[x][y] = false; // Unmark the cell after processing
//        return maxDepth + 1; // Depth is 1 + max depth of dependencies
//    }

    private int calculateDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) {
            return 0;
        }

        Cell cell = get(x, y);
        // If not a formula or empty cell, depth is 0
        if (cell == null || cell.getData().isEmpty() || cell.getType() != Ex2Utils.FORM) {
            return 0;
        }

        // If we've seen this cell before in current path, it's a cycle
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




//    @Override
//    public void load(String fileName) throws IOException {
//        // Add your code here
//
//        /////////////////////
//    }

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

//    @Override
//    public String eval(int x, int y) {
//        String ans = null; // Initialize as null
//        SCell cell1 = (SCell) get(x,y); //
//
//        if (get(x, y) != null) {
//           // ans = get(x, y).toString(); // Retrieve the string representation of the cell
//            ans = cell1.toString();
//        }
//
//        // Add your code here
//        if (!isIn(x, y)) {
//            return Ex2Utils.EMPTY_CELL; // Return empty cell if coordinates are invalid
//        }
//
//        SCell cell = (SCell) get(x, y);
//        if (cell != null) {
//            switch (cell.getType()) {
//                case Ex2Utils.NUMBER:
//                case Ex2Utils.TEXT:
//                    ans = cell.getData(); // Directly return the data for text or number
//                    break;
//                case Ex2Utils.FORM:
//                    try {
//                        double result = cell.computeForm(cell.getData());
//                        ans = String.valueOf(result); // Evaluate the formula and convert to string
//                    } catch (Exception e) {
//                        cell.setType(Ex2Utils.ERR_FORM_FORMAT); // Handle formula errors
//                        ans = Ex2Utils.ERR_FORM;
//                    }
//                    break;
//                case Ex2Utils.ERR_FORM_FORMAT:
//                    ans = Ex2Utils.ERR_FORM; // Return error message for wrong formula format
//                    break;
//                case Ex2Utils.ERR_CYCLE_FORM:
//                    ans = Ex2Utils.ERR_CYCLE; // Return error message for circular dependency
//                    break;
//            }
//        }
//
//        /////////////////////
//        return ans; // Return the evaluated or default result
//    }


//    @Override
//    public String eval(int x, int y) {
//        if (!isIn(x, y)) {
//            return Ex2Utils.EMPTY_CELL;
//        }
//
//        SCell cell = (SCell) get(x, y);
//        if (cell == null) {
//            return Ex2Utils.EMPTY_CELL;
//        }
//
//        switch (cell.getType()) {
//            case Ex2Utils.NUMBER:
//            case Ex2Utils.TEXT:
//                return cell.getData();
//            case Ex2Utils.FORM:
//                try {
//                    double result = cell.computeForm(cell.getData());
//                    return String.valueOf(result);
//                } catch (Exception e) {
//                    cell.setType(Ex2Utils.ERR_FORM_FORMAT);
//                    return Ex2Utils.ERR_FORM;
//                }
//            default:
//                return Ex2Utils.EMPTY_CELL;
//        }
//    }


//    @Override
//    public String eval(int x, int y) {
//        SCell cell = (SCell) get(x,y);  // Get cell A1
//
//        if (cell.getType() == Ex2Utils.FORM) {
//            String formula = cell.getData();  // Gets "=A0"
//            try {
//                double result = cell.computeForm(formula);  // This should:
//                // 1. Parse "=A0" to know it needs value from [0,0]
//                // 2. Get value from cell [0,0] (which is 5)
//                // 3. Return 5.0
//                return String.valueOf(result);
//            } catch (Exception e) {
//                return Ex2Utils.ERR_FORM;
//            }
//        }
//        return cell.getData();
//    }

//    @Override
//    public String eval(int x, int y) {
//        SCell cell = (SCell) get(x,y);
//
//        if (cell.getType() == Ex2Utils.FORM) {
//            String formula = cell.getData();
//            try {
//                // First validate if the formula is valid
//                if (!SCell.isForm(formula) && !SCell.isCoordinate(formula.substring(1))) {
//                    cell.setType(Ex2Utils.ERR_FORM_FORMAT);
//                    return Ex2Utils.ERR_FORM;
//                }
//
//                double result = cell.computeForm(formula);
//                return String.valueOf(result);
//            } catch (Exception e) {
//                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
//                return Ex2Utils.ERR_FORM;
//            }
//        }
//        return cell.getData();
//    }

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


//    @Override
//    public String value(int x, int y) {
//        String ans = Ex2Utils.EMPTY_CELL; // Default value for an empty cell
//
//        if (!isIn(x, y)) {
//            return ans; // Return default if coordinates are invalid
//        }
//
//        Cell c = get(x, y);
//        if (c != null) {
//            switch (c.getType()) {
//                case Ex2Utils.NUMBER:
//                case Ex2Utils.TEXT:
//                    ans = c.getData();
//                    break;
//                case Ex2Utils.FORM:
//                    try {
//                        String formula = c.getData().substring(1); // Remove '='
//                        List<CellEntry> dependencies = DependencyParser.parseDependencies(formula);
//
//                        // Replace references with actual values
//                        for (CellEntry dep : dependencies) {
//                            Cell referencedCell = get(dep.getX(), dep.getY());
//                            if (referencedCell == null || referencedCell.getData().isEmpty()) {
//                                throw new IllegalArgumentException("Referenced cell is empty or invalid: " + dep);
//                            }
//                            formula = formula.replace(dep.toString(), referencedCell.getData());
//                        }
//
//                        double result = SCell.computeForm(formula); // Now pass the preprocessed formula
//                        ans = String.valueOf(result);
//                    } catch (Exception e) {
//                        c.setType(Ex2Utils.ERR_FORM_FORMAT); // Handle formula errors
//                        ans = Ex2Utils.ERR_FORM;
//                    }
//                    break;
//                case Ex2Utils.ERR_FORM_FORMAT:
//                    ans = Ex2Utils.ERR_FORM;
//                    break;
//                case Ex2Utils.ERR_CYCLE_FORM:
//                    ans = Ex2Utils.ERR_CYCLE;
//                    break;
//            }
//        }
//
//        return ans; // Return the computed or default value
//    }

//    @Override
//    public String value(int x, int y) {
//        String ans = Ex2Utils.EMPTY_CELL;
//        // Add your code here
//
//        Cell c = get(x,y);
//        if(c!=null) {ans = c.toString();}
//
//        /////////////////////
//        return ans;
//    }

}
