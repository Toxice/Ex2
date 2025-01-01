package Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClass {

    SCell sCell = new SCell();

    @Test
    public void isNumberTest() {
        String text = "1234";
        boolean data = sCell.isNumber(text);
        assertTrue(data);
    }

    @Test
    public void isTextTest() {
        String text = "agbc";
        boolean data = SCell.isText(text);
        assertTrue(data);
    }

    @Test
    public void removeSpaceTest() {
        String text = "1 + 2 +    3";
        String expected = "1+2+3";
        String actual = sCell.removeSpace(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest() {
        String[] valid = {"=(3+3) + (5+2)", "=88+5", "=5+4", "=55-4"};
        for (int i = 0; i < valid.length; i++) {
            assertTrue(sCell.isForm(valid[i]));
        }
    }
    @Test
    public void computeForm() {
        String text = "=(3*2)+2";
        double actual = 8;
        double expected = SCell.computeForm(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest2() {
        String text = "=5+2+8*(3-2)";
        boolean ans = sCell.isForm(text);
        assertTrue(ans);
    }

    @Test
    public void valueTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        String text = "=A1";
        String actual = sheet.value(1,2);
        System.out.println(actual);
        System.out.println();
    }
}
