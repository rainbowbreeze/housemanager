/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains result of scraping action for the main search page of the website,
 * the ones with lot of result, not a particular announce page
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class SearchPageAgentResult {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public SearchPageAgentResult() {
    }

    // --------------------------------------- Public Properties
    private String mCursor;
    public String getCursor() {
        return mCursor;
    }
    public SearchPageAgentResult setCursor(String cursor) {
        mCursor = cursor;
        return this;
    }
    
    private int mTotalPages;
    public int getTotalPages() {
        return mTotalPages;
    }
    public SearchPageAgentResult setTotalPages(int totalPages) {
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
    
    
    // ------------------------------------------ Public Methods
    public boolean hasMoreResults() {
        return !StringUtils.isEmpty(mCursor);
    }
    
    public SearchPageAgentResult addError() {
        mConversionError++;
        return this;
    }
    
    public boolean hasErrors() {
        return mConversionError > 0;
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
