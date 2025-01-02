package Util;

/**
 * Represents Coordinates for Cells inside a Table
 */
public class Coordinate {
    public int x;
    public int y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static Coordinate parseCell(String cellName) {
        int xCord = cell2Num(cellName.charAt(0));
        String optionalNumber = cellName.substring(1);
        int yCord = Integer.parseInt(optionalNumber);
        return new Coordinate(xCord, yCord);
    }

    public static int cell2Num(char Cell) {
        return Cell - 'A';
    }
}
