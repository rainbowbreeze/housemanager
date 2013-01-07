/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.scraper.IHouseScraper;
import it.rainbowbreeze.housemanager.scraper.ImmobiliareScraper;
import it.rainbowbreeze.housemanager.servlet.HouseAgentQueueServlet;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * http://googcloudlabs.appspot.com/codelabexercise8.html#Upload
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ScrapingAgentManager {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ScrapingAgentManager.class.getSimpleName();
    private final ILogFacility mLogFacility;
    
    private final Map<String, IHouseScraper> mAgents;
    
    // -------------------------------------------- Constructors
    public ScrapingAgentManager(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        
        //registers all the scraping agents
        mAgents = new HashMap<String, IHouseScraper>();
        //creates scraping agents
        IHouseScraper immobiliare = new ImmobiliareScraper(mLogFacility, networkManager);
        mAgents.put(immobiliare.getName(), immobiliare);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    /**
     * Starts all the agents for scraping the data
     */
    public void startAgents() {
        Queue queue = QueueFactory.getQueue(HouseAgentQueueServlet.TASK_QUEUE_NAME);
        
        for (IHouseScraper agent : mAgents.values()) {
            mLogFacility.d(LOG_HASH, "Queuing scraping agent " + agent.getName());
            String taskName = agent.getName() + "-"; //no cursor
            TaskHandle taskHandle = queue.add(TaskOptions.Builder
                    .withTaskName(taskName)
                    .url(HouseAgentQueueServlet.TASK_QUEUE_URL)
                    .param(HouseAgentQueueServlet.PARAM_AGENT_NAME, agent.getName())
                    .param(HouseAgentQueueServlet.PARAM_CURSOR, "")
                    .method(Method.POST));
            //creates a new queue for each agent
        }
        //load all the registered scraping agents
    }

    /**
     * Returns the specific agent based on its name
     * 
     * @param taskName
     * @return
     */
    public IHouseScraper getAgent(String taskName) {
        return mAgents.get(taskName);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
