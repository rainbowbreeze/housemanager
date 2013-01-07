/**
 * 
 */
package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.scraper.IHouseScraper;
import it.rainbowbreeze.housemanager.scraper.SearchPageScrapingResult;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class HouseAgentQueueServlet extends HttpServlet {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = HouseAgentQueueServlet.class.getSimpleName();
    
    private final ILogFacility mLogFacility;

    // -------------------------------------------- Constructors
    public HouseAgentQueueServlet() {
        super();
        mLogFacility = App.i().getLogFacility();
    }

    // ------------------------------------------- Public Fields
    /** Task queue for scraping tasks */
    public static final String TASK_QUEUE_NAME = "houseagent"; //same name in the queue.xml
    public static final String TASK_QUEUE_URL = "/admin/tasks/houseagent"; //same name in the web.xml

    public static final String PARAM_AGENT_NAME = "AgentName";
    public static final String PARAM_CURSOR = "Cursor";

    
    // --------------------------------------- Public Properties

    // -------------------------------------------------- Events

    // ------------------------------------------ Public Methods
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        //get parameters
        String taskName = req.getParameter(PARAM_AGENT_NAME);
        String cursor = req.getParameter(PARAM_CURSOR);
        
        mLogFacility.d(LOG_HASH, "Starting agent " + taskName + " from cursor " + cursor);
        
        IHouseScraper agent = App.i().getScrapingAgentManager().getAgent(taskName);
        if (null == agent) {
            return;
        }
        
        SearchPageScrapingResult scrapingResult;
        if (StringUtils.isEmpty(cursor)) {
            //first start of the agent
            scrapingResult = agent.scrape();
        } else {
            scrapingResult = agent.scrape(cursor);
        }
        
        //search for new announces to add
        if (scrapingResult.hasErrors()) {
            mLogFacility.w(LOG_HASH, "Error processing agent " + taskName + " from cursor " + cursor);
            //TODO
        }
        
        if (scrapingResult.hasMoreResults()) {
            //launch a new pass of the process
            //TODO refactor to an unique procedure
        }
        
        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        mLogFacility.d(LOG_HASH, "Found " + scrapingResult.getAnnounces().size() + " announce(s)");
        for (HouseAnnounce announce : scrapingResult.getAnnounces()) {
            //TODO
            mLogFacility.d(LOG_HASH, "Saving announce with title " + announce.getTitle());
            dao.save(announce);
        }
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
