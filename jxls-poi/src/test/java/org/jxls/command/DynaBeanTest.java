package org.jxls.command;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.Test;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

public class DynaBeanTest {

    @Test
    public void grouping() throws Exception {
        List<DynaBean> employees = generateDynaSampleEmployeeData();
        try (InputStream is = getClass().getResourceAsStream("grouping_template.xlsx")) {
            try (OutputStream os = new FileOutputStream("target/grouping_output.xlsx")) {
                Context context = new Context();
                context.putVar("employees", employees);
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }
    }

    private List<DynaBean> generateDynaSampleEmployeeData() throws Exception {
        DynaClass dynaClass = new BasicDynaClass("Employee", null,
                new DynaProperty[] { new DynaProperty("name", String.class), });
        List<DynaBean> employeesDyna = new ArrayList<>();
    
        DynaBean elsa = dynaClass.newInstance();
        elsa.set("name", "Elsa");
        employeesDyna.add(elsa);
    
        DynaBean oleg = dynaClass.newInstance();
        oleg.set("name", "Oleg");
        employeesDyna.add(oleg);
    
        DynaBean john = dynaClass.newInstance();
        john.set("name", "John");
        employeesDyna.add(john);
    
        return employeesDyna;
    }

    @Test
    public void groupingWithJavaBean() throws Exception {
        List<TestEmployee> employees = generateStaticSampleEmployeeData();
        try (InputStream is = getClass().getResourceAsStream("grouping_template.xlsx")) {
            try (OutputStream os = new FileOutputStream("target/grouping_output.xlsx")) {
                Context context = new Context();
                context.putVar("employees", employees);
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }
    }

    private List<TestEmployee> generateStaticSampleEmployeeData() throws Exception {
        List<TestEmployee> employees = new ArrayList<>();
        employees.add(new TestEmployee("", "Elsa", "", "", 0));
        employees.add(new TestEmployee("", "Oleg", "", "", 0));
        employees.add(new TestEmployee("", "John", "", "", 0));
        return employees;
    }
}
