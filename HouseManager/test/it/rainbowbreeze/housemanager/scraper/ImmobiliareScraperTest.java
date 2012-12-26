/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.MockNetworkManager;
import it.rainbowbreeze.housemanager.logic.StreamHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        
        mNetworkManager.getUrlReplies().put(ImmobiliareScraper.URL_BASE, fileContent);
        
        ScrapingResult result = mScraper.scrape();
        assertNotNull(result);
        assertEquals(61, result.getTotalPages());
        assertEquals("15", result.getCursor());
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
