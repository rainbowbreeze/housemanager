/**
 * 
 */
package it.rainbowbreeze.housemanager.common;


import static it.rainbowbreeze.housemanager.common.RainbowContractHelper.checkNotNull;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.logic.NetworkManager;
import it.rainbowbreeze.housemanager.logic.ScrapingAgentManager;
import it.rainbowbreeze.housemanager.scraper.IHouseScraper;
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
    
    public NetworkManager getNetworkManager() {
        return checkNotNull(RainbowServiceLocator.get(NetworkManager.class), NetworkManager.class);
    }
    
    public HouseAnnounceDao getHouseAnnounceDao() {
        return checkNotNull(RainbowServiceLocator.get(HouseAnnounceDao.class), HouseAnnounceDao.class);
    }
    
    public ScrapingAgentManager getScrapingAgentManager() {
        return checkNotNull(RainbowServiceLocator.get(ScrapingAgentManager.class), ScrapingAgentManager.class);
    }

    /** TODO remove */
    public IHouseScraper getImmobiliareScraper() {
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
       
       IHouseScraper immobiliareScraper = new ImmobiliareScraper(logFacility, networkManager);
       RainbowServiceLocator.put(immobiliareScraper);
       HouseAnnounceDao houseAnnounceDao = new HouseAnnounceDao(logFacility);
       RainbowServiceLocator.put(houseAnnounceDao);
       ScrapingAgentManager scrapingAgentManager = new ScrapingAgentManager(logFacility);
       RainbowServiceLocator.put(scrapingAgentManager);
    }

}
