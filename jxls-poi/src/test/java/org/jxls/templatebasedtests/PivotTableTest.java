package org.jxls.templatebasedtests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.jxls.TestWorkbook;
import org.jxls.common.Context;
import org.jxls.entity.Employee;
import org.jxls.util.JxlsHelper;
import org.jxls.util.NlsHelper;

public class PivotTableTest {

    /**
     * Issue 155: Pivot table does not work with NLS
     */
    @Test
    public void nls() throws Exception {
        // Prepare
        final Context context = new Context();
        context.putVar("employees", getTestData());
        final Properties nls = new Properties();
        nls.put("name", "Name (EN)");
        nls.put("salary", "Salary (EN)");
        
        // Test
        File in = new File(getClass().getResource(getClass().getSimpleName() + ".xlsx").toURI());
        NlsHelper r = new NlsHelper() {
            @Override
            protected String translate(String name, String fallback) {
                return nls.getProperty(name, fallback);
            }
        };
        File temp = r.process(in);
        File out = new File("target/" + getClass().getSimpleName() + "_output.xlsx");
        try (InputStream is = new FileInputStream(temp)) {
            try (OutputStream os = new FileOutputStream(out)) {
                JxlsHelper jxls = JxlsHelper.getInstance();
                jxls.setEvaluateFormulas(true);
                jxls.processTemplate(is, os, context);
            }
        }
        temp.delete();
        
        // Verify
        try (TestWorkbook w = new TestWorkbook(out)) {
            w.selectSheet("Employees");
            Assert.assertEquals("Name (EN)", w.getCellValueAsString(1, 1));
            Assert.assertEquals("BU", w.getCellValueAsString(1, 2));
            Assert.assertEquals("Salary (EN)", w.getCellValueAsString(1, 3));
            Assert.assertEquals("Sven", w.getCellValueAsString(2, 1));
            w.selectSheet("Crosstab");
// TODO           Assert.assertEquals("BU", w.getCellValueAsString(7, 3)); // C7
//            Assert.assertEquals("Finance", w.getCellValueAsString(8, 3)); // C8
        }
    }

    private List<Employee> getTestData() {
        List<Employee> list = new ArrayList<>();
        add(list, "Sven", "Mayor", 100000);
        add(list, "Christiane", "Finance", 30000);
        add(list, "Betty", "Finance", 45000);
        add(list, "John", "Main", 50000);
        add(list, "Waldemar", "Main", 60000);
        return list;
    }

    private void add(List<Employee> list, String name, String department, double salary) {
        Employee e = new Employee(name, null, salary, 0);
        e.setBuGroup(department);
        list.add(e);
    }
}
