package Util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static Util.SCell.*;
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
        sheet.set(0,1,"=A0");
        SCell cell1 = new SCell("=5", sheet);
        SCell cell2 = new SCell("=A0", sheet);
        String ans1 = cell1.toString();
        String ans2 = cell2.toString();
        sheet.set(0,0,ans1);
        sheet.set(0,1,ans2);
        System.out.println(sheet.eval(0,0));
        System.out.println(sheet.eval(0,1));
//        String ans = sheet.eval(0,0);
//        System.out.println(ans);
//        String ans1 = sheet.eval(0,1);
//        System.out.println(ans1);

    }

    @Test
    public void evalTest2() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,0,"=5");
        sheet.set(0,1,"=A0");
        assertEquals("5.0", sheet.eval(0,0));  // Should return "5"
        assertEquals("5.0", sheet.eval(0,1));  // Should also return "5"
    }

    @Test
    public void evalTest3() {
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,0,"=5");    // Cell A0 = 5
        sheet.set(0,1,"=A0");   // Cell A1 = value of A0

        String[] evals = {sheet.eval(0,0), sheet.eval(0,1)};

        assertEquals("5.0", evals[0]);  // A0 should evaluate to 5
        assertEquals("5.0", evals[1]);  // A1 should also evaluate to 5
        System.out.println("A0 = " + evals[0]);
        System.out.println("A1 = " + evals[1]);
    }

    @Test
    public void isCoordTest() {
        String[] test = {"A0", "A12", "B101", "5"};
        boolean[] expected = {true, true, false, false};
        for (int i = 0; i < test.length; i++) {
            assertEquals(expected[i], isCoord(test[i]));
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
    public void CellValue_Test() {
        Ex2Sheet sheet = new Ex2Sheet();
        SCell cell1 = new SCell("=5", sheet);
        SCell cell2 = new SCell("=A0", sheet);
        sheet.set(0,0, cell1.getData());
        sheet.set(0,1,cell2.getData());
        double ans_Cell1 = cell1.computeForm(cell1.getData());
        double ans_Cell2 = cell2.computeForm(cell2.getData());
        String ans = "=A0";
        String ans3 = "=A0+1";
        double ans2 = cell2.computeForm(ans);
        System.out.println(ans_Cell1);
        System.out.println(ans_Cell2);
        System.out.println(ans3);
    }

    @Test
    public void new_computeForm_Test() {
        Ex2Sheet sheet = new Ex2Sheet();
        SCell cellNum = new SCell("=5", sheet);
        SCell cellReference = new SCell("=A0", sheet);
        SCell cellAll = new SCell("=1+A0", sheet);
        int[] types = {cellNum.getType(), cellReference.getType(), cellAll.getType()};
        for (int i = 0; i < types.length; i++) {
            System.out.println(types[i]);
        }
        sheet.set(0,0,cellNum.getData());
        sheet.set(0,1,cellReference.getData());
        sheet.set(0,2,cellAll.getData());
        String onlyNumber = "=5";
        String onlyReference = "=A0";
        String numberAndReference = "=1+A0";
        double number = cellNum.computeForm(cellNum.getData());
        double reference = cellReference.computeForm(cellReference.getData());
        double All = cellAll.computeForm(cellAll.getData());
        System.out.println(number);
        System.out.println(reference);
        System.out.println(All);
    }

    @Test
    public void cus_emek(){
        Ex2Sheet sheet = new Ex2Sheet();
        sheet.set(0,0,"=5");
        sheet.set(0,1,"=A0");
        sheet.set(0,2,"=1+A0");
        SCell cell1 = new SCell(sheet.get(0,0).getData(), sheet);
        SCell cell2 = new SCell(sheet.get(0,1).getData(), sheet);
        SCell cell3 = new SCell(sheet.get(0,2).getData(), sheet);
        cell1.setData(sheet.get(0,0).getData());
        cell2.setData(sheet.get(0,1).getData());
        cell3.setData(sheet.get(0,2).getData());
        double d1 = cell1.computeForm("=5");
        double d2 = cell2.computeForm("=A0");
        double d3 = cell3.computeForm("=1+A0");
        double[] dd = {d1, d2, d3};
        for (int i = 0; i < 3; i++) {
            System.out.println(dd[i]);
        }
    }

    @Test
    public void isFormTest1() {
        String[] ans = {"=1+3", "=A0", "=4+5"};
        for (int i = 0; i < ans.length; i++) {
            assertTrue(isForm(ans[i]));
        }
    }

    @Test
    public void inputCellTest() {
        Ex2Sheet table = new Ex2Sheet();
        table.set(0,0,"=5");
        table.set(0,1,"=A0");
        table.set(0,2,"=1+A0");
        int xx = 0;
        int yy = 0;
        CellEntry cord = new CellEntry(xx, yy);
        CellEntry cord1 = new CellEntry(xx,yy+1);
        CellEntry cord2 = new CellEntry(xx,yy+2);
        if(table.isIn(xx,yy)) {
            Cell cc = table.get(xx,yy);
            String ww = cord+": "+cc.toString()+" : ";
            if(Ex2Utils.Debug) {System.out.println(ww);}
            String c = cc.getData();
            String s1 = table.get(xx,yy).getData();
            if(c==null) {
                table.set(xx,yy,s1);
            }
            else {
                table.set(xx, yy, c);
                int[][] calc_d = table.depth();
                if (calc_d[xx][yy] == Ex2Utils.ERR) {
                    table.get(xx,yy).setType(Ex2Utils.ERR_CYCLE_FORM);
                }
            }
            table.eval();
            StdDrawEx2.resetXY();

            System.out.println();
        }
    }

    @Test
    public void toStringTest() {
        Ex2Sheet table = new Ex2Sheet();
        SCell cell = new SCell("=5", table);
        table.set(0,0,cell.toString());
        System.out.println("cell is: " + cell.toString());
    }
    }

