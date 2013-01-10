/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.agent.IHouseAgent;
import it.rainbowbreeze.housemanager.logic.agent.ImmobiliareAgent;
import it.rainbowbreeze.housemanager.servlet.HouseAgentFullQueueServlet;
import it.rainbowbreeze.housemanager.servlet.HouseAgentSingleQueueServlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
public class HouseAgentsManager {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = HouseAgentsManager.class.getSimpleName();
    private final ILogFacility mLogFacility;
    
    private final Map<String, IHouseAgent> mAgents;
    
    // -------------------------------------------- Constructors
    public HouseAgentsManager(ILogFacility logFacility, NetworkManager networkManager) {
        mLogFacility = logFacility;
        
        //registers all the scraping agents
        mAgents = new HashMap<String, IHouseAgent>();
        //creates scraping agents
        IHouseAgent immobiliare = new ImmobiliareAgent(mLogFacility, networkManager);
        mAgents.put(immobiliare.getName(), immobiliare);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    /**
     * Enqueues all the house agents in the process queue
     */
    public void enqueueAgents() {
        mLogFacility.d(LOG_HASH, "Enqueuing all the " + mAgents.size() + " house agents");
        for (IHouseAgent agent : mAgents.values()) {
            enqueueAgent(agent, null);
        }
    }

    /**
     * Enqueues a single house agent in the process queue
     * @param agent
     * @param cursor
     */
    public TaskHandle enqueueAgent(IHouseAgent agent, String cursor) {
        if (null == agent) {
            mLogFacility.w(LOG_HASH, "Null agent, aborting");
            return null;
        }
        
        mLogFacility.d(LOG_HASH, "Queuing house agent " + agent.getName() + " with cursor " + cursor);
        Queue queue = QueueFactory.getQueue(HouseAgentFullQueueServlet.TASK_QUEUE_NAME);
        String taskName = agent.getTaskQueueName(cursor);
        String processedCursor = StringUtils.isEmpty(cursor) ? "" : cursor;
        
        TaskHandle taskHandle = queue.add(TaskOptions.Builder
                .withTaskName(taskName)
                .url(HouseAgentFullQueueServlet.TASK_QUEUE_URL)
                .param(HouseAgentFullQueueServlet.PARAM_AGENT_DOMAIN, agent.getName())
                .param(HouseAgentFullQueueServlet.PARAM_CURSOR, processedCursor)
                .method(Method.POST));
        
        return taskHandle;
    }
    
    public TaskHandle enqueueAnnounceAnalysis(IHouseAgent agent, HouseAnnounce announce) {
        if (null == agent) {
            mLogFacility.w(LOG_HASH, "Null agent, aborting");
            return null;
        }
        if (null == announce) {
            mLogFacility.w(LOG_HASH, "Null announce, aborting");
            return null;
        }

        Queue queue = QueueFactory.getQueue(HouseAgentSingleQueueServlet.TASK_QUEUE_NAME);
        String key = agent.getUniqueKey(announce);
        mLogFacility.d(LOG_HASH, "Queuing deep analysis for announce " + key);
        String taskName = agent.getTaskQueueName(announce);;
        
        TaskHandle taskHandle = queue.add(TaskOptions.Builder
                .withTaskName(taskName)
                .url(HouseAgentSingleQueueServlet.TASK_QUEUE_URL)
                .param(HouseAgentSingleQueueServlet.PARAM_AGENT_DOMAIN, agent.getName())
                .param(HouseAgentSingleQueueServlet.PARAM_ANNOUNCE_KEY, key)
                .method(Method.POST));
        
        return taskHandle;
    }

    /**
     * Returns the specific agent based on its name
     * 
     * @param taskName
     * @return
     */
    public IHouseAgent getAgent(String taskName) {
        return mAgents.get(taskName);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
