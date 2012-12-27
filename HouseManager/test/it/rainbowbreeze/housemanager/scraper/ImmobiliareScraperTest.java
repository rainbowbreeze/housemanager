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
import java.io.IOException;

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
    public void testFirstPage() throws IOException {
        File file = new File("testresources/immobiliare_mock_1.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));
        
        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_FIRST_QUERY, fileContent);
        
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
    public void testLastPage() throws IOException {
        String cursor = "/Pavia/vendita_case-Pavia.html?criterio=rilevanza&pag=61";
        File file = new File("testresources/immobiliare_mock_2.txt");
        String fileContent = StreamHelper.toString(new FileInputStream(file));

        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_NEXT_QUERY + cursor, fileContent);
        
        SearchPageScrapingResult result = mScraper.scrapeNext(cursor);
        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertEquals(61, result.getTotalPages());
        assertEquals(6, result.getAnnounces().size());
        assertFalse(result.hasMoreResults());
        assertTrue(StringUtils.isEmpty(result.getCursor()));
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertFalse("Detail url", StringUtils.isEmpty(announce.getDetailUrl()));
            assertFalse("Image url", StringUtils.isEmpty(announce.getImgUrl()));
            assertFalse("Short desc", StringUtils.isEmpty(announce.getShortDesc()));
            assertFalse("Title", StringUtils.isEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertFalse("Deep processed", announce.wasDeepProcessed());
            //assertTrue("Price", announce.getPrice() > 0); //trattativa riservata
        }
    }
    
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
