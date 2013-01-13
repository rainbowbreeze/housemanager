package it.rainbowbreeze.housemanager.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import it.rainbowbreeze.housemanager.common.ILogFacility;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.googlecode.objectify.Key;

/**
 * Generic DAO using Objectify framework
 * 
 * Frameworks to use
 * http://code.google.com/p/objectify-appengine/wiki/Concepts?tm=6
 * http://code.google.com/p/twig-persist/wiki/Comparison#Objectify_and_SimpleDS
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 * @param <Entity>
 */
public abstract class ObjectifyAbstractDao <Entity> {
    // ------------------------------------------ Private Fields

    protected final ILogFacility mLogFacility;
    private final String LOG_HASH;

    // -------------------------------------------- Constructors
    public ObjectifyAbstractDao(ILogFacility logFacility) {
        mLogFacility = logFacility;
        LOG_HASH = getLogHash();
    }

    
    // --------------------------------------- Public Properties

    // ------------------------------------------- Public Methods

    public Key<Entity> save(Entity newItem) {
        mLogFacility.d(LOG_HASH, "Save entity");
        Key<Entity> result = ofy().save().entity(newItem).now();
        return result;
    }

    // ----------------------------------------- Private Methods
    protected Entity get(Class<Entity> entityClass, long id) {
        mLogFacility.d(LOG_HASH, "Load entity " + id);
        Entity result = ofy().load()
                .key(Key.create(entityClass, id))
                .get();
        return result;
    }
    
    protected Entity get(Class<Entity> entityClass, String id) {
        mLogFacility.d(LOG_HASH, "Load entity " + id);
        Entity result = ofy().load()
                .key(Key.create(entityClass, id))
                .get();
        return result;
    }
    
    protected List<Entity> getAll(Class<Entity> entityClass) {
        mLogFacility.d(LOG_HASH, "Load all entities");
        List<Entity> entities = ofy().load()
                .type(entityClass)
                .list();
        return entities;
    }

    protected int count(Class<Entity> entityClass) {
        Iterable<Key<Entity>> allKeysIterator = ofy().load().type(entityClass).keys();
        if (null != allKeysIterator) {
            ArrayList<Key<Entity>> allKeys = Lists.newArrayList(allKeysIterator);
            return allKeys.size();
        } else {
            return 0;
        }
    }

    protected void deleteAll(Class<Entity> entityClass) {
        mLogFacility.d(LOG_HASH, "Delete all entities");
        
        List<Key<Entity>> allKeys = ofy()
                .load()
                .type(entityClass)
                .keys()
                .list();

        // Useful for deleting items
        ofy().delete()
                .keys(allKeys)
                .now();
    }

    /**
     * Return the string to use in logging messages
     * @return
     */
    protected abstract String getLogHash();

    // ----------------------------------------- Private Classes

}