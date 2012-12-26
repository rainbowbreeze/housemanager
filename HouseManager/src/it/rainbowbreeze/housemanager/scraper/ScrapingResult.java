/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains result of scraping action
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ScrapingResult {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public ScrapingResult() {
    }

    // --------------------------------------- Public Properties
    private String mCursor;
    public String getCursor() {
        return mCursor;
    }
    public ScrapingResult setCursor(String cursor) {
        mCursor = cursor;
        return this;
    }
    
    private int mTotalPages;
    public int getTotalPages() {
        return mTotalPages;
    }
    public ScrapingResult setTotalPages(int totalPages) {
        this.mTotalPages = totalPages;
        return this;
    }
    
    private List<HouseAnnounce> mAnnounces = new ArrayList<HouseAnnounce>();
    public List<HouseAnnounce> getAnnounces() {
        return mAnnounces;
    }
    
    private int mConversionError;
    public int getConversionError() {
        return mConversionError;
    }
    public void setConversionError(int conversionError) {
        this.mConversionError = conversionError;
    }
    // ------------------------------------------ Public Methods
    public boolean hasMoreResults() {
        return !StringUtils.isEmpty(mCursor);
    }
    
    public ScrapingResult addError() {
        mConversionError++;
        return this;
    }
    
    public boolean hasErrors() {
        return mConversionError > 0;
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
