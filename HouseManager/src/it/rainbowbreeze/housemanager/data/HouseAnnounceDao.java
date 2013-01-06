/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

/**
 * DAO using Objectify
 * 
 * TODO: check what framework use
 * http://code.google.com/p/objectify-appengine/wiki/Concepts?tm=6
 * http://code.google.com/p/twig-persist/wiki/Comparison#Objectify_and_SimpleDS
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class HouseAnnounceDao {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = HouseAnnounceDao.class.getSimpleName();
    
    private final ILogFacility mLogFacility;

    // -------------------------------------------- Constructors
    public HouseAnnounceDao(ILogFacility logFacility) {
        mLogFacility = logFacility;
    }

    static {
        ObjectifyService.register(HouseAnnounce.class);
    }
    
    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public HouseAnnounce get(long id) {
        mLogFacility.d(LOG_HASH, "Load entity " + id);
        HouseAnnounce result = ofy().load()
                .type(HouseAnnounce.class)
                .filterKey("=", id)
                .first()
                .get();
        return result;
    }
    
    public List<HouseAnnounce> getAll() {
        mLogFacility.d(LOG_HASH, "Load all entities");
        List<HouseAnnounce> entities = ofy().load()
                .type(HouseAnnounce.class)
                .list();
        return entities;
    }
    
    public int count() {
        Iterable<Key<HouseAnnounce>> allKeysIterator = ofy().load().type(HouseAnnounce.class).keys();
        if (null != allKeysIterator) {
            ArrayList<Key<HouseAnnounce>> allKeys = Lists.newArrayList(allKeysIterator);
            return allKeys.size();
        } else {
            return 0;
        }
    }
    
    public Key<HouseAnnounce> save(HouseAnnounce newItem) {
        mLogFacility.d(LOG_HASH, "Save entity");
        Key<HouseAnnounce> result = ofy().save().entity(newItem).now();
        return result;
    }
    
    public void deleteAll() {
        mLogFacility.d(LOG_HASH, "Delete all entities");
        ofy().delete()
                .type(HouseAnnounce.class);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
