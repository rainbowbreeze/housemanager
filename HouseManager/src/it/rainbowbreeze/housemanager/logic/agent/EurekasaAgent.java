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

/**
 * Specialized scraper for Eurekasa.it website
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class EurekasaAgent extends HouseAgentAbstract {

    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = EurekasaAgent.class.getSimpleName();

    static final String DOMAIN_SITE = "EurekasaIt";

    private static final String URL_RESULT_PAGE = "http://annunci-casa.eurekasa.it/vendita/residenziale/Lombardia/Pavia/Pavia/XXXX/Pavia.html?mappa=1";
    private static final String URL_DETAIL_ANNOUNCE_BASE = "http://annunci-casa.eurekasa.it/vendita/residenziale/Lombardia/Pavia/";
    private static final String URL_BASE_SITE = "http://annunci-casa.eurekasa.it";

    
    // -------------------------------------------- Constructors
    public EurekasaAgent(ILogFacility logFacility, NetworkManager networkManager) {
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
                ? URL_RESULT_PAGE.replace("XXXX", "pag1")
                : URL_RESULT_PAGE.replace("XXXX", cursor) + cursor;
    }
    
    
    // ----------------------------------------- Private Methods
    @Override
    protected String getLogHash() {
        return LOG_HASH;
    }
    
    @Override
    protected String getAnnounceBaseTaskQueueName(HouseAnnounce announce) {
        return ScraperUtils.getTextBetween(announce.getDetailUrl(), URL_DETAIL_ANNOUNCE_BASE, ".html");
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
            pageCount = doc.select("div.padder").get(0).getElementsByTag("div").first().text().trim();
            pageCount = ScraperUtils.getTextBetween(pageCount, "vendita: ", " Ordina");
            return Integer.parseInt(pageCount)  / 15;  //14 announces per page
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
            String cursorUrl = doc.select("li#next").first().select("a").first().attr("href");
            String cursor = ScraperUtils.getTextBetween(cursorUrl, "Pavia/Pavia/", "/Pavia.html");
            return cursor;
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find li#next");
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

        String id = announceElem.attr("id");
        id = ScraperUtils.getTextBetween(id, "snipAd-", null);
        
        try {
            String link = announceElem.select("a#titleLink-" + id).first().attr("href");
            if (StringUtils.isNotEmpty(link)) {
                announce.setDetailUrl(link);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find a#titleLink-");
        }
        
        try {
            String title = announceElem.select("b#title-" + id).first().text();
            if (StringUtils.isNotEmpty(title)) {
                announce.setTitle(title);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find b#titleLink-");
        }
        
        try {
            String imgSrc = announceElem.select("img#image-" + id).first().attr("src");
            if (StringUtils.isNotEmpty(imgSrc)) {
                announce.setImgUrl(URL_BASE_SITE + imgSrc);
                findData = true;
            }
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find img#image-");
        }
        
        try {
            String priceStr = announceElem.select("div#prezzo-" + id).first().text();
            if (StringUtils.isEmpty(ScraperUtils.getTextBetween(priceStr, null, "&nbsp;€"))) {
                priceStr = ScraperUtils.getTextBetween(priceStr, null, " €");
            } else {
                priceStr = ScraperUtils.getTextBetween(priceStr, null, "&nbsp;€");
            }
            priceStr = priceStr.replace(".", "");
            int price = Integer.parseInt(priceStr);
            announce.setPrice(price);
            findData = true;
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of price for announce " + announce.getDetailUrl());
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div#prezzo-");
        }
        
        try {
            String areaStr = announceElem.select("div#superficie-" + id).first().text();
            areaStr = ScraperUtils.getTextBetween(areaStr, null, "m").trim();
            int area = Integer.parseInt(areaStr);
            announce.setArea(area);
            findData = true;
        } catch (NumberFormatException e) {
            mLogFacility.w(LOG_HASH, "Wrong conversion of area for announce " + announce.getDetailUrl());
        } catch (Exception e) {
            mLogFacility.w(LOG_HASH, "Cannot find div#superficie-");
        }
        
        announce.setId(getUniqueKey(announce));

        return findData ? announce : null;
    }

    @Override
    protected String getResultListIdentifier() {
        return "div.snipAd";
    }

    @Override
    protected boolean scrapeAnnounceDocument(Document doc, HouseAnnounce announce) {
        // TODO Auto-generated method stub
        return false;
    }

    // ----------------------------------------- Private Classes
}
