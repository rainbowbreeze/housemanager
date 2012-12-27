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
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ImmobiliareScraper {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ImmobiliareScraper.class.getSimpleName();

    public static final String URL_FIRST_QUERY = "http://www.immobiliare.it/Pavia/vendita_case-Pavia.html?criterio=rilevanza";
    public static final String URL_NEXT_QUERY = "http://www.immobiliare.it";
    
    private final ILogFacility mLogFacility;
    private final NetworkManager mNetworkManager;

    // -------------------------------------------- Constructors
    public ImmobiliareScraper(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        mNetworkManager = networkManager;
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public SearchPageScrapingResult scrape() {
        return scrapePage(URL_FIRST_QUERY);
    }

    public SearchPageScrapingResult scrapeNext(String cursor) {
        return scrapePage(URL_NEXT_QUERY + cursor);
    }
    
    // ----------------------------------------- Private Methods
    
    private SearchPageScrapingResult scrapePage(String url) {
        String text = null;
        try {
            text = mNetworkManager.getUrlContent(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Document doc = Jsoup.parse(text);
        
        //find total pages
        SearchPageScrapingResult result = new SearchPageScrapingResult();

        if (null == doc) {
            //TODO add error here
            return result;
        }
        
        result.setTotalPages(findTotalPages(doc));
        result.setCursor(findNextPage(doc));
        
        if (null != text) {
            Elements announces = doc.select("div.contenuto_box");
            for (Element announceElem : announces) {
                HouseAnnounce announce = parseAnnounce(announceElem);
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
     * Finds link for the next page
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
     * Analyzes a single element and discover all the information for the house
     * @param announceElem
     * @return
     */
    private HouseAnnounce parseAnnounce(Element announceElem) {
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

        return findData ? announce : null;
    }


    // ----------------------------------------- Private Classes
}
