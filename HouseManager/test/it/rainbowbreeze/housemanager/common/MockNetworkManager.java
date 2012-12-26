/**
 * 
 */
package it.rainbowbreeze.housemanager.common;

import it.rainbowbreeze.housemanager.logic.NetworkManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

/**
 * Mock {@link NetworkManager} class
 * 
 * Set url content and then call the real 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class MockNetworkManager extends NetworkManager {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public MockNetworkManager(ILogFacility logFacility) {
        super(logFacility);
        mUrlReplies = new Hashtable<String, String>();
    }

    // --------------------------------------- Public Properties
    private final Hashtable<String, String> mUrlReplies;
    public Hashtable<String, String> getUrlReplies() {
        return mUrlReplies;
    }
    
    // ------------------------------------------ Public Methods
    @Override
    public String getUrlContent(String url) throws MalformedURLException, IOException {
        if (mUrlReplies.containsKey(url)) {
            return mUrlReplies.get(url);
        } else {
            throw new IOException("URL not found in internal mock table" + url);
        }
    }

    // ----------------------------------------- Private Methods
}
