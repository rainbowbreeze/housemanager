/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

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

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ImmobiliareScraperTest {
    // ------------------------------------------ Private Fields
    private final MockNetworkManager mNetworkManager;
    private ImmobiliareScraper mScraper;

    // -------------------------------------------- Constructors
    public ImmobiliareScraperTest() {
        mNetworkManager = new MockNetworkManager(App.i().getLogFacility());
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    @Before
    public void init() {
        mNetworkManager.getUrlReplies().clear();
        mScraper = new ImmobiliareScraper(
                App.i().getLogFacility(),
                mNetworkManager);
    }
    
    @Test
    public void testFirstPage() throws Exception {
        File file = new File("testresources/immobiliare_mock_search_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        
        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_FIRST_RESULT_PAGE, fileContent);
        
        SearchPageScrapingResult result = mScraper.scrape();
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
            assertFalse("Deep processed", announce.wasDeepProcessed());
        }
    }

    @Test
    public void testLastPage() throws Exception {
        String cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=61";
        File file = new File("testresources/immobiliare_mock_search_2.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));

        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_NEXT_RESULT_PAGE_BASE + cursor, fileContent);
        
        SearchPageScrapingResult result = mScraper.scrapeNext(cursor);
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
        
        AnnounceScrapingResult result = mScraper.scrapeDeep(announce);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertNotNull(result.getAnnounce());
        announce = result.getAnnounce();
        assertTrue(announce.wasDeepProcessed());
        assertEquals("45.181", announce.getLat());
        assertEquals("9.16894", announce.getLon());
    }
    
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
