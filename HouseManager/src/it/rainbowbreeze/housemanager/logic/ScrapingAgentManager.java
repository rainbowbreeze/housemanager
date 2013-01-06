/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import java.util.List;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.scraper.IHouseScraper;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ScrapingAgentManager {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ScrapingAgentManager.class.getSimpleName();
    private final ILogFacility mLogFacility;
    
    private List<IHouseScraper> mAgents;
    
    // -------------------------------------------- Constructors
    public ScrapingAgentManager(ILogFacility logFacility) {
        mLogFacility = logFacility;
        
        //registers all the scraping agents
        mAgents.add(App.i().getImmobiliareScraper());
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public void goScrape() {
        for (IHouseScraper agent : mAgents) {
            mLogFacility.d(LOG_HASH, "Queuing scraping agent " + agent.getName());
            //creates a new queue for each agent
        }
        //load all the registered scraping agents
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
