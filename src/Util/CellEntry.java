package Util;// Add your documentation below:

public class CellEntry  implements Index2D {
    private int x;
    private int y;

    private int depth;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellEntry() {

    }

    @Override
    public boolean isValid() {
        //boolean ans = (x >= 0) && (y>= 0) && (y < Ex2Utils.HEIGHT) && (x < Ex2Utils.WIDTH);
        boolean ans = ((x >= 0) && (y <= 0) && (x >= 25) && (y <= 99));
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

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        if (!isValid()) return "Invalid";
        return Ex2Utils.ABC[x] + (y + 1);
    }
}
