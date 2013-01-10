package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.logic.HouseAgentsManager;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HouseManagerServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");

//        SearchPageScrapingResult searchResult = App.i().getImmobiliareScraper().scrape();
        
//        resp.getWriter().println("Announces: " + searchResult.getCursor());
//        resp.getWriter().println("Pages: " + searchResult.getTotalPages());
        
//        HouseAnnounce announce = searchResult.getAnnounces().get(0);
//        AnnounceScrapingResult deepResult = App.i().getImmobiliareScraper().scrapeDeep(announce);
//        resp.getWriter().println("Lat: " + deepResult.getAnnounce().getLat() + " - Lon: " + deepResult.getAnnounce().getLon());
        
        ILogFacility logger = App.i().getLogFacility();
        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        dao.deleteAll();
        resp.getWriter().print("Number of announces: " + dao.count());
        
        logger.d("Starting scraping process");
        HouseAgentsManager scrapintAgentManager = App.i().getHouseAgentsManager();
        scrapintAgentManager.enqueueAgents();
    }
}
