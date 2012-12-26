/**
 * 
 */
package it.rainbowbreeze.housemanager.domain;

import javax.persistence.Entity;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
@Entity(name = "HouseAnnounce")
public class HouseAnnounce {

    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    private String mTitle;
    public String getTitle() {
        return mTitle;
    }
    public HouseAnnounce setTitle(String value) {
        mTitle = value;
        return this;
    }

    private String mDetailUrl;
    public String getDetailUrl() {
        return mDetailUrl;
    }
    public HouseAnnounce setDetailUrl(String value) {
        mDetailUrl = value;
        return this;
    }

    private int mPrice;
    public int getPrice() {
        return mPrice;
    }
    public HouseAnnounce setPrice(int value) {
        mPrice = value;
        return this;
    }

    private int mArea;
    public int getArea() {
        return mArea;
    }
    public HouseAnnounce setArea(int value) {
        mArea = value;
        return this;
    }

    private String mShortDesc;
    public String getShortDesc() {
        return mShortDesc;
    }
    public HouseAnnounce setShortDesc(String value) {
        mShortDesc = value;
        return this;
    }

    private String mImgUrl;
    public String getImgUrl() {
        return mImgUrl;
    }
    public HouseAnnounce setImgUrl(String value) {
        mImgUrl = value;
        return this;
    }


    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
