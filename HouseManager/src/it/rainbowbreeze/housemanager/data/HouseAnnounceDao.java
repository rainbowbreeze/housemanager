/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import static com.googlecode.objectify.ObjectifyService.ofy;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.ObjectifyService;

/**
 * DAO for {@link HouseAnnounce} object
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class HouseAnnounceDao extends ObjectifyAbstractDao<HouseAnnounce> {
    // ------------------------------------------ Private Fields
    static final String LOG_HASH = HouseAnnounceDao.class.getSimpleName();
    
    // -------------------------------------------- Constructors
    public HouseAnnounceDao(ILogFacility logFacility) {
        super(logFacility);
    }

    static {
        ObjectifyService.register(HouseAnnounce.class);
    }

    // --------------------------------------- Public Properties
    public HouseAnnounce get(long id) {
        return super.get(HouseAnnounce.class, id);
    }

    public HouseAnnounce get(String id) {
        return super.get(HouseAnnounce.class, id);
    }
    

    public List<HouseAnnounce> getAll() {
        return super.getAll(HouseAnnounce.class);
    }
    
    public int count() {
        return super.count(HouseAnnounce.class);
    }
    
    public void deleteAll() {
        super.deleteAll(HouseAnnounce.class);
    }

    /**
     * Returns only deep processed announces with valid fields (location, etc).
     * In addition, some fields are encoded using RFC2396 spec
     * @return
     */
    public List<HouseAnnounce> getAllValidAndEncoded() {
        mLogFacility.d(LOG_HASH, "Load all announces with useful fields");
        
        //extract only processed announces
        List<HouseAnnounce> announces = ofy().load()
                .type(HouseAnnounce.class)
                .filter(HouseAnnounce.Contract.DEEPPROCESSED + " =", true)
                .list();
        
        if (null == announces) {
            mLogFacility.d(LOG_HASH, "Cannot find any announces, no date or no one has been deep processed");
            return new ArrayList<HouseAnnounce>();
        }
        
        //extract only announces with valid data
        int count = 0;
        for(int i=announces.size()-1; i >=0; i--) {
            HouseAnnounce announce = announces.get(i);
            if (StringUtils.isEmpty(announce.getLat()) || StringUtils.isEmpty(announce.getLon())) {
                count++;
                announces.remove(i);
            }
        }
        if (count > 0) mLogFacility.d(LOG_HASH, "Removed " + count + " announce(s)");

        for(HouseAnnounce announce : announces) {
            announce.encode();
        }
        return announces;
    }
    
    // ----------------------------------------- Private Methods
    @Override
    protected String getLogHash() {
        return LOG_HASH;
    }


    // ----------------------------------------- Private Classes
}
