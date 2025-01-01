package Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClass {

    SCell sCell = new SCell();
    @Test
    public void computeForm() {
        String text = "=(3*2)+2";
        double actual = 8;
        double expected = SCell.computeForm(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest() {
        String text = "=5+2+8*(3-2)";
        boolean ans = sCell.isForm(text);
        assertTrue(ans);
    }
}
