/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import it.rainbowbreeze.housemanager.common.ILogFacility;

import java.util.ArrayList;
import java.util.Map;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;

/**
 * 
 * TODO
 * Explore
 *  ds.allocateIds() to pre-generate system id prior to save entity
 *  
 *  
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public abstract class AbstractEntityDao<T extends Entity> {
    // ------------------------------------------ Private Fields
    private final ILogFacility mLogFacility;
    private final DatastoreService mDs;

    // -------------------------------------------- Constructors
    public AbstractEntityDao(ILogFacility logFacility) {
        mLogFacility = logFacility;
        mDs = DatastoreServiceFactory.getDatastoreService();
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods]
    public Entity get(String name) {
        Key key = KeyFactory.createKey(getKind(), name);
        try {
             Entity entity = mDs.get(key);
             return entity;
        } catch (EntityNotFoundException e) {
            mLogFacility.d(getLogHash(), "Entity not found for the name " + name);
            return null;
        }
    }
    
    public Map<Key, Entity> get(String... names) {
        if (null == names || 0 == names.length) {
            return null;
        }
        
        ArrayList<Key> keys = new ArrayList<Key>();
        for (String name : names) {
            Key key = KeyFactory.createKey(getKind(), name);
            keys.add(key);
        }

        Map<Key, Entity> entities = mDs.get(keys);
        return entities;
        //Entity entity = entities.get(key);
    }
    
    public void getAll() {
        Entity test;
        
        Query query = new Query(getKind());
//        query.setFilter(new Query.FilterPredicate(propertyName, FilterOperator.EQUAL, value));
//        query.setFilter(new Query.FilterPredicate("__key__", FilterOperator.GREATER_THAN, value)); //search on key name
        
        Query kindlessQuery = new Query();
        kindlessQuery.setFilter(new Query.FilterPredicate("__key__", FilterOperator.EQUAL, "something")); //only equal an not equal in kindless query
//        
//        query.setFilter(new Query.CompositeFilter(
//                CompositeFilterOperator.AND,
//                new ArrayList<Query.Filter>(
//                        Arrays.asList(
//                                new Query.FilterPredicate(propertyName, operator, value),
//                                new Query.FilterPredicate(propertyName, operator, value)
//                        )
//                )));
//        query.setFilter(Query.CompositeFilterOperator.and(
//                Query.FilterOperator.EQUAL.of(propertyName, value),
//                Query.FilterOperator.EQUAL.of(propertyName, value)
//                ));
//        query.addSort(propertyName, SortDirection.ASCENDING);
        
        query.setKeysOnly(); //gets only keys, and not entity properties

//        query = new Query(getKind()).addSort(propertyName, SortDirection.ASCENDING);
        
        PreparedQuery pq = mDs.prepare(query);
        Iterable<Entity> results = pq.asIterable();
        //expensive because to find the offset, it scroll thru all the results until the offset.
        //better to use cursors
//        Iterable<Entity> results = pq.asIterable(FetchOptions.Builder.withLimit(10).offset(3));
        
        //test to verify if there is a least one result
        if (pq.countEntities(FetchOptions.Builder.withLimit(1)) == 1) {
            // The query has at least one result.
        }
        
        pq.asSingleEntity(); //get only the first entity
        pq.asList(FetchOptions.Builder.withDefaults()); //not in batches like asIterable does, but all together in one shot
        
        for (Entity result : results) {
//            String title = (String) result.getProperty(propertyName);
        }
    }
    
    public void cursorQuery() {
        Query query = new Query(getKind()).addSort("propertyName", SortDirection.ASCENDING);
        PreparedQuery pq = mDs.prepare(query);
        QueryResultList<Entity> results = pq.asQueryResultList(FetchOptions.Builder.withLimit(10));
        Cursor cursor = results.getCursor();
        String cursorString = cursor.toWebSafeString();
        
//        String cursorString = req.getParameter("xxxx");
        cursor = null;
        if (cursorString != null) {
            cursor = Cursor.fromWebSafeString(cursorString);
        }
        results = pq.asQueryResultList(FetchOptions.Builder.withLimit(10).startCursor(cursor));
        //or endCursor
    }
    
    public void projectionQuery() {
        Query query = new Query(getKind());
//        query.addProjection(new PropertyProjection(propertyName, String.class));
    }

    public void save(T object) {
//        mDs.put(entity);
    }

    public void delete(T object) {
//      mDs.delete(entity);
  }

    // ----------------------------------------- Private Methods
    /**
     * Returns the kind of entity managed by this DAO
     * @return
     */
    protected abstract String getKind();

    /**
     * Returns the log hash to use while logging
     * @return
     */
    protected abstract  String getLogHash();

    protected abstract T fromEntityToObject(Entity entity);
    
    protected abstract Entity fromObjectToEntity(T object);

    // ----------------------------------------- Private Classes
}
