/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.MockNetworkManager;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.StreamHelper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class EurekasaAgentTest {
    // ------------------------------------------ Private Fields
    private final MockNetworkManager mNetworkManager;
    private EurekasaAgent mAgent;

    // -------------------------------------------- Constructors
    public EurekasaAgentTest() {
        mNetworkManager = new MockNetworkManager(App.i().getLogFacility());
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void setUp() {
        mNetworkManager.getUrlReplies().clear();
        mAgent = new EurekasaAgent(
                App.i().getLogFacility(),
                mNetworkManager);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testFirstPage() throws Exception {
        File file = new File("testresources/eurekasa_mock_search_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        
        mNetworkManager.getUrlReplies().put(mAgent.getSearchUrlFromCursor(null), fileContent);
        
        SearchPageAgentResult result = mAgent.scrape();
        assertNotNull(result);
        assertTrue(result.hasMoreResults());
        assertFalse(result.hasErrors());
        assertEquals(53, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        assertEquals("pag2", result.getCursor());
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertNotNull(announce.getAnnounceType());
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() >= 0);
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }
    
    @Test
    public void testSecondPage() throws Exception {
        File file = new File("testresources/eurekasa_mock_search_2.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        String cursor = "pag2";
        
        mNetworkManager.getUrlReplies().put(mAgent.getSearchUrlFromCursor(cursor), fileContent);
        
        SearchPageAgentResult result = mAgent.scrape(cursor);
        assertNotNull(result);
        assertTrue(result.hasMoreResults());
        assertFalse(result.hasErrors());
        assertEquals(53, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        assertEquals("pag3", result.getCursor());
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() >= 0);
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }

    @Test
    public void testLastPage() throws Exception {
        File file = new File("testresources/eurekasa_mock_search_53.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        String cursor = "pag53";
        
        mNetworkManager.getUrlReplies().put(mAgent.getSearchUrlFromCursor(cursor), fileContent);
        
        SearchPageAgentResult result = mAgent.scrape(cursor);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertFalse(result.hasMoreResults());
        assertEquals(53, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        assertTrue(StringUtils.isEmpty(result.getCursor()));
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() >= 0);
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }
    
    @Test
    public void testAnnounceScraping() throws Exception {
        String url = "http://annunci-casa.eurekasa.it/vendita/residenziale/Lombardia/Pavia/Villa-Pavia-19567617.html";
        
        File file = new File("testresources/eurekasa_mock_detail_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));

        mNetworkManager.getUrlReplies().put(url, fileContent);
        
        HouseAnnounce announce = new HouseAnnounce()
                .setDetailUrl(url);
        
        AnnounceScrapingResult result = mAgent.scrapeAnnounce(announce);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertNotNull(result.getAnnounce());
        announce = result.getAnnounce();
        assertTrue(announce.wasDeepProcessed());
        assertEquals("45.187301635742", announce.getLat());
        assertEquals("9.1743803024292", announce.getLon());
        assertFalse(StringUtils.isEmpty(announce.getShortDesc()));
        assertTrue(announce.getShortDesc().startsWith("PAVIA - Viale Campari 83/b: luminoso tre locali al piano 5° ed ultimo servito da ascensore composto da ingresso, soggiorno, cucina semi-abitabile, due camere di cui una matrimoniale, bagno, ripostiglio, ampio balcone, cantina. l'immobile che è da rivedere internamente è libero subito. le spese condominiali sono di circa"));
    }
    
    @Test
    public void testGetTaskQueueName_Announce() {
        Date testDate = new Date(1358160587000L);  //January, 14th 2013 - 10:49:46 am UTC
        String url = "http://annunci-casa.eurekasa.it/vendita/residenziale/Lombardia/Pavia/Appartamento-Pavia-35936442.html";
        HouseAnnounce announce = mAgent.createAnnounce()
                .setDetailUrl(url);
        String taskName = mAgent.getTaskQueueName(testDate, announce);
        assertEquals("EurekasaIt_20130114-Ann_Appartamento-Pavia-35936442", taskName);
    }
    
    /**
    @Test
    public void testGetTaskQueueName_Cursor() {
        Date testDate = new Date(1358160587000L);  //January, 14th 2013 - 10:49:46 am UTC
        String cursor = null;
        String taskName = mAgent.getTaskQueueName(testDate, cursor);
        assertEquals("ImmobiliareIt_20130114-Pg_", taskName);
        cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=2";
        taskName = mAgent.getTaskQueueName(testDate, cursor);
        assertEquals("ImmobiliareIt_20130114-Pg_2", taskName);
        cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=234";
        taskName = mAgent.getTaskQueueName(testDate, cursor);
        assertEquals("ImmobiliareIt_20130114-Pg_234", taskName);
    }
    
    */
    
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
