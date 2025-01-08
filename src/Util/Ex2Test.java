package Util;

import org.junit.jupiter.api.Test;
import static Util.SCell.*;
import static org.junit.jupiter.api.Assertions.*;

public class Ex2Test {
    /**
     * Validates the isForm Function
     * true iff the given String is valid number
     */
    @Test
    public void isNumberTest() {
        SCell sCell = new SCell();
        String[] numbers = {"1234", "5748524", "215495645", "7894566123", "111111111", "45464"};
        for (int i = 0; i < numbers.length; i++) {
            boolean data = sCell.isNumber(numbers[i]);
            assertTrue(data);
        }
    }

    /**
     * true iff the given String is not number
     */
    @Test
    public void isNotNumberTest() {
        SCell sCell = new SCell();
        String[] numbers = {"dsdsdsd", "=6458455", "2154a95645", "789gffgf4566123", "111111ttrr111", "=45464"};
        for (int i = 0; i < numbers.length; i++) {
            boolean data = sCell.isNumber(numbers[i]);
            assertFalse(data);
        }
    }

    /**
     * true iff the given String is a Text
     */
    @Test
    public void isTextTest() {
        String[] texts = {"agbc", "dgdfdfdgdfdsfte", "yruiffhdhfusdhsdhsfhdu", "fdfdfdjfhj!@$%^", "hfjdfhhdfhsidjsdsurefjdhdjfhfj", "fgdjfdjdjf473643784"};
        for (int i = 0; i < texts.length; i++) {
            boolean data = SCell.isText(texts[i]);
            assertTrue(data);
        }
    }

    /**
     * true iff the given String is not a Text
     */
    @Test
    public void isNotTextTest() {
        String[] texts = {"=agbc", "=dgdfdfdgdfdsfte", "5525", "=fdfdfdjfhj!@$%^", "=hfjdfhhdfhsidjsdsurefjdhdjfhfj", "=A0"};
        for (int i = 0; i < texts.length; i++) {
            boolean data = SCell.isText(texts[i]);
            assertFalse(data);
        }
    }

    /**
     * true iff the String after trying to remove the sapces is equal to "expected" String
     */
    @Test
    public void removeSpaceTest() {
        SCell sCell = new SCell();
        String text = "1 + 2 +    3";
        String expected = "1+2+3";
        String actual = sCell.removeSpace(text);
        assertEquals(expected, actual);
    }

    /**
     * true iff the given String is a valid formula
     */
    @Test
    public void isFormTest() {
        SCell sCell = new SCell();
        String[] valid = {"=(3+3) + (5+2)", "=(1*8)+5", "=4+7/8*88", "=A0", "=85+A5+B75"};
        for (int i = 0; i < valid.length; i++) {
            assertTrue(sCell.isForm(valid[i]));
        }
    }

    /**
     * true iff the given String is computable
     */
    @Test
    public void computeFormTest() {
        SCell cell = new SCell();
        String[] forms = {"=(3*2)+2", "=5+50", "=6*(1+2)*2", "1+1+1+1+1+(2+8)+(4*3)"};
        double[] expected = {8, 55, 36, 27};
        for (int i = 0; i < forms.length; i++) {
            double actual = cell.computeForm(forms[i]);
            assertEquals(expected[i], actual);
        }
    }

    /**
     * Set the value of all the Cells inside the Sheet Table to 0,1,2,...,20 Accordingly
     * Set the value of each Cell to the value of Corresponding Cell in the Sheet Table
     * true iff all the values of each Cell is equals to the value of each Corresponding Cell inside the Sheet Table
     */
    @Test
    public void computeForm_CellTest() {
        Ex2Sheet sheet = new Ex2Sheet(20, 20);
        SCell[] cells = new SCell[20];
        int[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};

        for (int i = 0; i < expected.length; i++) {
            sheet.set(0, i, String.valueOf(i));
            cells[i] = (SCell) sheet.get(0, i); // Cell A0 ~ A19
        }

        for (int j = 0; j < expected.length; j++) {
            assertEquals(String.valueOf(expected[j]), cells[j].getData());
        }
    }

    /**
     * true iff the given String a valid Coordinate
     * Example:
     * A0 - True
     * B55 - True
     * C100 - False
     */
    @Test
    public void isCoordinate_Pass_Test() {
        String coordiantes[] = {"A0", "A1", "G5", "Z99"};
        for (int i = 0; i < coordiantes.length; i++) {
            assertTrue(isCoordinate(coordiantes[i]));
        }
    }

    /**
     * true iff each String is NOT a Coordinate
     * Example:
     * B555- False
     * C7777777 - False
     * B - False
     */
    @Test
    public void isCoordiantes_Fail_Test() {
        String[] cords = {"A100", "A", "B", "G455"};
        for (int i = 0; i < cords.length; i++) {
            assertFalse(isCoordinate(cords[i]));
        }
    }

    /**
     * Tests the eval(int x, int y) function
     * true iff every cell value is computable by eval(int x, int y)
     */
    @Test
    public void evalTest() {
        int value = 20;
        Ex2Sheet sheet = new Ex2Sheet(value, value);
        for (int i = 0; i < value; i++) {
            sheet.set(0, i, String.valueOf(i));
        }
        String[] expected = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19"};
        for (int i = 0; i < value; i++) {
            assertEquals(expected[i], sheet.eval(0, i));
        }
    }

    /**
     * Tests the Ability of computeForm to compute when a Cell Reference is Given
     * true iff the expected value is equal to the computable value
     */
    @Test
    public void scanFormulaTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        SCell[] cells = new SCell[2];
        cells[0] = new SCell("5", sheet);
        cells[1] = new SCell("=A0", sheet);

        double expected = 6;

        sheet.set(0, 0, "=5");
        sheet.set(0, 1, cells[1].getData());

        assertEquals(expected, cells[1].computeForm("=1+A0"));
    }

    /**
     * Tests the Order Functionality
     */
    @Test
    public void testBasicOrder() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "text");
        assertEquals(0, sheet.get(0, 0).getOrder());
        assertEquals(0, sheet.get(0, 1).getOrder());
        sheet.set(1, 0, "=A0");
        assertEquals(1, sheet.get(1, 0).getOrder());
    }

    /**
     * Tests the Depth
     */
    @Test
    public void testComplexOrder() {
        Ex2Sheet sheet = new Ex2Sheet();
        // Setup test data
        sheet.set(0, 0, "10");
        sheet.set(0, 1, "20");
        sheet.set(1, 0, "=A0+A1");
        sheet.set(1, 1, "=B0*2");
        sheet.set(2, 0, "=(B0+B1)/2");

        // Test orders
        assertEquals(0, sheet.get(0, 0).getOrder());
        assertEquals(0, sheet.get(0, 1).getOrder());
        assertEquals(1, sheet.get(1, 0).getOrder());
        assertEquals(1, sheet.get(1, 1).getOrder());
        assertEquals(2, sheet.get(2, 0).getOrder());
    }

    /**
     * Tests for the ERR_CYCLE = "ERR_CYCLE!", ERR_FORM = "ERR_FORM!" Messages
     */
    @Test
    public void Error_Messages_Test() {
        Ex2Sheet sheet = new Ex2Sheet();
        String[] ERRS = {Ex2Utils.ERR_CYCLE, Ex2Utils.ERR_FORM};
        sheet.set(0,0,"=A0");
        sheet.set(0,1,"=NOT_A_VALID_FORM");
        for (int i = 0; i < 2; i++) {
            assertEquals(ERRS[i], sheet.eval(0, i));
        }
    }
}


