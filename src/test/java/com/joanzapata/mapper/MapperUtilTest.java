package com.joanzapata.mapper;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class MapperUtilTest {

    @Test
    public void removeSuffix() {
        assertEquals("Test", MapperUtil.removeSuffix("TestDTO", asList("DTO")));
    }

    @Test
    public void removeSuffixAmongOthers() {
        assertEquals("Test", MapperUtil.removeSuffix("TestDTO", asList("BO", "DT", "DTO")));
    }

    @Test
    public void removeSuffixCaseSensitive() {
        assertEquals("TestDTO", MapperUtil.removeSuffix("TestDTO", asList("dto", "to", "o")));
    }

    @Test
    public void removeSuffixFirstMatch() {
        assertEquals("Test", MapperUtil.removeSuffix("TestDTO", asList("DTO", "stDTO")));
        assertEquals("Te", MapperUtil.removeSuffix("TestDTO", asList("stDTO", "DTO")));
    }
}
