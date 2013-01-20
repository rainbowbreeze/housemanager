/**
 * 
 */
package it.rainbowbreeze.housemanager.logic.agent;

import java.util.Date;

import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public interface IHouseAgent {

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
    SearchPageAgentResult scrape();

    /**
     * Scrapes for page 2 - n of a global search on selected site 
     * @param cursor
     * @return
     */
    SearchPageAgentResult scrape(String cursor);

    /**
     * Adds information to an announce scraping his own page
     * @param announce
     * @return
     */
    AnnounceScrapingResult scrapeAnnounce(HouseAnnounce announce);
    
    /**
     * Returns a unique key for the given announce
     * @param announce
     * @return
     */
    String getUniqueKey(HouseAnnounce announce);
    
    /**
     * Returns a valid task queue name [a-zA-Z\d_-] for the given announce
     * 
     * @param date
     * @param announce
     * @return
     */
    String getTaskQueueName(Date date, HouseAnnounce announce);
    

    /**
     * Returns a valid task queue name [a-zA-Z\d_-] for the given cursor
     * 
     * @param date
     * @param cursor
     * @return
     */
    String getTaskQueueName(Date date, String cursor);

    /**
     * Returns a new {@link HouseAnnounce} with basic field initialized
     * @return
     */
    HouseAnnounce createAnnounce();
}
