/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import it.rainbowbreeze.housemanager.common.ILogFacility;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class NetworkManager {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = NetworkManager.class.getSimpleName();
    
    private final ILogFacility mLogFacility;

    // -------------------------------------------- Constructors
    public NetworkManager(ILogFacility logFacility) {
        mLogFacility = logFacility;
    }

    // --------------------------------------- Public Properties
    public String getUrlContent(String url) throws MalformedURLException, IOException {
        mLogFacility.info("Get content for url: " + url);
        
        URL urlObj = new URL(url);
        BufferedReader reader = null;
        try {
            InputStream inStream = urlObj.openStream();
            InputStreamReader inStreamReader = new InputStreamReader(inStream);
            reader = new BufferedReader(inStreamReader);
            
            StringBuilder builder = new StringBuilder();
            String aux = "";
            while ((aux = reader.readLine()) != null) {
                builder.append(aux);
            }
            reader.close();
            return builder.toString();
            
        } catch (MalformedURLException e) {
            mLogFacility.warn(LOG_HASH, "Malformed url");
            throw e;
        } catch (IOException e) {
            mLogFacility.warn(LOG_HASH, "IO Error: " + e.getMessage());
            throw e;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {}
            }
        }
    }

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
