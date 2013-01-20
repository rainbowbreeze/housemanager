/**
 * 
 */
package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.HouseAgentsManager;
import it.rainbowbreeze.housemanager.logic.agent.AnnounceScrapingResult;
import it.rainbowbreeze.housemanager.logic.agent.IHouseAgent;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Queue for scanning a single search result entry in a website
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
@SuppressWarnings("serial")
public class HouseAgentSingleQueueServlet extends HttpServlet {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = HouseAgentSingleQueueServlet.class.getSimpleName();
    
    private final ILogFacility mLogFacility;

    // -------------------------------------------- Constructors
    public HouseAgentSingleQueueServlet() {
        super();
        mLogFacility = App.i().getLogFacility();
    }

    // ------------------------------------------- Public Fields
    /** Task queue for scraping tasks */
    public static final String TASK_QUEUE_NAME = "houseagent"; //same name in the queue.xml
    public static final String TASK_QUEUE_URL = "/admin/tasks/houseagentsingle"; //same name in the web.xml

    public static final String PARAM_AGENT_DOMAIN = "AgentDomain";
    public static final String PARAM_ANNOUNCE_KEY = "HouseKey";

    
    // --------------------------------------- Public Properties

    // -------------------------------------------------- Events

    // ------------------------------------------ Public Methods
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        //get parameters
        String agentDomain = req.getParameter(PARAM_AGENT_DOMAIN);
        String announceKey = req.getParameter(PARAM_ANNOUNCE_KEY);
        mLogFacility.d(LOG_HASH, "Starting deep analysis for domain " + agentDomain + " from announce " + announceKey);

        HouseAgentsManager houseAgentsManager = App.i().getHouseAgentsManager();
        IHouseAgent agent = houseAgentsManager.getAgent(agentDomain);
        if (null == agent) {
            mLogFacility.w(LOG_HASH, "Agent not found, aborting");
            return;
        }

        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        HouseAnnounce announce = dao.get(announceKey);
        if (null == announce) {
            mLogFacility.w(LOG_HASH, "Cannot get announce " + announceKey);
            return;
        }
        
        AnnounceScrapingResult scrapingResult = agent.scrapeAnnounce(announce);
        if (null == scrapingResult || scrapingResult.hasErrors()) {
            mLogFacility.w(LOG_HASH, "Error while checking the single announce");
            return;
        }
        if (null == scrapingResult.getAnnounce()) {
            mLogFacility.w(LOG_HASH, "Return announce is empty");
            return;
        }
        dao.save(scrapingResult.getAnnounce());
    }


    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
