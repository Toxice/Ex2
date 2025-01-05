package Util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static Util.SCell.isCoordinate;
import static Util.SCell.isForm;
import static org.junit.jupiter.api.Assertions.*;

public class Ex2Test {
    @Test
    public void isNumberTest() {
        SCell sCell = new SCell();
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
        SCell sCell = new SCell();
        String text = "1 + 2 +    3";
        String expected = "1+2+3";
        String actual = sCell.removeSpace(text);
        assertEquals(expected, actual);
    }

    @Test
    public void isFormTest() {
        SCell sCell = new SCell();
        String[] valid = {"=(3+3) + (5+2)", "=88+5", "=5+4", "=55-4"};
        for (int i = 0; i < valid.length; i++) {
            assertTrue(sCell.isForm(valid[i]));
        }
    }
//    @Test
//    public void computeForm() {
//        String text = "=(3*2)+2";
//        double actual = 8;
//        double expected = SCell.computeForm(text);
//        System.out.println(expected);
//        assertEquals(expected, actual);
//    }

    @Test
    public void computeFormCellTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        SCell cell = (SCell) sheet.get(0,0);
        String text = "=A0";
        double ans = cell.computeForm(text);
    }

//    @Test
//    public void evalTest() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0,0,"25");
//        sheet.set(0,1,"A0");
//        sheet.eval(0,1);
//    }

    @Test
    public void isFormTest2() {
        SCell sCell = new SCell();
        String text = "=5+2+8*(3-2)";
        boolean ans = sCell.isForm(text);
        assertTrue(ans);
    }

//    @Test
//    public void valueTest() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        String text = "=A1";
//        String actual = sheet.value(1,2);
//        System.out.println(actual);
//        System.out.println();
//    }

//    @Test
//    public void dependencyParserTest() {
//        DependencyParser parser = new DependencyParser();
//        String cell1 = "A11";
//        String cell2 = "B2";
//        Coordinate coordinate1 = parser.parseCell(cell1);
//        Coordinate coordinate2 = parser.parseCell(cell2);
//        assertEquals(coordinate1.x, 0);
//        assertEquals(coordinate1.y, 11);
//        assertEquals(coordinate2.x, 1);
//        assertEquals(coordinate2.y, 2);
//    }

    @Test
    public void depthTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        DependencyParser parser = new DependencyParser();
        sheet.set(0,0,"5");
        sheet.set(0,1,"2");
        sheet.set(2,0,"A0");
        Coordinate c1 = parser.parseCell(sheet.value(0,2));
        System.out.println();
    }

    @Test
    public void isCoordinateTest() {
        String coordiantes[] = {"A0", "A1","G5", "Z99"};
        for (int i = 0; i < coordiantes.length; i++) {
            assertTrue(isCoordinate(coordiantes[i]));
        }
    }

    @Test
    public void isCoordiantesFail() {
        String cord = "A100";
        assertFalse(isCoordinate(cord));
    }

    @Test
    public void setDataTest() {
        String s = "A0";
        SCell cell = new SCell("A0");
        cell.setData(s);
    }

    @Test
    public void dependencyParserTest() {
        String s = "A0";
        List<CellEntry> Entries = DependencyParser.parseDependencies(s);
    }

    @Test
    public void parseCoordinateValueTest() {
        String s = "A0";
        System.out.println(DependencyParser.parseCoordinateValue(s).toString());
    }

    @Test
    public void isFormCellTest() {
        String s = "=A0";
        boolean ans = isForm(s);
        assertTrue(ans);
    }

    @Test
    public void computeFormCell() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,1,"5");
        String s = "=A1";
        SCell cell = new SCell(s, sheet);
        double dd = cell.computeForm(s);
        System.out.println(dd);
    }

    @Test
    public void evalTest() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,0,"=5");
        SCell cell1 = new SCell("=A0", sheet);
        sheet.set(0,1,"=A0");
        String ans = sheet.eval(0,0);
        System.out.println(ans);
        String ans1 = sheet.eval(0,1);
        System.out.println(ans1);

    }
}
