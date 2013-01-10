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
import it.rainbowbreeze.housemanager.logic.agent.AnnounceScrapingResult;
import it.rainbowbreeze.housemanager.logic.agent.ImmobiliareAgent;
import it.rainbowbreeze.housemanager.logic.agent.SearchPageAgentResult;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ImmobiliareAgentTest {
    // ------------------------------------------ Private Fields
    private final MockNetworkManager mNetworkManager;
    private ImmobiliareAgent mAgent;

    // -------------------------------------------- Constructors
    public ImmobiliareAgentTest() {
        mNetworkManager = new MockNetworkManager(App.i().getLogFacility());
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void setUp() {
        mNetworkManager.getUrlReplies().clear();
        mAgent = new ImmobiliareAgent(
                App.i().getLogFacility(),
                mNetworkManager);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testFirstPage() throws Exception {
        File file = new File("testresources/immobiliare_mock_search_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        
        mNetworkManager.getUrlReplies().put(ImmobiliareAgent.URL_FIRST_RESULT_PAGE, fileContent);
        
        SearchPageAgentResult result = mAgent.scrape();
        assertNotNull(result);
        assertTrue(result.hasMoreResults());
        assertFalse(result.hasErrors());
        assertEquals(61, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        assertEquals("/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=2", result.getCursor());
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isNotEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() > 0);
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }

    @Test
    public void testSecondPage() throws Exception {
        File file = new File("testresources/immobiliare_mock_search_2.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        String cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=2";
        
        mNetworkManager.getUrlReplies().put(ImmobiliareAgent.URL_NEXT_RESULT_PAGE_BASE + cursor, fileContent);
        
        SearchPageAgentResult result = mAgent.scrape(cursor);
        assertNotNull(result);
        assertTrue(result.hasMoreResults());
        assertFalse(result.hasErrors());
        assertEquals(62, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        assertEquals("/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=3", result.getCursor());
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isNotEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() > -1); //trattative riservate
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }

    @Test
    public void testLastPage() throws Exception {
        String cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=61";
        File file = new File("testresources/immobiliare_mock_search_61.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));

        mNetworkManager.getUrlReplies().put(ImmobiliareAgent.URL_NEXT_RESULT_PAGE_BASE + cursor, fileContent);
        
        SearchPageAgentResult result = mAgent.scrape(cursor);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertEquals(61, result.getTotalPages());
        assertEquals(6, result.getAnnounces().size());
        assertFalse(result.hasMoreResults());
        assertTrue(StringUtils.isEmpty(result.getCursor()));
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertTrue("Detail url", StringUtils.isNotEmpty(announce.getDetailUrl()));
            assertTrue("Image url", StringUtils.isNotEmpty(announce.getImgUrl()));
            assertTrue("Short desc", StringUtils.isNotEmpty(announce.getShortDesc()));
            assertTrue("Title", StringUtils.isNotEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Domain", StringUtils.isNotEmpty(announce.getDomainSite()));
            assertFalse("Deep processed", announce.wasDeepProcessed());
            //assertTrue("Price", announce.getPrice() > 0); //trattativa riservata
        }
    }
    
    @Test
    public void testAnnounceScraping() throws Exception {
        String url = "http://www.immobiliare.it/34534230-Vendita-Bilocale-ottimo-stato-piano-terra-Pavia.html";
        
        File file = new File("testresources/immobiliare_mock_detail_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));

        mNetworkManager.getUrlReplies().put(url, fileContent);
        
        HouseAnnounce announce = new HouseAnnounce()
                .setDetailUrl(url);
        
        AnnounceScrapingResult result = mAgent.scrapeDeep(announce);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertNotNull(result.getAnnounce());
        announce = result.getAnnounce();
        assertTrue(announce.wasDeepProcessed());
        assertEquals("45.181", announce.getLat());
        assertEquals("9.16894", announce.getLon());
    }
    
    @Test
    public void testGetTaskQueueName_Announce() {
        String url = "http://www.immobiliare.it/34534230-Vendita-Bilocale-ottimo-stato-piano-terra-Pavia.html";
        HouseAnnounce announce = mAgent.createAnnounce()
                .setDetailUrl(url);
        String taskName = mAgent.getTaskQueueName(announce);
        assertEquals("ImmobiliareIt_34534230", taskName);
    }
    
    @Test
    public void testGetTaskQueueName_Cursor() {
        String cursor = null;
        String taskName = mAgent.getTaskQueueName(cursor);
        assertEquals("ImmobiliareIt-", taskName);
        cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=2";
        taskName = mAgent.getTaskQueueName(cursor);
        assertEquals("ImmobiliareIt-2", taskName);
        cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=234";
        taskName = mAgent.getTaskQueueName(cursor);
        assertEquals("ImmobiliareIt-234", taskName);
    }
    
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}