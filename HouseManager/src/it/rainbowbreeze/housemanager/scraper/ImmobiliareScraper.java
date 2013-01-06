/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.NetworkManager;

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
public class ImmobiliareScraper {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ImmobiliareScraper.class.getSimpleName();

    private static final String DOMAIN_SITE = "Immobiliare.it";

    public static final String URL_FIRST_RESULT_PAGE = "http://www.immobiliare.it/Pavia/vendita_case-Pavia.html?criterio=rilevanza";
    public static final String URL_NEXT_RESULT_PAGE_BASE = "http://www.immobiliare.it";
    public static final String URL_DETAIL_ANNOUNCE_BASE = "http://www.immobiliare.it";
    
    private final ILogFacility mLogFacility;
    private final NetworkManager mNetworkManager;

    // -------------------------------------------- Constructors
    public ImmobiliareScraper(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        mNetworkManager = networkManager;
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    
    /**
     * Scrapes the first page of a global search on selected site
     * @return
     */
    public SearchPageScrapingResult scrape() {
        return scrapePage(URL_FIRST_RESULT_PAGE);
    }

    /**
     * Scrapes for page 2 - n of a global search on selected site 
     * @param cursor
     * @return
     */
    public SearchPageScrapingResult scrapeNext(String cursor) {
        return scrapePage(URL_NEXT_RESULT_PAGE_BASE + cursor);
    }
    
    /**
     * Adds information to an announce scraping his own page
     * @param announce
     * @return
     */
    public AnnounceScrapingResult scrapeDeep(HouseAnnounce announce) {
        return scrapeAnnounce(announce);
    }
    

    // ----------------------------------------- Private Methods
    
    /**
     * Analyzes a generic search result page
     * @param url
     * @return
     */
    private SearchPageScrapingResult scrapePage(String url) {
        SearchPageScrapingResult result = new SearchPageScrapingResult();
        
        mLogFacility.info(LOG_HASH, "Scraping for result page " + url);

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
            mLogFacility.warn(LOG_HASH, "Wrong conversion of total pages: " + pageCount);
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find page counter element");
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
            mLogFacility.warn(LOG_HASH, "Cannot find div#paginazione");
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
        HouseAnnounce announce = new HouseAnnounce();
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
            mLogFacility.warn(LOG_HASH, "Cannot find div.annuncio_title");
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
            mLogFacility.warn(LOG_HASH, "Wrong conversion of price: " + priceStr);
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find span.price");
        }
        
        String areaStr = null;
        try {
            areaStr = announceElem.select("div.bottom").first().select("div.align_left").first().childNode(4).toString();
            areaStr = areaStr.replace("&nbsp;", "").replace("m&sup2;", "").trim();
            int area = Integer.parseInt(areaStr);
            findData = true;
            announce.setArea(area);
        } catch (NumberFormatException e) {
            mLogFacility.warn(LOG_HASH, "Wrong conversion of area: " + areaStr);
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find class.align_left");
        }
        
        try {
            String desc = announceElem.select("div.descrizione").first().text();
            if (StringUtils.isNotEmpty(desc)) {
                announce.setShortDesc(desc);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find div.descrizione");
        }
        
        try {
            Element imgElem = announceElem.select("div.wrap_img").first().select("img").first();
            String imgUrl = imgElem.attr("src");
            if (StringUtils.isNotEmpty(imgUrl)) {
                announce.setImgUrl(imgUrl);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find div.wrap_img");
        }
        
        announce.setDomainSite(DOMAIN_SITE);

        return findData ? announce : null;
    }


    private AnnounceScrapingResult scrapeAnnounce(HouseAnnounce announce) {
        AnnounceScrapingResult result = new AnnounceScrapingResult();

        if (null == announce || StringUtils.isEmpty(announce.getDetailUrl())) {
            //TODO add error here
            result.addError();
            return result;
        }

        mLogFacility.info(LOG_HASH, "Scraping for announce " + announce.getDetailUrl());
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
                announce.setDeepProcessed(true);
            }
        } catch (Exception e) {
            mLogFacility.warn(LOG_HASH, "Cannot find div.titolo_mappa");
        }
        
        result.setAnnounce(announce);
        return result;
    }

    
    // ----------------------------------------- Private Classes
}
