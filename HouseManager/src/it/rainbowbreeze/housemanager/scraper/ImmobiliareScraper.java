/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.logic.NetworkManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ImmobiliareScraper {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ImmobiliareScraper.class.getSimpleName();

    public static final String URL_BASE = "http://www.immobiliare.it/Pavia/vendita_case-Pavia.html?criterio=rilevanza";
    
    private final ILogFacility mLogFacility;
    private final NetworkManager mNetworkManager;

    // -------------------------------------------- Constructors
    public ImmobiliareScraper(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        mNetworkManager = networkManager;
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public ScrapingResult scrape() {
        String text = null;
        try {
            text = mNetworkManager.getUrlContent(URL_BASE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Document doc = Jsoup.parse(text);

        //find total pages
        ScrapingResult result = new ScrapingResult();
        result.setTotalPages(findTotalPages(doc));
        result.setCursor(findTotalAds(doc) + "");
        
        if (null != text) {
            Elements announces = doc.select("div.contenuto_box");
            result.setCursor(announces.size() + "");
        }
        
        return result;
    }

    // ----------------------------------------- Private Methods
    /**
     * Finds the number of total pages to query based on results of first query page
     * 
     * @param doc doc to analyze
     * @return
     */
    private int findTotalPages(Document doc) {
        String pageCount = null;
        Elements pageCountElems = doc.select("div#pageCount");
        for (Element pageElem : pageCountElems) {
            Elements strongElems = pageElem.select("strong");
            for (Element strong : strongElems) {
                if (!strong.childNodes().isEmpty()) {
                    pageCount = strong.childNode(0).toString();
                }
            }
        }
        try {
            return Integer.parseInt(pageCount);
        } catch (NumberFormatException e) {
            mLogFacility.info(LOG_HASH, "Wrong conversion of total pages: " + pageCount);
            return 0;
        }
    }
    
    private int findTotalAds(Document doc) {
        // TODO Auto-generated method stub
        return 0;
    }



    // ----------------------------------------- Private Classes
}
