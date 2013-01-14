package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.logic.HouseAgentsManager;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Refreshes all the announces data
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
@SuppressWarnings("serial")
public class RefreshDataCronServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HouseAgentsManager scrapintAgentManager = App.i().getHouseAgentsManager();
        scrapintAgentManager.enqueueAgents(true);

        resp.setContentType("text/plain");
        resp.getWriter().println("Removed all data and restarted agents");
    }
}
