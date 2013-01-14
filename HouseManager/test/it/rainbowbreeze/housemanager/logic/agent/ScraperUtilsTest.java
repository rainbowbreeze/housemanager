/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import java.sql.Date;

import it.rainbowbreeze.housemanager.logic.agent.ScraperUtils;

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
        assertNull(ScraperUtils.getTextBetween("blabla", null, null));
        assertNull(ScraperUtils.getTextBetween("bla", "long", null));
        assertNull(ScraperUtils.getTextBetween("bla", "left", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "left", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftXXX", null));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftXXX", "right"));
        assertNull(ScraperUtils.getTextBetween("leftXXX", "leftX", "right"));
        assertTrue(StringUtils.isEmpty(ScraperUtils.getTextBetween("leftXXX", "left", "X")));
        assertEquals("XXX", ScraperUtils.getTextBetween("leftXXXright", "left", "right"));
    }
    
    @Test
    public void testGetyyyyMMdd() {
        assertNull(ScraperUtils.getyyyyMMdd(null));
        Date date = new Date(1326538186000l);  //January, 14th 2012 - 10:49:46 am UTC
        assertEquals("20120114", ScraperUtils.getyyyyMMdd(date));
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
