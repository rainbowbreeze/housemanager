package it.rainbowbreeze.housemanager;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.scraper.ScrapingResult;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HouseManagerServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        
        ScrapingResult result = App.i().getImmobiliareScraper().scrape();
        
        resp.getWriter().println("Hello, world");
        resp.getWriter().println("Announces: " + result.getCursor());
        resp.getWriter().println("Pages: " + result.getTotalPages());
    }
}
