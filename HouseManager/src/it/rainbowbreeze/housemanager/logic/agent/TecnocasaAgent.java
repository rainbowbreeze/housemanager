/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.NetworkManager;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
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
public class TecnocasaAgent implements IHouseAgent {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = TecnocasaAgent.class.getSimpleName();

    private static final String DOMAIN_SITE = "TecnocasaIt";

    //public for testing purposes
    public static final String URL_FIRST_RESULT_PAGE = "http://www.tecnocasa.it/annunci/immobili/lombardia/pavia.html?searchRequest.radius=3&searchRequest.townId=13800324508618&searchRequest.price=0&searchRequest.destinationProperty=CIVIL&searchRequest.squareMeters=0&searchRequest.mission=acquis&searchRequest.pageSize=20&searchRequest.searchId=it_236952020&searchRequest.pageNumber=";
    private static final String URL_DETAIL_ANNOUNCE_BASE = "http://www.immobiliare.it";
    
    private final ILogFacility mLogFacility;
    private final NetworkManager mNetworkManager;

    // -------------------------------------------- Constructors
    public TecnocasaAgent(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        mNetworkManager = networkManager;
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public String getName() {
        return DOMAIN_SITE;
    }
    
    public void initProcess() {
    }
    
    public void coolDown() {
        
    }
    
    public SearchPageAgentResult scrape() {
        return scrapePage(URL_FIRST_RESULT_PAGE + "0");
    }

    public SearchPageAgentResult scrape(String cursor) {
        return scrapePage(URL_FIRST_RESULT_PAGE + cursor);
    }

    public AnnounceScrapingResult scrapeDeep(HouseAnnounce announce) {
        return scrapeAnnounce(announce);
    }
    
    public String getUniqueKey(HouseAnnounce announce) {
        if (null == announce) {
            return null;
        }
        return announce.getDomainSite() + "-" + announce.getDetailUrl();
    }

    public String getTaskQueueName(Date date, HouseAnnounce announce) {
        if (null == announce) {
            return null;
        }
        String numericCode = ScraperUtils.getTextBetween(announce.getDetailUrl(), URL_DETAIL_ANNOUNCE_BASE + "/", "-");
        StringBuilder sb = new StringBuilder();
        sb.append(announce.getDomainSite()).append("_")
                .append(ScraperUtils.getyyyyMMdd(date))
                .append("-Ann_")
                .append(numericCode);
        return sb.toString();
    }
    
    public String getTaskQueueName(Date date, String cursor) {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("_")
                .append(ScraperUtils.getyyyyMMdd(date))
                .append("-Pg_");
        if (StringUtils.isNotEmpty(cursor)) {
            //search for last =
            int pos = cursor.lastIndexOf("=");
            if (pos > -1) {
                //get only the final number
                sb.append(cursor.substring(pos+1));
            } else {
                //appends a random UUID
                sb.append(UUID.randomUUID().toString());
            }
        }
        return sb.toString();
    }
    
    @Override
    public HouseAnnounce createAnnounce() {
        return new HouseAnnounce()
                .setDomainSite(DOMAIN_SITE);
    }

    // ----------------------------------------- Private Methods
    
    /**
     * Analyzes a generic search result page
     * @param url
     * @return
     */
    private SearchPageAgentResult scrapePage(String url) {
        SearchPageAgentResult result = new SearchPageAgentResult();
        
        mLogFacility.d(LOG_HASH, "Scraping for result page " + url);

        String text = null;
        try {
            text = mNetworkManager.getUrlContent(url);
        } catch (Exception e) {
            //TODO add error here
            e.printStackTrace();
            result.addError();
            return result;
        }

        Document doc = Jsoup.parse(text);
        
        if (null == doc) {
            //TODO add error here
            result.addError();
            return result;
        }
        
        //finds total pages
        result.setTotalPages(findTotalPages(doc));
        //finds cursor to open next search page
        result.setCursor(findNextPage(doc));
        
        Elements announces = doc.select("div.searchList");
        for (Element announceElem : announces) {
            HouseAnnounce announce = parseAnnounceInSearchResult(announceElem);
            if (null != announce) {
                result.getAnnounces().add(announce);
            } else {
                result.addError();
            }
        }
        
        return result;
    }
    

    /**
     * Finds the number of total pages to query based on results of first query page
     * 
     * @param doc doc to analyze
     * @return
     */
    private int findTotalPages(Document doc) {
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
    
    
    /**
     * Finds link for the next search page result
     * @param doc
     * @return
     */
    private String findNextPage(Document doc) {
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
    private HouseAnnounce parseAnnounceInSearchResult(Element announceElem) {
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
        
        announce.setId(getUniqueKey(announce));

        return findData ? announce : null;
    }


    private AnnounceScrapingResult scrapeAnnounce(HouseAnnounce announce) {
        AnnounceScrapingResult result = new AnnounceScrapingResult();

        if (null == announce || StringUtils.isEmpty(announce.getDetailUrl())) {
            //TODO add error here
            result.addError();
            return result;
        }

        mLogFacility.d(LOG_HASH, "Scraping for announce " + announce.getDetailUrl());
        String text = null;
        try {
            text = mNetworkManager.getUrlContent(announce.getDetailUrl());
        } catch (Exception e) {
            //TODO add error here
            e.printStackTrace();
            result.addError();
            return result;
        }

        Document doc = Jsoup.parse(text);

        if (null == doc) {
            //TODO add error here
            result.addError();
            return result;
        }
        
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
        
        announce.setDeepProcessed(deeperProcessed);

        result.setAnnounce(announce);
        return result;
    }

    // ----------------------------------------- Private Classes
}
