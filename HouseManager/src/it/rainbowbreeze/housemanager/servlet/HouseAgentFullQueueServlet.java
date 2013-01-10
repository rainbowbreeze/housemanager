/**
 * 
 */
package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.HouseAgentsManager;
import it.rainbowbreeze.housemanager.logic.agent.IHouseAgent;
import it.rainbowbreeze.housemanager.logic.agent.SearchPageAgentResult;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Queue for scanning an entire website search result
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
@SuppressWarnings("serial")
public class HouseAgentFullQueueServlet extends HttpServlet {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = HouseAgentFullQueueServlet.class.getSimpleName();
    
    private final ILogFacility mLogFacility;

    // -------------------------------------------- Constructors
    public HouseAgentFullQueueServlet() {
        super();
        mLogFacility = App.i().getLogFacility();
    }

    // ------------------------------------------- Public Fields
    /** Task queue for scraping tasks */
    public static final String TASK_QUEUE_NAME = "houseagent"; //same name in the queue.xml
    public static final String TASK_QUEUE_URL = "/admin/tasks/houseagentfull"; //same name in the web.xml

    public static final String PARAM_AGENT_DOMAIN = "AgentDomain";
    public static final String PARAM_CURSOR = "Cursor";

    
    // --------------------------------------- Public Properties

    // -------------------------------------------------- Events

    // ------------------------------------------ Public Methods
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        //get parameters
        String agentDomain = req.getParameter(PARAM_AGENT_DOMAIN);
        String cursor = req.getParameter(PARAM_CURSOR);
        mLogFacility.d(LOG_HASH, "Starting agent " + agentDomain + " from cursor " + cursor);

        HouseAgentsManager houseAgentsManager = App.i().getHouseAgentsManager();
        IHouseAgent agent = houseAgentsManager.getAgent(agentDomain);
        if (null == agent) {
            mLogFacility.w(LOG_HASH, "Agent not found, aborting");
            return;
        }
        
        SearchPageAgentResult scrapingResult;
        if (StringUtils.isEmpty(cursor)) {
            //first start of the agent
            scrapingResult = agent.scrape();
        } else {
            scrapingResult = agent.scrape(cursor);
        }
        
        //search for new announces to add
        if (scrapingResult.hasErrors()) {
            mLogFacility.w(LOG_HASH, "Error processing agent " + agentDomain + " from cursor " + cursor);
            //TODO send an email with the error
            return;
        }
        
        addResultsToStorage(houseAgentsManager, agent, scrapingResult);

        if (scrapingResult.hasMoreResults()) {
            //launch a new pass of the process
            houseAgentsManager.enqueueAgent(agent, scrapingResult.getCursor());
        }
        
    }


    // ----------------------------------------- Private Methods
    /**
     * Adds scraping results to storage
     * @param scrapingResult
     */
    private void addResultsToStorage(
            HouseAgentsManager houseAgentsManager,
            IHouseAgent agent,
            SearchPageAgentResult scrapingResult) {
        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        
        mLogFacility.d(LOG_HASH, "Found " + scrapingResult.getAnnounces().size() + " announce(s)");
        for (HouseAnnounce announce : scrapingResult.getAnnounces()) {
            //searches for a previous inserted house announce
            String uniqueKey = agent.getUniqueKey(announce); 
            HouseAnnounce existingAnnouce = dao.get(uniqueKey);
            if (null == existingAnnouce || !existingAnnouce.wasDeepProcessed()) {
                mLogFacility.d(LOG_HASH, "Saving announce " + uniqueKey);
                dao.save(announce);
                mLogFacility.d(LOG_HASH, "Launching deep analysis of announce " + uniqueKey);
                houseAgentsManager.enqueueAnnounceAnalysis(agent, announce);
            }
        }
    }

    // ----------------------------------------- Private Classes
}

