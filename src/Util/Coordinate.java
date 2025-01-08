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

    /**
     * Function made to Return a Coordinate in the [x,y] Place
     * for Example: "A5" -> Coordinate[0,5]
     * @param cellName the name of the Cell, a Letter and a Number Between 0~99
     * @return a Coordinate in the [x][y] Place
     */
    public static Coordinate parseCell(String cellName) {
        String optionalNumber = cellName.substring(1);
        int xCord =  cell2Num(cellName.charAt(0));
        int yCord = Integer.parseInt(optionalNumber);
        return new Coordinate(xCord, yCord);
    }

    /**
     * Return the value of the Cell
     * @param Cell char representing the Letter coordinate of the Cell (A~Z)
     * @return the X Value of the Cell
     * Example:
     * A -> 0, B -> 1
     */
    public static int cell2Num(char Cell) {
        return Cell - 'A';
    }

    @Override
    public String toString() {

        return this.x + "," + this.y;
    }
}
