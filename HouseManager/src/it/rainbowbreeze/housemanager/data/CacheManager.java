/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.logic.ScraperUtils;

import java.io.IOException;
import java.util.Date;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * Stores values in cache, instead of storage
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class CacheManager {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = CacheManager.class.getSimpleName();
    private static final String MEMKEY_LASTDATAREFRESH = "LastDataRefresh";
    private static final String MEMKEY_ANNOUNCESJSON = "AnnouncesJson";
    private static final String MEMKEY_ANNOUNCESNUMBER = "AnnouncesNumber";
    
    private final ILogFacility mLogFacility;
    private final MemcacheService mMemcache;
    
    // -------------------------------------------- Constructors
    public CacheManager(ILogFacility logFacility) {
        mLogFacility = logFacility;
        mMemcache = MemcacheServiceFactory.getMemcacheService();
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public boolean isRefreshRequired(Date lastDataRefresh) {
        Date cachedLastDataRefresh = (Date) mMemcache.get(MEMKEY_LASTDATAREFRESH);
        if (null != cachedLastDataRefresh && cachedLastDataRefresh.equals(lastDataRefresh)) {
            return false; 
        } else {
            return true; 
        }
    }
    
    public int getAnnouncesNumber() {
        Integer cacheValue = (Integer) mMemcache.get(MEMKEY_ANNOUNCESNUMBER);
        return null == cacheValue ? 0 : cacheValue;
    }
    
    public String getAnnouncesJson() {
        try {
            //string is compressed in order to save space
            return ScraperUtils.decompress((String) mMemcache.get(MEMKEY_ANNOUNCESJSON));
        } catch (IOException e) {
            mLogFacility.w(LOG_HASH, e.getMessage());
            return null;
        }
    }
    
    /**
     * Put some data inside the cache
     * 
     * @param announcesNumber
     * @param announcesJson
     * @param lastDataRefresh
     */
    public void cacheAnnounces(int announcesNumber, String announcesJson, Date lastDataRefresh) {
        try {
            mMemcache.put(MEMKEY_ANNOUNCESNUMBER, announcesNumber);
            mMemcache.put(MEMKEY_ANNOUNCESJSON, ScraperUtils.compress(announcesJson));
            mMemcache.put(MEMKEY_LASTDATAREFRESH, lastDataRefresh);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, e.getMessage());
            cleanCache();
        }
    }

    public void cleanCache() {
        mMemcache.delete(MEMKEY_ANNOUNCESNUMBER);
        mMemcache.delete(MEMKEY_ANNOUNCESJSON);
        mMemcache.delete(MEMKEY_LASTDATAREFRESH);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
