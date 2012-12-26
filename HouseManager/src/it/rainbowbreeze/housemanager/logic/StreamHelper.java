/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class StreamHelper {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // -------------------------------------------------- Events

    // ------------------------------------------ Public Methods
    public static String toString(InputStream is) throws IOException {
        InputStreamReader inStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inStreamReader);
        
        StringBuilder builder = new StringBuilder();
        String aux = "";
        while ((aux = reader.readLine()) != null) {
            builder.append(aux);
        }
        reader.close();
        return builder.toString();
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
