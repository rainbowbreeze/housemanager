/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

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

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
