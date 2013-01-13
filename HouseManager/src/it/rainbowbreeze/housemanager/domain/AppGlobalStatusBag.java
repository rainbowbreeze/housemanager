/**
 * 
 */
package it.rainbowbreeze.housemanager.domain;

import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;

/**
 * Contains application's status variables (last scraping time, etc)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Entity
public class AppGlobalStatusBag {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    private Date lastDataRefresh;
    /**
     * Get the date of latest backend data refresh (house announces)
     * 
     * @return
     */
    public Date getLastDataRefresh() {
        return lastDataRefresh;
    }
    public void setLastDataRefresh(Date lastDataRefresh) {
        this.lastDataRefresh = lastDataRefresh;
    }

    private List<String> runningAgents;
    /**
     * Background agents that are still running. If empty, no agents are running
     * @return
     */
    public List<String> getRunningAgents() {
        return runningAgents;
    }
    public void setRunningAgents(List<String> runningAgents) {
        this.runningAgents = runningAgents;
    }
    
    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
}
