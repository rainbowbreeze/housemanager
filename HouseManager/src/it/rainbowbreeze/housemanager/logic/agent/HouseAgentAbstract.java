package it.rainbowbreeze.housemanager.logic.agent;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce.AnnounceType;
import it.rainbowbreeze.housemanager.logic.NetworkManager;
import it.rainbowbreeze.housemanager.logic.ScraperUtils;

/**
 * Abstract class for house website scraper
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public abstract class HouseAgentAbstract  implements IHouseAgent {
    // ------------------------------------------ Private Fields
    private final String LOG_HASH;

    protected final ILogFacility mLogFacility;
    protected final NetworkManager mNetworkManager;


    // -------------------------------------------- Constructors
    public HouseAgentAbstract(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        mNetworkManager = networkManager;
        LOG_HASH = getLogHash();
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    /**
     * Get the URL of a search page from a given cursor. If the cursor is null, the first
     * search page URL is returned
     * 
     * @param cursor
     * @return
     */
    public abstract String getSearchUrlFromCursor(String cursor);


    public void initProcess() {
    }

    public void coolDown() {
    }

    public HouseAnnounce createAnnounce() {
        return new HouseAnnounce()
                .setDomainSite(getName())
                .setAnnounceType(AnnounceType.SELL)
                .setPrice(-1);
    }

    public String getTaskQueueName(Date date, HouseAnnounce announce) {
        if (null == announce) {
            return null;
        }
        String numericCode = getAnnounceBaseTaskQueueName(announce);
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
    
    public String getUniqueKey(HouseAnnounce announce) {
        if (null == announce) {
            return null;
        }
        return announce.getDomainSite() + "-" + announce.getDetailUrl();
    }

    public AnnounceScrapingResult scrapeAnnounce(HouseAnnounce announce) {
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
        
        boolean deeperProcessed = scrapeAnnounceDocument(doc, announce);
        announce.setDeepProcessed(deeperProcessed);
        result.setAnnounce(announce);
        return result;
    }
    
    public SearchPageAgentResult scrape() {
        return scrapePage(getSearchUrlFromCursor(null), getResultListIdentifier());
    }

    public SearchPageAgentResult scrape(String cursor) {
        return scrapePage(getSearchUrlFromCursor(cursor), getResultListIdentifier());
    }

    
    // ----------------------------------------- Private Methods

    /** 
     * Returns the log hash to use
     * @return
     */
    protected abstract String getLogHash();

    /**
     * Retrieve a unique string that can be used for announce task queue name
     * @param announce
     * @return
     */
    protected abstract String getAnnounceBaseTaskQueueName(HouseAnnounce announce);
    
    
    /**
     * Returns the identifier that selects the list of search result in the search result page
     * @return
     */
    protected abstract String getResultListIdentifier();

    /**
     * Analyzes a generic search result page
     * @param url URL to download for the search result
     * @param divContent the key to use to identify search result area of the downloaded page
     * @return
     */
    protected SearchPageAgentResult scrapePage(String url, String divContent) {
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
        result.setCursor(findNextPageCursor(doc));
        
        Elements announces = doc.select(divContent);
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
     * Finds next page cursor for a given search
     * @param doc
     * @return
     */
    protected abstract String findNextPageCursor(Document doc);


    /**
     * Finds total pages of a given search
     * @param doc
     * @return
     */
    protected abstract int findTotalPages(Document doc);
    

    /**
     * Parses a single announce in a given search
     * @param announceElem
     * @return
     */
    protected abstract HouseAnnounce parseAnnounceInSearchResult(Element announceElem);

    /**
     * Parses the house announce specific page
     * @param doc
     * @param announce
     * @return
     */
    protected abstract boolean scrapeAnnounceDocument(Document doc, HouseAnnounce announce);


    // ----------------------------------------- Private Classes

}