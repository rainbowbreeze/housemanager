/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

/**
 * Contains result of scraping action for a single announce page
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class AnnounceScrapingResult {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public AnnounceScrapingResult() {
    }

    // --------------------------------------- Public Properties
    private HouseAnnounce mAnnounce;
    public HouseAnnounce getAnnounce() {
        return mAnnounce;
    }
    public AnnounceScrapingResult setAnnounce(HouseAnnounce newValue) {
        mAnnounce = newValue;
        return this;
    }
    
    private int mConversionError;
    public int getConversionError() {
        return mConversionError;
    }
    
    
    // ------------------------------------------ Public Methods
    public AnnounceScrapingResult addError() {
        mConversionError++;
        return this;
    }
    
    public boolean hasErrors() {
        return mConversionError > 0;
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
