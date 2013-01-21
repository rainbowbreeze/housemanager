/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce.AnnounceType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * TODO: Check https://developers.google.com/appengine/docs/java/tools/localunittesting
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class HouseAnnounceDaoTest {
    // ------------------------------------------ Private Fields
    private final LocalServiceTestHelper mHelper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private HouseAnnounceDao mDao;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void setUp() {
        mHelper.setUp();
        mDao = App.i().getHouseAnnounceDao();
        mDao.deleteAll();
        int size = mDao.count();
        assertEquals(0, size);
    }
    
    @After
    public void tearDown() {
        if (null != mDao) {
            mDao.deleteAll();
        }
        mHelper.tearDown();
    }
    
    @Test
    public void testCRUD() {
        Key<HouseAnnounce> saveResult;
        int size;
        
        HouseAnnounce announce1 = createAnnounce1();
        saveResult = mDao.save(announce1);
        assertNotNull(saveResult);
        size = mDao.count();
        assertEquals(1, size);
        
        HouseAnnounce announce2 = createAnnounce2();
        saveResult = mDao.save(announce2);
        assertNotNull(saveResult);
        size = mDao.count();
        assertEquals(2, size);
        
        HouseAnnounce announce3 = createAnnounce3();
        saveResult = mDao.save(announce3);
        assertNotNull(saveResult);
        size = mDao.count();
        assertEquals(3, size);
        
        List<HouseAnnounce> getAllResult = mDao.getAll();
        assertNotNull(getAllResult);
        assertEquals(3, getAllResult.size());
        
        mDao.deleteAll();
        size = mDao.count();
        assertEquals(0, size);
    }
    
    @Test
    public void testFilteredGet() {
        int size;
        List<HouseAnnounce> actualResult;
        
        HouseAnnounce announce1 = createAnnounce1()
                .setDeepProcessed(true).setLat(null).setLon(null);
        HouseAnnounce announce2 = createAnnounce1()
                .setDeepProcessed(true).setLat("45.23").setLon(null);
        HouseAnnounce announce3 = createAnnounce1()
                .setDeepProcessed(true).setLat("45.22").setLon("9.11");
        HouseAnnounce announce4 = createAnnounce4()
                .setDeepProcessed(false).setLat("45.17").setLon("9.20");
        HouseAnnounce announce5 = createAnnounce4()
                .setDeepProcessed(false).setLat("300").setLon("200");
        assertNotNull(mDao.save(announce1));
        assertNotNull(mDao.save(announce2));
        assertNotNull(mDao.save(announce3));
        assertNotNull(mDao.save(announce4));
        assertNotNull(mDao.save(announce5));

        size = mDao.count();
        assertEquals(5, size);
        
        actualResult = mDao.getAllValidAndEncoded();
        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        HouseAnnounce actualAnnounce = actualResult.get(0);
        assertNotNull(actualAnnounce);
        assertEquals(announce3.getId(), actualAnnounce.getId());
        actualAnnounce = actualResult.get(1);
        assertNotNull(actualAnnounce);
        assertEquals(announce4.getId(), actualAnnounce.getId());
    }

    // ----------------------------------------- Private Methods
    private HouseAnnounce createAnnounce1() {
        HouseAnnounce houseAnnounce = new HouseAnnounce()
                .setAnnounceType(AnnounceType.RENT)
                .setArea(110)
                .setDeepProcessed(false)
                .setDetailUrl("http://detailurl1")
                .setDomainSite("domain1")
                .setImgUrl("http://imageurl1")
                .setLat("1.1")
                .setLon("2.2")
                .setPrice(111000)
                .setShortDesc("shortdesc 1")
                .setTitle("Title 1");
        return houseAnnounce;
    }

    private HouseAnnounce createAnnounce2() {
        HouseAnnounce houseAnnounce = new HouseAnnounce()
                .setAnnounceType(AnnounceType.RENT)
                .setArea(220)
                .setDeepProcessed(true)
                .setDetailUrl("http://detailurl2")
                .setDomainSite("domain2")
                .setImgUrl("http://imageurl2")
                .setLat("3.3")
                .setLon("4.4")
                .setPrice(222000)
                .setShortDesc("shortdesc 2")
                .setTitle("Title 2");
        return houseAnnounce;
    }

    private HouseAnnounce createAnnounce3() {
        HouseAnnounce houseAnnounce = new HouseAnnounce()
                .setAnnounceType(AnnounceType.SELL)
                .setArea(330)
                .setDeepProcessed(false)
                .setDetailUrl("http://detailurl3")
                .setDomainSite("domain3")
                .setImgUrl("http://imageurl3")
                .setLat("5.5")
                .setLon("6.6")
                .setPrice(333000)
                .setShortDesc("shortdesc 3")
                .setTitle("Title 3");
        return houseAnnounce;
    }

    private HouseAnnounce createAnnounce4() {
        HouseAnnounce houseAnnounce = new HouseAnnounce()
                .setAnnounceType(AnnounceType.SELL)
                .setArea(440)
                .setDeepProcessed(false)
                .setDetailUrl("http://detailurl4")
                .setDomainSite("domain4")
                .setImgUrl("http://imageurl4")
                .setLat("7.7")
                .setLon("8.8")
                .setPrice(444000)
                .setShortDesc("shortdesc 4")
                .setTitle("Title 4");
        return houseAnnounce;
    }

    // ----------------------------------------- Private Classes
}
