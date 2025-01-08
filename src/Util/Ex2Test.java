package Util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static Util.SCell.*;
import static org.junit.jupiter.api.Assertions.*;

public class Ex2Test {
    /**
     * Validates the isForm Function
     * true iff the given String
     */
    @Test
    public void isNumberTest() {
        SCell sCell = new SCell();
        String[] numbers = {"1234", "5748524", "215495645", "7894566123","111111111", "45464"};
        for (int i = 0; i < numbers.length; i++) {
            boolean data = sCell.isNumber(numbers[i]);
            assertTrue(data);
        }
    }

    @Test
    public void isNotNumberTest() {
        SCell sCell = new SCell();
        String[] numbers = {"dsdsdsd", "=6458455", "2154a95645", "789gffgf4566123", "111111ttrr111", "=45464"};
        for (int i = 0; i < numbers.length; i++) {
            boolean data = sCell.isNumber(numbers[i]);
            assertFalse(data);
        }
    }

    @Test
    public void isTextTest() {
        String[] texts = {"agbc", "dgdfdfdgdfdsfte", "yruiffhdhfusdhsdhsfhdu", "fdfdfdjfhj!@$%^", "hfjdfhhdfhsidjsdsurefjdhdjfhfj", "fgdjfdjdjf473643784"};
        for (int i = 0; i < texts.length; i++) {
            boolean data = SCell.isText(texts[i]);
            assertTrue(data);
        }
    }

    @Test
    public void isNotTextTest() {
        String[] texts = {"=agbc", "=dgdfdfdgdfdsfte", "5525", "=fdfdfdjfhj!@$%^", "=hfjdfhhdfhsidjsdsurefjdhdjfhfj", "=A0"};
        for (int i = 0; i < texts.length; i++) {
            boolean data = SCell.isText(texts[i]);
            assertFalse(data);
        }
    }

    @Test
    public void removeSpaceTest() {
        SCell sCell = new SCell();
        String text = "1 + 2 +    3";
        String expected = "1+2+3";
        String actual = sCell.removeSpace(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest() {
        SCell sCell = new SCell();
        String[] valid = {"=(3+3) + (5+2)","=(1*8)+5", "=4+7/8*88"};
        for (int i = 0; i < valid.length; i++) {
            assertTrue(sCell.isForm(valid[i]));
        }
    }

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

    @Test
    public void computeForm_CellTest() {
        Ex2Sheet sheet = new Ex2Sheet(20,20);
        SCell[] cells = new SCell[20];
        int[] values = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};

        for (int i = 0; i < values.length; i++) {
            sheet.set(0, i, String.valueOf(i));
            cells[i] = (SCell) sheet.get(0, i); // Cell A0 ~ A19
        }

        for (int j = 0; j < values.length; j++) {
            assertEquals(String.valueOf(values[j]), cells[j].getData());
        }
    }
    @Test
    public void isCoordinate_Pass_Test() {
        String coordiantes[] = {"A0", "A1","G5", "Z99"};
        for (int i = 0; i < coordiantes.length; i++) {
            assertTrue(isCoordinate(coordiantes[i]));
        }
    }

    @Test
    public void isCoordiantes_Fail_Test() {
        String[] cords = {"A100", "A", "B", "G455"};
        for (int i = 0; i < cords.length; i++) {
            assertFalse(isCoordinate(cords[i]));
        }
    }

    @Test
    public void computeForm_From_Cell_Test() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,1,"5");
        String s = "=A1";
        SCell cell = new SCell(s, sheet);
        double dd = cell.computeForm(s);

    }

    @Test
    public void evalTest() {
        int value = 20;
        Ex2Sheet sheet = new Ex2Sheet(value, value);
        for (int i = 0; i < value; i++) {
            sheet.set(0,i, String.valueOf(i));
        }
        String[] expected = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19"};
        for (int i = 0; i < value; i++) {
            assertEquals(expected[i], sheet.eval(0,i));
        }
    }

    @Test
    public void scanFormulaTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        SCell cell1 = new SCell("=5",sheet);
        SCell cell2 = new SCell("=A0", sheet);
        String cell2Value = cell2.getData();
        sheet.set(0,0,"=5");
        sheet.set(0,1, cell2Value);
        String referenceFormula = "=1.0+A0";
        double computedFormula = cell2.computeForm(referenceFormula);
        System.out.println(computedFormula);
    }

    @Test
    public void Ex2Sheet_set_Test(){
        Ex2Sheet sheet = new Ex2Sheet();
        String[] values = {"=5", "=A0", "=1+A0", "=A1"};
        double[] expected = {5, 5, 6, 5};
        SCell cells[] = new SCell[4];
        double actual = 0;
        for (int i = 0; i < 4; i++) {
            sheet.set(0,i,values[i]);
            cells[i] = new SCell(sheet.get(0, i).getData(), sheet);
        }
        for (int j = 0; j < 4; j++) {
            actual = cells[j].computeForm(values[j]);
            assertEquals(expected[j], actual);
        }
    }

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

    @Test
    public void testComplexOrder() {
        Ex2Sheet sheet = new Ex2Sheet();
        // Setup test data
        sheet.set(0, 0, "10"); //A0 is a number, depth = 0
        sheet.set(0, 1, "20"); //A1 is a form with no reference, depth = 1
        sheet.set(1, 0, "=A0+A1"); //B0 is a form with two references, depth = 1 + max(A0,A1) = 1 + 1 = 2
        sheet.set(1, 1, "=B0*2"); //B1 is a form with 1 reference, depth = 1 + depth(B0) = 1 + 1 = 3
        sheet.set(2, 0, "=(B0+B1)/2"); //C0 depth = 1 +

        // Test orders
        assertEquals(0, sheet.get(0, 0).getOrder());
        assertEquals(0, sheet.get(0, 1).getOrder());
        assertEquals(1, sheet.get(1, 0).getOrder());
        assertEquals(1, sheet.get(1, 1).getOrder());
        assertEquals(2, sheet.get(2, 0).getOrder());
    }
}


