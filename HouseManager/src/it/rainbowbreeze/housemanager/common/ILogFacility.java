/**
 * 
 */
package it.rainbowbreeze.housemanager.common;

/**
 * Basic logger class
 * 
 * @author alfredomorresi
 *
 */
public interface ILogFacility {

    void info(String message);
    void info(String method, String message);

    void warn(String message);
    void warn(String method, String message);
}
