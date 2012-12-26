/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import static org.junit.Assert.*;
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

import com.sun.source.tree.AssertTree;

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
        
        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_BASE, fileContent);
        
        ScrapingResult result = mScraper.scrape();
        assertNotNull(result);
        assertTrue(result.hasMoreResults());
        assertFalse(result.hasErrors());
        assertEquals(61, result.getTotalPages());
        assertEquals(15, result.getAnnounces().size());
        
        for (HouseAnnounce announce : result.getAnnounces()) {
            assertFalse("Detail url", StringUtils.isEmpty(announce.getDetailUrl()));
            assertFalse("Image url", StringUtils.isEmpty(announce.getImgUrl()));
            assertFalse("Short desc", StringUtils.isEmpty(announce.getShortDesc()));
            assertFalse("Title", StringUtils.isEmpty(announce.getTitle()));
            assertTrue("Area", announce.getArea() > 0);
            assertTrue("Price", announce.getPrice() > 0);
        }
        
        assertEquals("15", result.getCursor());
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
