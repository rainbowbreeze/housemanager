/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.NetworkManager;
import it.rainbowbreeze.housemanager.logic.ScraperUtils;

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
public class TecnocasaAgent extends HouseAgentAbstract {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = TecnocasaAgent.class.getSimpleName();

    private static final String DOMAIN_SITE = "TecnocasaIt";

    //public for testing purposes
    private static final String URL_RESULT_PAGE = "http://www.tecnocasa.it/annunci/immobili/lombardia/pavia.html?searchRequest.radius=3&searchRequest.townId=13800324508618&searchRequest.price=0&searchRequest.destinationProperty=CIVIL&searchRequest.squareMeters=0&searchRequest.mission=acquis&searchRequest.pageSize=20&searchRequest.searchId=it_236952020&searchRequest.pageNumber=";
    private static final String URL_DETAIL_ANNOUNCE_BASE = "http://www.tecnocasa.it";

    // -------------------------------------------- Constructors
    public TecnocasaAgent(ILogFacility logFacility, NetworkManager networkManager) {
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
                ? URL_RESULT_PAGE + "0"
                : URL_RESULT_PAGE + cursor;
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

    /**
     * Finds the number of total pages to query based on results of first query page
     * 
     * @param doc doc to analyze
     * @return
     */
    protected int findTotalPages(Document doc) {
        String pageCount = null;
        try {
            String hrefPage = doc.select("div.searchPaginator").first().getElementsByTag("a").last().attr("href").trim();
            pageCount = ScraperUtils.getTextBetween(hrefPage, "&searchRequest.pageNumber=", "&");
            if (StringUtils.isEmpty(pageCount)) pageCount = ScraperUtils.getTextBetween(hrefPage, "&searchRequest.pageNumber=", null);
            return Integer.parseInt(pageCount);
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of total pages: " + pageCount);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find page counter element");
        }
        return 0;
    }
    
    
    @Override
    protected String findNextPageCursor(Document doc) {
        String pageCount = null;
        try {
            Elements hrefPages = doc.select("div.searchPaginator").first().getElementsByTag("a");
            Element nextElem = null;
            for (Element href : hrefPages) {
                if ("".equals(href.text().trim())) {
                    nextElem = href;
                    break;
                }
            }
            String hrefPage = nextElem.attr("href").trim();
            pageCount = ScraperUtils.getTextBetween(hrefPage, "&searchRequest.pageNumber=", "&");
            if (StringUtils.isEmpty(pageCount)) pageCount = ScraperUtils.getTextBetween(hrefPage, "&searchRequest.pageNumber=", null);
            return pageCount;
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of total pages: " + pageCount);
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find page counter element");
        }
        return null;
    }

    
    /**
     * Analyzes a single result of a global search and discover all the information for the house
     * 
     * @param announceElem
     * @return
     */
    protected HouseAnnounce parseAnnounceInSearchResult(Element announceElem) {
        HouseAnnounce announce = createAnnounce();
        boolean findData = false;

        try {
            Elements contentElems = announceElem.select("div.content-list").first().getElementsByTag("div").first().getElementsByTag("p");
            
            Element mqElem = contentElems.get(1);
            try {
                String area = ScraperUtils.getTextBetween(mqElem.text().trim(), "Mq", "ca.").trim();
                announce.setArea(Integer.parseInt(area));
                findData = true;
            } catch (Exception e) {}
            
            Element priceElem = contentElems.get(2);
            try {
                String price = ScraperUtils.getTextBetween(priceElem.text().trim(), "€", null).trim().replace(".", "");
                announce.setPrice(Integer.parseInt(price));
                findData = true;
            } catch (Exception e) {}

        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div#content-list");
        }
        
        try {
            Element detailElem = announceElem.getElementsByTag("ul").first();
            String detailUrlFull = detailElem.getElementsByTag("li").get(2).getElementsByTag("a").first().attr("href");
            if (StringUtils.isNotEmpty(detailUrlFull)) {
                announce.setDetailUrl(ScraperUtils.getTextBetween(detailUrlFull, null, "?"));
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find ul");
        }
        
        try {
            Element imgElem = announceElem.select("div.boxImage").first().select("img").first();
            String imgUrl = imgElem.attr("src");
            if (StringUtils.isNotEmpty(imgUrl)) {
                announce.setImgUrl(URL_DETAIL_ANNOUNCE_BASE + imgUrl);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div.wrap_img");
        }
        
        announce.setId(getUniqueKey(announce));

        return findData ? announce : null;
    }

    @Override
    protected String getResultListIdentifier() {
        return "div.searchList";
    }

    @Override
    protected boolean scrapeAnnounceDocument(Document doc, HouseAnnounce announce) {
        return false;
    }

    // ----------------------------------------- Private Classes
}
