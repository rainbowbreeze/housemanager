/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.NetworkManager;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Specialized scraper for Immobiliare.it website
 * 
 * It uses Jsoup library to parse the HTML DOM
 *  http://jsoup.org/cookbook/extracting-data/dom-navigation
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ImmobiliareAgent extends HouseAgentAbstract {

    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ImmobiliareAgent.class.getSimpleName();

    static final String DOMAIN_SITE = "ImmobiliareIt";

    //public for tests
    private static final String URL_FIRST_RESULT_PAGE = "http://www.immobiliare.it/Pavia/vendita_case-Pavia.html?criterio=rilevanza";
    private static final String URL_NEXT_RESULT_PAGE_BASE = "http://www.immobiliare.it";
    private static final String URL_DETAIL_ANNOUNCE_BASE = "http://www.immobiliare.it/";

    
    // -------------------------------------------- Constructors
    public ImmobiliareAgent(ILogFacility logFacility, NetworkManager networkManager) {
        super(logFacility, networkManager);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public String getName() {
        return DOMAIN_SITE;
    }
    
    @Override
    public String getSearchUrlFromCursor(String cursor) {
        return StringUtils.isEmpty(cursor)
                ? URL_FIRST_RESULT_PAGE
                : URL_NEXT_RESULT_PAGE_BASE + cursor;
    }
    

    // ----------------------------------------- Private Methods
    @Override
    protected String getLogHash() {
        return LOG_HASH;
    }
    
    @Override
    protected String getAnnounceBaseTaskQueueName(HouseAnnounce announce) {
        return ScraperUtils.getTextBetween(announce.getDetailUrl(), URL_DETAIL_ANNOUNCE_BASE, "-");
    }

    @Override
    protected String getResultListIdentifier() {
        return "div.contenuto_box";
    }

    /**
     * Finds the number of total pages to query based on results of first query page
     * 
     * @param doc doc to analyze
     * @return
     */
    @Override
    protected int findTotalPages(Document doc) {
        String pageCount = null;
        try {
            pageCount = doc.select("div#pageCount").first().getElementsByTag("strong").last().text().trim();
            return Integer.parseInt(pageCount);
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of total pages: " + pageCount);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find page counter element");
        }
        return 0;
    }
    
    
    /**
     * Finds link for the next search page result
     * @param doc
     * @return
     */
    @Override
    protected String findNextPageCursor(Document doc) {
        try {
            Elements nextPageElems = doc.select("div#paginazione").first().select("a");
            for (Element nextPageElem : nextPageElems) {
                if (nextPageElem.className().startsWith("no-decoration button next_page")) {
                    String nextPageUrl = nextPageElem.attr("href");
                    return "#".equalsIgnoreCase(nextPageUrl) ? null : nextPageUrl;
                }
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div#paginazione");
        }
        return null;
    }

    
    /**
     * Analyzes a single result of a global search and discover all the information for the house
     * 
     * @param announceElem
     * @return
     */
    @Override
    protected HouseAnnounce parseAnnounceInSearchResult(Element announceElem) {
        HouseAnnounce announce = createAnnounce();
        boolean findData = false;

        try {
            Element linkElem = announceElem.select("div.annuncio_title").first().getElementsByTag("a").first();
            String title = linkElem.text();
            if (StringUtils.isNotEmpty(title)) {
                announce.setTitle(title);
                findData = true;
            }
            String url = linkElem.attr("href");
            if (StringUtils.isNotEmpty(url)) {
                announce.setDetailUrl(url);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div.annuncio_title");
        }
        
        String priceStr = null;
        try {
            priceStr = announceElem.select("span.price").first().text();
            priceStr = priceStr.replace("€", "").replace(".", "").trim();
            if ("Trattative riservate".equalsIgnoreCase(priceStr)) {
                findData = true;
                announce.setPrice(0);
            } else {
                int price;
                try {
                    price = Integer.parseInt(priceStr);
                } catch (NumberFormatException e) {
                    //sometimes price is 300.001 - 500.000 €
                    priceStr = ScraperUtils.getTextBetween(priceStr, null, "-").trim();
                    price = Integer.parseInt(priceStr);
                }
                findData = true;
                announce.setPrice(price);
            }
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of price: " + priceStr);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find span.price");
        }
        
        String areaStr = null;
        try {
            areaStr = announceElem.select("div.bottom").first().select("div.align_left").first().childNode(4).toString();
            areaStr = areaStr.replace("&nbsp;", "").replace("m&sup2;", "").trim();
            int area = Integer.parseInt(areaStr);
            findData = true;
            announce.setArea(area);
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of area: " + areaStr);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find class.align_left");
        }
        
        try {
            String desc = announceElem.select("div.descrizione").first().text();
            if (StringUtils.isNotEmpty(desc)) {
                announce.setShortDesc(desc);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div.descrizione");
        }
        
        try {
            Element imgElem = announceElem.select("div.wrap_img").first().select("img").first();
            String imgUrl = imgElem.attr("src");
            if (StringUtils.isNotEmpty(imgUrl)) {
                announce.setImgUrl(imgUrl);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div.wrap_img");
        }
        
        announce.setId(getUniqueKey(announce));

        return findData ? announce : null;
    }

    @Override
    protected boolean scrapeAnnounceDocument(Document doc, HouseAnnounce announce) {
        boolean deeperProcessed = false;
        
        //data to add
        //lat/long
        try {
            Element mapsElem = doc.select("div#titolo_mappa").first().getElementsByTag("script").last();
            String scriptContent = mapsElem.toString();
            String lat = ScraperUtils.getTextBetween(scriptContent, "__g_latitudine = \"", "\";");
            String lon = ScraperUtils.getTextBetween(scriptContent, "__g_longitudine = \"", "\";");
            if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lon)) {
                announce.setLat(lat);
                announce.setLon(lon);
                deeperProcessed = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div#titolo_mappa");
        }
        
        //complete description
        try {
            String desc = doc.select("div.descrizione").first().text();
            if (StringUtils.isNotEmpty(desc)) {
                announce.setShortDesc(desc);
                deeperProcessed = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div.descrizione");
        }

        return deeperProcessed;
    }
    
    // ----------------------------------------- Private Classes
}
