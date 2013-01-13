/**
 * 
 */
package it.rainbowbreeze.housemanager.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Contains application's status variables (last scraping time, etc)
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@Entity
public class AppGlobalStatusBag {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public AppGlobalStatusBag() {
        runningAgents = new ArrayList<String>();
    }

    // --------------------------------------- Public Properties
    @Id private Long id;
    
    private Date lastDataRefresh;
    /**
     * Get the date of latest backend data refresh (house announces)
     * 
     * @return
     */
    public Date getLastDataRefresh() {
        return lastDataRefresh;
    }
    public AppGlobalStatusBag setLastDataRefresh(Date newValue) {
        lastDataRefresh = newValue;
        return this;
    }

    private List<String> runningAgents;
    /**
     * Background agents that are still running. If empty, no agents are running
     * @return
     */
    public List<String> getRunningAgents() {
        return runningAgents;
    }
    
    // ------------------------------------------ Public Methods
    public AppGlobalStatusBag addRunningAgents(String newAgent) {
        if (!runningAgents.contains(newAgent)) runningAgents.add(newAgent);
        return this;
    }

    public AppGlobalStatusBag removeRunningAgents(String agentToRemove) {
        runningAgents.remove(agentToRemove);
        return this;
    }
    
    public boolean isRunningAgentsEmpty() {
        return runningAgents.isEmpty();
    }

    // ----------------------------------------- Private Methods
}
