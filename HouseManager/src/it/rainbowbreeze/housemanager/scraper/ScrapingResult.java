/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

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

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
