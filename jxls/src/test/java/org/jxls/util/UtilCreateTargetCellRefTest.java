package org.jxls.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.jxls.common.CellRef;

/**
 * Tests for Util.createTargetCellRef()
 */
public class UtilCreateTargetCellRefTest {

    @Test
    public void single() {
        check("S!A1", // first arg is expectation
                "A1"); // after that n cellRefs (sheet name prefix "S!" can be omitted)
    }

    @Test
    public void horizontal() {
        check("S!G14:S!H14", // expectation
                "G14", "H14");
    }

    @Test
    public void horizontal_gap() {
        check("S!G14,S!I14", // expectation: no area
                "G14", "I14");
    }

    @Test
    public void diagonal() {
        check("S!A1,S!B2", // expectation: no area
                "A1", "B2");
    }

    @Test
    public void vertical() {
        check("S!G14:S!G15", // expectation
                "G14", "G15");
    }

    @Test
    public void vertical_gap() {
        check("S!G14,S!G16", // expectation: no area
                "G14", "G16");
    }

    @Test
    public void vertical_2sheets() {
        check("S!G14,Sheet2!G15", // expectation: no area
                "G14", "Sheet2!G15");
    }

    @org.junit.Ignore // TODO #90
    @Test // issue #90
    public void rectangle() {
        check("S!G14:S!J16", // expectation
                "G14", "H14", "I14", "J14",
                "G15", "H15", "I15", "J15",
                "G16", "H16", "I16", "J16");
    }

    @Test
    public void rectangle_gap() {
        check("S!G14,S!I14,S!G15,S!I15", // expectation: no area
                "G14", "I14", "G15", "I15");
    }

    @Test
    public void rectangle_2sheets() {
        // This input might not be realistic for JXLS but it tests createTargetCellRef().
        check("S!G14,S!H14,Sheet2!G15,S!H15", // expectation: no area
                "G14", "H14", "Sheet2!G15", "H15");
    }

    private void check(String expected, String... cellRefs) {
        // Prepare
        List<CellRef> cellRefsList = new ArrayList<>();
        for (String aCellRef : cellRefs) {
            if (!aCellRef.contains("!")) {
                aCellRef = "S!" + aCellRef; // add sheet name
            }
            cellRefsList.add(new CellRef(aCellRef));
        }
        
        // Test
        String result = Util.createTargetCellRef(cellRefsList);
        
        // Verify
        Assert.assertEquals(expected, result);
    }
}
