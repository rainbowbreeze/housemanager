/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import com.google.appengine.api.datastore.Entity;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class HouseAnnounceDao extends AbstractEntityDao<Entity> {
    private static final String LOG_HASH = HouseAnnounceDao.class.getSimpleName();

    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public HouseAnnounceDao(ILogFacility logFacility) {
        super(logFacility);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
    @Override
    protected String getKind() {
        return "HouseAnnounce";
    }

    @Override
    protected String getLogHash() {
        return LOG_HASH;
    }

    protected Entity fromEntityToObject(Entity entity) {
        if (null == entity) return null;
        
        HouseAnnounce houseAnnounce = new HouseAnnounce();
//        if (entity.hasProperty("test") {
//            houseAnnounce.setXXXX(entity.getProperty("test"));
//        }
        return null;
    }

    protected Entity fromObjectToEntity(Entity object) {
        return null;
    }

    // ----------------------------------------- Private Classes
}
