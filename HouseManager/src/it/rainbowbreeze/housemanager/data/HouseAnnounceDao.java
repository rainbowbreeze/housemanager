/**
 * 
 */
package it.rainbowbreeze.housemanager.data;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.util.List;

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
        List<HouseAnnounce> announces = getAll();;
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
