/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.domain.AppGlobalStatusBag;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class AppGlobalStatusBagDaoTest {
    // ------------------------------------------ Private Fields
    private static final String AGENT1 = "Immobiliare.it";
    private static final String AGENT2 = "Technocasa.it";
    private static final String AGENT3 = "MiaCasa.com";
    
    private final LocalServiceTestHelper mHelper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private AppGlobalStatusBagDao mDao;
    

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void setUp() {
        mHelper.setUp();
        mDao = App.i().getAppGlobalStatusBagDao();
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
        AppGlobalStatusBag actualBag;
        Key<AppGlobalStatusBag> actualSaveResult;
        
        //tests delete
        mDao.deleteAll();
        actualBag = mDao.get();
        assertNotNull(actualBag);
        assertNull(actualBag.getLastDataRefresh());
        assertNotNull(actualBag.getRunningAgents());
        assertTrue(actualBag.isRunningAgentsEmpty());
        
        //tests saving of new item
        Date expectedLatestRun = new Date();
        AppGlobalStatusBag expectedStatusBag = new AppGlobalStatusBag()
                .setLastDataRefresh(expectedLatestRun)
                .addRunningAgents(AGENT1)
                .addRunningAgents(AGENT2);
        actualSaveResult = mDao.save(expectedStatusBag);
        assertNotNull(actualSaveResult);
        
        //tests get
        actualBag = mDao.get();
        assertNotNull(actualBag);
        assertEquals(expectedLatestRun, actualBag.getLastDataRefresh());
        assertFalse(actualBag.isRunningAgentsEmpty());
        assertTrue(actualBag.getRunningAgents().contains(AGENT1));
        assertTrue(actualBag.getRunningAgents().contains(AGENT2));
    }

    @Test
    public void testAddRemoveRunningAgents() {
        AppGlobalStatusBag actualBag;
        
        //creates an item
        Date expectedLatestRun = new Date();
        AppGlobalStatusBag expectedStatusBag = new AppGlobalStatusBag()
                .setLastDataRefresh(expectedLatestRun);
        assertNotNull(mDao.save(expectedStatusBag));
        
        //adds a new running agent
        actualBag = mDao.get();
        actualBag.addRunningAgents(AGENT1);
        assertNotNull(mDao.save(expectedStatusBag));
        actualBag = mDao.get();
        assertFalse(actualBag.isRunningAgentsEmpty());
        assertTrue(actualBag.getRunningAgents().contains(AGENT1));
        
        //adds a bounce of new running agents
        actualBag = mDao.get();
        actualBag.addRunningAgents(AGENT2);
        actualBag.addRunningAgents(AGENT3);
        assertNotNull(mDao.save(expectedStatusBag));
        actualBag = mDao.get();
        assertFalse(actualBag.isRunningAgentsEmpty());
        assertTrue(actualBag.getRunningAgents().contains(AGENT1));
        assertTrue(actualBag.getRunningAgents().contains(AGENT2));
        assertTrue(actualBag.getRunningAgents().contains(AGENT3));
        
        //removes a running agent
        actualBag = mDao.get();
        actualBag.removeRunningAgents(AGENT1);
        assertNotNull(mDao.save(expectedStatusBag));
        actualBag = mDao.get();
        assertFalse(actualBag.isRunningAgentsEmpty());
        assertTrue(actualBag.getRunningAgents().contains(AGENT2));
        assertTrue(actualBag.getRunningAgents().contains(AGENT3));
        
        //removes the others running agent
        actualBag = mDao.get();
        actualBag.removeRunningAgents(AGENT2);
        actualBag.removeRunningAgents(AGENT3);
        assertNotNull(mDao.save(expectedStatusBag));
        actualBag = mDao.get();
        assertTrue(actualBag.isRunningAgentsEmpty());
    }
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
