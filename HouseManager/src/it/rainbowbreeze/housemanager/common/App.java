/**
 * 
 */
package it.rainbowbreeze.housemanager.common;


import static it.rainbowbreeze.housemanager.common.RainbowContractHelper.checkNotNull;
import it.rainbowbreeze.housemanager.data.AppGlobalStatusBagDao;
import it.rainbowbreeze.housemanager.data.CacheManager;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.logic.HouseAgentsManager;
import it.rainbowbreeze.housemanager.logic.JsonHelper;
import it.rainbowbreeze.housemanager.logic.NetworkManager;
import it.rainbowbreeze.housemanager.logic.agent.IHouseAgent;
import it.rainbowbreeze.housemanager.logic.agent.ImmobiliareAgent;

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
    
    public AppGlobalStatusBagDao getAppGlobalStatusBagDao() {
        return checkNotNull(RainbowServiceLocator.get(AppGlobalStatusBagDao.class), AppGlobalStatusBagDao.class);
    }

    public HouseAgentsManager getHouseAgentsManager() {
        return checkNotNull(RainbowServiceLocator.get(HouseAgentsManager.class), HouseAgentsManager.class);
    }

    public JsonHelper getJsonHelper() {
        return checkNotNull(RainbowServiceLocator.get(JsonHelper.class), JsonHelper.class);
    }
    
    public CacheManager getCacheManager() {
        return checkNotNull(RainbowServiceLocator.get(CacheManager.class), CacheManager.class);
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
       CacheManager cacheManager = new CacheManager(logFacility);
       RainbowServiceLocator.put(cacheManager);
       
       IHouseAgent immobiliareScraper = new ImmobiliareAgent(logFacility, networkManager);
       RainbowServiceLocator.put(immobiliareScraper);
       HouseAnnounceDao houseAnnounceDao = new HouseAnnounceDao(logFacility);
       RainbowServiceLocator.put(houseAnnounceDao);
       AppGlobalStatusBagDao appGlobalStatusBagDao = new AppGlobalStatusBagDao(logFacility);
       RainbowServiceLocator.put(appGlobalStatusBagDao);
       HouseAgentsManager houseAgentsManager = new HouseAgentsManager(logFacility, networkManager);
       RainbowServiceLocator.put(houseAgentsManager);
       JsonHelper jsonHelper = new JsonHelper(logFacility);
       RainbowServiceLocator.put(jsonHelper);
    }

}
