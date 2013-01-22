/**
 * 
 */
package it.rainbowbreeze.housemanager.domain;

import it.rainbowbreeze.housemanager.logic.ScraperUtils;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */

@Entity
public class HouseAnnounce {

    // ------------------------------------------ Private Fields

    // ------------------------------------------ Public Fields
    public static class Contract {
        private Contract() {}
        
        public static final String ID = "id";
        public static final String DEEPPROCESSED = "deepProcessed";
    }

    // -------------------------------------------- Constructors
    public HouseAnnounce() {
        deepProcessed = false;
    }
    

    // --------------------------------------- Public Properties
    @Id private String id;
    /**
     * Id of the announce
     * @return
     */
    public String getId() {
        return id;
    }
    public HouseAnnounce setId(String newValue) {
        id = newValue;
        return this;
    }
    
    
    private String domainSite;
    /**
     * Source site of the announce
     * @return
     */
    public String getDomainSite() {
        return domainSite;
    }
    public HouseAnnounce setDomainSite(String newValue) {
        domainSite = newValue;
        return this;
    }

    public enum AnnounceType { SELL, RENT };
    private AnnounceType announceType;
    /**
     * Type of the announce, if it's a sell or a rental announce
     * @return
     */
    public AnnounceType getAnnounceType() {
        return announceType;
    }
    public HouseAnnounce setAnnounceType(AnnounceType newValue) {
        announceType = newValue;
        return this;
    }
    
    private String title;
    public String getTitle() {
        return title;
    }
    public HouseAnnounce setTitle(String newValue) {
        title = newValue;
        return this;
    }

    private String detailUrl;
    public String getDetailUrl() {
        return detailUrl;
    }
    public HouseAnnounce setDetailUrl(String newValue) {
        detailUrl = newValue;
        return this;
    }

    private int price;
    public int getPrice() {
        return price;
    }
    public HouseAnnounce setPrice(int newValue) {
        price = newValue;
        return this;
    }

    private int area;
    public int getArea() {
        return area;
    }
    public HouseAnnounce setArea(int newValue) {
        area = newValue;
        return this;
    }

    private String shortDesc;
    public String getShortDesc() {
        return shortDesc;
    }
    public HouseAnnounce setShortDesc(String newValue) {
        shortDesc = newValue;
        return this;
    }

    private String imgUrl;
    public String getImgUrl() {
        return imgUrl;
    }
    public HouseAnnounce setImgUrl(String newValue) {
        imgUrl = newValue;
        return this;
    }
    
    
    @Index private boolean deepProcessed;
    /**
     * Deep analysis of the announce, not only the scraping for main page site
     */
    public boolean wasDeepProcessed() {
        return deepProcessed;
    }
    public HouseAnnounce setDeepProcessed(boolean newValue) {
        deepProcessed = newValue;
        return this;
    }
    
    private String lat;
    /**
     * Latitude
     */
    public String getLat() {
        return lat;
    }
    public HouseAnnounce setLat(String newValue) {
        lat = newValue;
        return this;
    }
    private String lon;
    /**
     * Longitude
     */
    public String getLon() {
        return lon;
    }
    public HouseAnnounce setLon(String newValue) {
        lon = newValue;
        return this;
    }
    
    // ------------------------------------------ Public Methods
    /**
     * URL encodes some fields that could contains dirty values and could creates problem to JavaScript.
     * The encoding follows the RFC2396.
     *   
     * @return
     */
    public HouseAnnounce encode() {
        setTitle(ScraperUtils.encodeRFC2396(getTitle()));
        setShortDesc(ScraperUtils.encodeRFC2396(getShortDesc()));
        return this;
    }


    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
