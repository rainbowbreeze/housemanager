/**
 * 
 */
package it.rainbowbreeze.housemanager.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class LogFacility implements ILogFacility {

    // ------------------------------------------ Private Fields
    private final Logger mLog;

    // -------------------------------------------- Constructors
    public LogFacility(String appName) {
        mLog = Logger.getLogger(appName);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods

    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#d(java.lang.String)
     */
    public void d(String message) {
        log(Level.INFO, message);
//        log(Level.FINE, message);
    }

    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#d(java.lang.String, java.lang.String)
     */
    public void d(String method, String message) {
        log(Level.INFO, createMethodPrefix(method, message));
//        log(Level.FINE, createMethodPrefix(method, message));
    }
    
    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#info(java.lang.String)
     */
    public void i(String message) {
        log(Level.INFO, message);
    }

    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#info(java.lang.String, java.lang.String)
     */
    public void i(String method, String message) {
        log(Level.INFO, createMethodPrefix(method, message));
    }
    
    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#warn(java.lang.String)
     */
    public void w(String message) {
        log(Level.WARNING, message);
    }

    /* (non-Javadoc)
     * @see it.rainbowbreeze.housemanager.common.ILogFacility#warn(java.lang.String, java.lang.String)
     */
    public void w(String method, String message) {
        log(Level.WARNING, createMethodPrefix(method, message));
    }
    
    // ----------------------------------------- Private Methods
    private String createMethodPrefix(String method, String message) {
        return "[" + method + "] " + message;
    }
    
    private void log(Level level, String message) {
        mLog.log(level, message);
    }


    // ----------------------------------------- Private Classes
}
