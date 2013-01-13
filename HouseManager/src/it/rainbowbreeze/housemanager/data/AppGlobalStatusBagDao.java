/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.AppGlobalStatusBag;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.List;

import com.googlecode.objectify.ObjectifyService;

/**
 * DAO for {@link HouseAnnounce} object
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class AppGlobalStatusBagDao extends ObjectifyAbstractDao<AppGlobalStatusBag> {
    // ------------------------------------------ Private Fields
    static final String LOG_HASH = AppGlobalStatusBagDao.class.getSimpleName();
    
    // -------------------------------------------- Constructors
    public AppGlobalStatusBagDao(ILogFacility logFacility) {
        super(logFacility);
    }

    static {
        ObjectifyService.register(AppGlobalStatusBag.class);
    }

    // --------------------------------------- Public Properties
    public AppGlobalStatusBag get() {
        List<AppGlobalStatusBag> bag = getAll(AppGlobalStatusBag.class);
        if (null != bag && bag.size() > 0) {
            return bag.get(0);
        } else {
            return new AppGlobalStatusBag();
        }
    }

    public void deleteAll() {
        super.deleteAll(AppGlobalStatusBag.class);
    }

    // ----------------------------------------- Private Methods
    @Override
    protected String getLogHash() {
        return LOG_HASH;
    }

    // ----------------------------------------- Private Classes
}
