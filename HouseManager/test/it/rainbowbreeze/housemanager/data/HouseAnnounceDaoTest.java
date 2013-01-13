/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    }
    
    @After
    public void tearDown() {
        mHelper.tearDown();
        if (null != mDao) {
            mDao.deleteAll();
        }
        
    }
    
    @Test
    public void testCRUD() {
        int size;
        
        mDao.deleteAll();
        size = mDao.count();
        assertEquals(0, size);
        
        HouseAnnounce houseAnnounce1 = new HouseAnnounce()
                .setAnnounceType(AnnounceType.RENT)
                .setArea(45)
                .setDeepProcessed(false)
                .setDetailUrl("http://detailurl1")
                .setDomainSite("tecnocasa")
                .setImgUrl("http://imageurl1")
                .setLat("123.123")
                .setLon("456.456")
                .setPrice(145000)
                .setShortDesc("shortdesc 1")
                .setTitle("Title 1");
        Key<HouseAnnounce> saveResult = mDao.save(houseAnnounce1);
        assertNotNull(saveResult);
        
        size = mDao.count();
        assertEquals(1, size);
        
        
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
