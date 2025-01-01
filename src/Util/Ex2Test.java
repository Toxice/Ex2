package Util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Ex2Test {

    CellEntry cellEntry = new CellEntry();
    @Test
    public void isNumberTest() {
        String text = "1234";
        boolean data = cellEntry.isNumber(text);
        assertTrue(data);
    }

    @Test
    public void isTextTest() {
        String text = "agbc";
        boolean data = cellEntry.isText(text);
        assertTrue(data);
    }

    @Test
    public void removeSpaceTest() {
        String text = "1 + 2 +    3";
        String expected = "1+2+3";
        String actual = cellEntry.removeSpace(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest() {
        String[] valid = {"=(3+3) + (5+2)", "=88+5", "=5+4", "=55-4"};
        for (int i = 0; i < valid.length; i++) {
            assertTrue(cellEntry.isForm(valid[i]));
        }
    }

    @Test
    public void isFormInvalidTest() {
        String[] invalid = {"=(3(*5", "=4+++", "=()", "=88+-()"};
        for (int i = 0; i < invalid.length; i++) {
            assertFalse(cellEntry.isForm(invalid[i]));
        }
    }

    @Test
    public void computeFormTest() {
        String text = "=1+(3*3)";
        double expected = 10;
        double actual = cellEntry.computeForm(text);
        assertEquals(expected, actual);
    }
}
