/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for scraping process
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ScraperUtils {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    /**
     * Gets the string contained inside specified tags, tags excluded
     * 
     * @param sourceString
     * @param leftTag
     * @param rightTag
     * @return
     */
    public static String getTextBetween(String sourceString, String leftTag, String rightTag) {
        if (StringUtils.isEmpty(sourceString)) return null;
        if (StringUtils.isEmpty(leftTag)) return null;
        int startPos = sourceString.indexOf(leftTag);
        if (startPos < 0) return null;
        
        startPos+= leftTag.length();
        if (startPos >= sourceString.length()) return null;
        if (StringUtils.isEmpty(rightTag)) return sourceString.substring(startPos);
        
        int endPos = sourceString.indexOf(rightTag, startPos);
        if (endPos < 0) return null;
        return sourceString.substring(startPos, endPos);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
