/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;

import it.rainbowbreeze.housemanager.logic.ScraperUtils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ScraperUtilsTest {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Test
    public void testGetTextBetween() {
        assertNull(ScraperUtils.getTextBetween(null, null, null));
        assertNull(ScraperUtils.getTextBetween("bla", "long", null));
        assertNull(ScraperUtils.getTextBetween("bla", "left", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "left", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftXXX", null));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftXXX", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftX", "right"));
        assertTrue(StringUtils.isEmpty(ScraperUtils.getTextBetween("leftXXX", "left", "X")));
        assertEquals("XXX", ScraperUtils.getTextBetween("leftXXXright", "left", "right"));
        assertEquals("XXXright", ScraperUtils.getTextBetween("leftXXXright", "left", null));
        assertEquals("TestFull", ScraperUtils.getTextBetween("TestFull", null, null));
        assertEquals("Test", ScraperUtils.getTextBetween("TestFull", null, "Full"));
    }
    
    @Test
    public void testGetyyyyMMdd() {
        assertNull(ScraperUtils.getyyyyMMdd(null));
        Date date = new Date(1326538186000l);  //January, 14th 2012 - 10:49:46 am UTC
        assertEquals("20120114", ScraperUtils.getyyyyMMdd(date));
    }
    
    @Test
    public void testExtractNumbers() {
        assertEquals(null, ScraperUtils.extractNumbers(null));
        assertEquals("", ScraperUtils.extractNumbers(""));
        assertEquals("123", ScraperUtils.extractNumbers("123"));
        assertEquals("123", ScraperUtils.extractNumbers(" 123 "));
        assertEquals("456", ScraperUtils.extractNumbers("aa 456"));
        assertEquals("456", ScraperUtils.extractNumbers("aa 456 ddx"));
        assertEquals("123456", ScraperUtils.extractNumbers("a123a 456 asd"));
        assertEquals("854", ScraperUtils.extractNumbers("854 €"));
    }
    
    @Test
    public void testCompressDecompress() throws IOException {
        assertEquals(null, ScraperUtils.compress(null));
        assertEquals("", ScraperUtils.compress(""));
        assertEquals(null, ScraperUtils.decompress(null));
        assertEquals("", ScraperUtils.decompress(""));
        
        File file = new File("testresources/longstring.txt");
        String expectedContent = StreamHelper.toString(new FileInputStream(file));
        String compressedContent = ScraperUtils.compress(expectedContent);
        assertNotNull(compressedContent);
        assertTrue(expectedContent.length() > compressedContent.length());
        String actualContent = ScraperUtils.decompress(compressedContent);
        assertEquals(expectedContent, actualContent);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
