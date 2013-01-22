/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import it.rainbowbreeze.housemanager.common.App;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.utils.SystemProperty;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MiscUtils {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public static void addStatsForProduction(HttpServletRequest req, String attrName) {
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            req.setAttribute(attrName, App.TRACKING_CODE);
        }
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
