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

    void d(String message);
    void d(String method, String message);

    void i(String message);
    void i(String method, String message);

    void w(String message);
    void w(String method, String message);
}
