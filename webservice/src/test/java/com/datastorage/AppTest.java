package com.datastorage;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    @Test
    public void test1() throws ParseException {
//        String d = DateFormatUtils.format(1598883834388L,"yyyy-MM-dd HH:mm:ss");
//        System.out.println(d);
        String a = "2";
        System.out.println(String.valueOf(a).matches("^\\d+$"));
    }
}
