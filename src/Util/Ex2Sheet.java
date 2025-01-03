package Util;

import java.io.IOException;
import java.util.List;
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

    @Override
    public String value(int x, int y){
        String ans = Ex2Utils.EMPTY_CELL;
        ans = eval(x, y);
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
        Cell c = new SCell(s);
        table[x][y] = c;
        // Add your code here
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates");
            ////////////////////
        }
        c.setData(s); // try
        eval();
    }
    @Override
    public void eval() {
        int[][] dd = depth();
        // Add your code here

        // ///////////////////
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

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()]; // Initialize the depth array

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depths[x][y] = calculateDepth(x, y, new boolean[width()][height()]); // Track visited cells to prevent cycles
            }
        }

        return depths;
    }

    /**
     * Recursively calculates the depth of a cell.
     *
     * @param x        The x-coordinate of the cell.
     * @param y        The y-coordinate of the cell.
     * @param visited  A 2D boolean array to track visited cells (to detect cycles).
     * @return The depth of the cell or -1 if a circular dependency is detected.
     */
    private int calculateDepth(int x, int y, boolean[][] visited) {
        if (!isIn(x, y)) {
            return 0; // Return 0 for invalid cells
        }

        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.FORM) {
            return 0; // Non-form cells have a depth of 0
        }

        if (visited[x][y]) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Mark as a circular dependency
            return -1; // Return -1 for circular dependencies
        }

        visited[x][y] = true; // Mark the cell as visited

        List<CellEntry> dependencies = DependencyParser.parseDependencies(cell.getData()); // Parse dependencies
        int maxDepth = 0;
        for (CellEntry dep : dependencies) {
            int depDepth = calculateDepth(dep.getX(), dep.getY(), visited); // Recursive depth calculation
            if (depDepth == -1) {
                return -1; // Propagate circular dependency detection
            }
            maxDepth = Math.max(maxDepth, depDepth);
        }

        visited[x][y] = false; // Unmark the cell after processing
        return maxDepth + 1; // Depth is 1 + max depth of dependencies
    }




    @Override
    public void load(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = null; // Initialize as null

        if (get(x, y) != null) {
            ans = get(x, y).toString(); // Retrieve the string representation of the cell
        }

        // Add your code here
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL; // Return empty cell if coordinates are invalid
        }

        SCell cell = (SCell) get(x, y);
        if (cell != null) {
            switch (cell.getType()) {
                case Ex2Utils.NUMBER:
                case Ex2Utils.TEXT:
                    ans = cell.getData(); // Directly return the data for text or number
                    break;
                case Ex2Utils.FORM:
                    try {
                        double result = cell.computeForm(cell.getData());
                        ans = String.valueOf(result); // Evaluate the formula and convert to string
                    } catch (Exception e) {
                        cell.setType(Ex2Utils.ERR_FORM_FORMAT); // Handle formula errors
                        ans = Ex2Utils.ERR_FORM;
                    }
                    break;
                case Ex2Utils.ERR_FORM_FORMAT:
                    ans = Ex2Utils.ERR_FORM; // Return error message for wrong formula format
                    break;
                case Ex2Utils.ERR_CYCLE_FORM:
                    ans = Ex2Utils.ERR_CYCLE; // Return error message for circular dependency
                    break;
            }
        }

        /////////////////////
        return ans; // Return the evaluated or default result
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
