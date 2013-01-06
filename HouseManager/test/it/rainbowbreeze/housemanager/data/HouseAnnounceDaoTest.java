/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static org.junit.Assert.assertEquals;
import it.rainbowbreeze.housemanager.common.App;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

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
        //TODO test no key in the datastore in a better way
        size = mDao.count();
        assertEquals(0, size);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
