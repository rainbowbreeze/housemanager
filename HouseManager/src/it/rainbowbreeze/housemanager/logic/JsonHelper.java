/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class JsonHelper {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = JsonHelper.class.getSimpleName();
    
    private final ILogFacility mLogFacility;
    private static Gson mGson;

    // -------------------------------------------- Constructors
    public JsonHelper(ILogFacility logFacility) {
        mLogFacility = logFacility;
    }
    
    static {
        mGson = new GsonBuilder()
                .setExclusionStrategies(new HouseManagerExcludeStrat())
                .create();
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public String toJson(Object src) {
        return mGson.toJson(src);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
    private static class HouseManagerExcludeStrat implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {

            return (f.getDeclaringClass() == HouseAnnounce.class 
                    && (f.getName().equals(HouseAnnounce.Contract.ID)
                        || f.getName().equals(HouseAnnounce.Contract.DEEPPROCESSED)));
        }

    }
}
