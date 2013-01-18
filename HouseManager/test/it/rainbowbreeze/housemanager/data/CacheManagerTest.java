/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.rainbowbreeze.housemanager.common.App;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class CacheManagerTest {
    // ------------------------------------------ Private Fields
    private final LocalServiceTestHelper mHelper =
            new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
    private CacheManager mCacheManager;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void setUp() {
        mHelper.setUp();
        mCacheManager = App.i().getCacheManager();
        mCacheManager.cleanCache();
    }
    
    @After
    public void tearDown() {
        mCacheManager.cleanCache();
        mHelper.tearDown();
    }
    
    @Test
    public void testIsRefreshRequired() {
        assertTrue(mCacheManager.isRefreshRequired(null));
        Date firstMoment = new Date();
        assertTrue(mCacheManager.isRefreshRequired(firstMoment));
        mCacheManager.cacheAnnounces(20, "jsonString", firstMoment);
        assertFalse(mCacheManager.isRefreshRequired(firstMoment));
        Date secondMoment = new Date();
        assertTrue(mCacheManager.isRefreshRequired(secondMoment));
    }
    
    @Test
    public void testCacheAnnounces() {
        assertTrue(mCacheManager.isRefreshRequired(null));
        assertEquals(0, mCacheManager.getAnnouncesNumber());
        assertNull(mCacheManager.getAnnouncesJson());

        Date firstMoment = new Date();
        final String jsonString = "jsonString";
        final int announcesNumber = 20;
        mCacheManager.cacheAnnounces(announcesNumber, jsonString, firstMoment);
        assertEquals(announcesNumber, mCacheManager.getAnnouncesNumber());
        assertEquals(jsonString, mCacheManager.getAnnouncesJson());
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
