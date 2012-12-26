/**
 * 
 */
package it.rainbowbreeze.housemanager.common;


import static it.rainbowbreeze.housemanager.common.RainbowContractHelper.*;
import it.rainbowbreeze.housemanager.logic.NetworkManager;
import it.rainbowbreeze.housemanager.scraper.ImmobiliareScraper;

/**
 * Basic class for dependency injection and global bag of values
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class App {
    // ------------------------------------------ Private Fields
    private static App mApp = null;

    private static final String HOUSE_MANAGER = "HouseManager";

    // -------------------------------------------- Constructors
    private App() {
        initValues();
    }

    // --------------------------------------- Public Properties
    public ILogFacility getLogFacility() {
        return checkNotNull(RainbowServiceLocator.get(LogFacility.class), LogFacility.class);
    }
    public ImmobiliareScraper getImmobiliareScraper() {
        return checkNotNull(RainbowServiceLocator.get(ImmobiliareScraper.class), ImmobiliareScraper.class);
    }
    
    // ------------------------------------------ Public Methods
    public synchronized static App i() {
        if (null == mApp) {
            mApp = new App();
        }
        return mApp;
    }

    // ----------------------------------------- Private Methods
    private void initValues() {
       LogFacility logFacility = new LogFacility(HOUSE_MANAGER);
       RainbowServiceLocator.put(logFacility);
       NetworkManager networkManager = new NetworkManager(logFacility);
       RainbowServiceLocator.put(networkManager);
       
       ImmobiliareScraper immobiliareScraper = new ImmobiliareScraper(logFacility, networkManager);
       RainbowServiceLocator.put(immobiliareScraper);
    }

}
