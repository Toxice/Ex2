package Util;

import java.io.IOException;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL; // Default value for an empty cell

        // Add your code here
        if (!isIn(x, y)) {
            return ans; // Return default if coordinates are invalid
        }

        Cell c = get(x, y);
        if (c != null) {
            // Handle different cell types
            switch (c.getType()) {
                case Ex2Utils.NUMBER:
                case Ex2Utils.TEXT:
                    ans = c.getData();
                    break;
                case Ex2Utils.FORM:
                    try {
                        double result = SCell.computeForm(c.getData());
                        ans = String.valueOf(result);
                    } catch (Exception e) {
                        c.setType(Ex2Utils.ERR_FORM_FORMAT);
                        ans = Ex2Utils.ERR_FORM;
                    }
                    break;
                case Ex2Utils.ERR_FORM_FORMAT:
                    ans = Ex2Utils.ERR_FORM;
                    break;
                case Ex2Utils.ERR_CYCLE_FORM:
                    ans = Ex2Utils.ERR_CYCLE;
                    break;
            }
        }

        /////////////////////
        return ans; // Return the computed or default value
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

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        // Add your code here

        // ///////////////////
        return ans;
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
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        // Add your code here

        /////////////////////
        return ans;
        }
}
