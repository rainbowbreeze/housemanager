package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.ScrapingAgentManager;
import it.rainbowbreeze.housemanager.scraper.AnnounceScrapingResult;
import it.rainbowbreeze.housemanager.scraper.SearchPageScrapingResult;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HouseManagerServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        
        SearchPageScrapingResult searchResult = App.i().getImmobiliareScraper().scrape();
        
        resp.getWriter().println("Hello, world");
        resp.getWriter().println("Announces: " + searchResult.getCursor());
        resp.getWriter().println("Pages: " + searchResult.getTotalPages());
        
        HouseAnnounce announce = searchResult.getAnnounces().get(0);
        AnnounceScrapingResult deepResult = App.i().getImmobiliareScraper().scrapeDeep(announce);
        resp.getWriter().println("Lat: " + deepResult.getAnnounce().getLat() + " - Lon: " + deepResult.getAnnounce().getLon());
        
        ILogFacility logger = App.i().getLogFacility();
        logger.d("Starting scraping process");
        App.i().getHouseAnnounceDao().deleteAll();
        ScrapingAgentManager scrapintAgentManager = App.i().getScrapingAgentManager();
        scrapintAgentManager.startAgents();
    }
}
