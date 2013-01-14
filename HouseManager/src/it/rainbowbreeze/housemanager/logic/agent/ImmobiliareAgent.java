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
public class ImmobiliareAgent implements IHouseAgent {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ImmobiliareAgent.class.getSimpleName();

    private static final String DOMAIN_SITE = "ImmobiliareIt";

    public static final String URL_FIRST_RESULT_PAGE = "http://www.immobiliare.it/Pavia/vendita_case-Pavia.html?criterio=rilevanza";
    public static final String URL_NEXT_RESULT_PAGE_BASE = "http://www.immobiliare.it";
    public static final String URL_DETAIL_ANNOUNCE_BASE = "http://www.immobiliare.it";
    
    private final ILogFacility mLogFacility;
    private final NetworkManager mNetworkManager;

    // -------------------------------------------- Constructors
    public ImmobiliareAgent(ILogFacility logFacility, NetworkManager networkManager) {
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
        return scrapePage(URL_FIRST_RESULT_PAGE);
    }

    public SearchPageAgentResult scrape(String cursor) {
        return scrapePage(URL_NEXT_RESULT_PAGE_BASE + cursor);
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
        
        if (null != text) {
            Elements announces = doc.select("div.contenuto_box");
            for (Element announceElem : announces) {
                HouseAnnounce announce = parseAnnounceInSearchResult(announceElem);
                if (null != announce) {
                    result.getAnnounces().add(announce);
                } else {
                    result.addError();
                }
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
    private String findNextPage(Document doc) {
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
    private HouseAnnounce parseAnnounceInSearchResult(Element announceElem) {
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
                int price = Integer.parseInt(priceStr);
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
