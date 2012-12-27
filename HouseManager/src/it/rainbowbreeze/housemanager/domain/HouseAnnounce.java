/**
 * 
 */
package it.rainbowbreeze.housemanager.domain;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class HouseAnnounce {

    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public HouseAnnounce() {
        mDeepProcessed = false;
    }
    

    // --------------------------------------- Public Properties
    private String mTitle;
    public String getTitle() {
        return mTitle;
    }
    public HouseAnnounce setTitle(String newValue) {
        mTitle = newValue;
        return this;
    }

    private String mDetailUrl;
    public String getDetailUrl() {
        return mDetailUrl;
    }
    public HouseAnnounce setDetailUrl(String newValue) {
        mDetailUrl = newValue;
        return this;
    }

    private int mPrice;
    public int getPrice() {
        return mPrice;
    }
    public HouseAnnounce setPrice(int newValue) {
        mPrice = newValue;
        return this;
    }

    private int mArea;
    public int getArea() {
        return mArea;
    }
    public HouseAnnounce setArea(int newValue) {
        mArea = newValue;
        return this;
    }

    private String mShortDesc;
    public String getShortDesc() {
        return mShortDesc;
    }
    public HouseAnnounce setShortDesc(String newValue) {
        mShortDesc = newValue;
        return this;
    }

    private String mImgUrl;
    public String getImgUrl() {
        return mImgUrl;
    }
    public HouseAnnounce setImgUrl(String newValue) {
        mImgUrl = newValue;
        return this;
    }
    
    
    private boolean mDeepProcessed;
    /**
     * Deep analysis of the announce, not only the scraping for main page site
     */
    public boolean wasDeepProcessed() {
        return mDeepProcessed;
    }
    public HouseAnnounce setDeepProcessed(boolean newValue) {
        mDeepProcessed = newValue;
        return this;
    }
    
    private String mLat;
    /**
     * Latitude
     */
    public String getLat() {
        return mLat;
    }
    public HouseAnnounce setLat(String newValue) {
        mLat = newValue;
        return this;
    }
    private String mLon;
    /**
     * Longitude
     */
    public String getLon() {
        return mLon;
    }
    public HouseAnnounce setLon(String newValue) {
        mLon = newValue;
        return this;
    }
    


    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
