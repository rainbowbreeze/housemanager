/**
 * 
 */
package it.rainbowbreeze.housemanager.scraper;

import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public interface IHouseScraper {

    /**
     * Returns the agent name (unique among all agents)
     * @return
     */
    String getName();

    /**
     * Tasks to do before the scraping begins
     */
    void initProcess();

    /**
     * Tasks to do after the scraping has finished
     */
    void coolDown();
    
    /**
     * Scrapes the first page of a global search on selected site
     * @return
     */
    SearchPageScrapingResult scrape();

    /**
     * Scrapes for page 2 - n of a global search on selected site 
     * @param cursor
     * @return
     */
    SearchPageScrapingResult scrape(String cursor);

    
    /**
     * Adds information to an announce scraping his own page
     * @param announce
     * @return
     */
    AnnounceScrapingResult scrapeDeep(HouseAnnounce announce);
}
