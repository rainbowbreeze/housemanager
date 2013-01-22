package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.common.ILogFacility;
import it.rainbowbreeze.housemanager.data.CacheManager;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.AppGlobalStatusBag;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;
import it.rainbowbreeze.housemanager.logic.MiscUtils;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * http://www.getlatlon.com/
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
@SuppressWarnings("serial")
public class MapServlet extends HttpServlet {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = MapServlet.class.getSimpleName();
    
    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        AppGlobalStatusBag bag = App.i().getAppGlobalStatusBagDao().get();
        CacheManager cacheManager = App.i().getCacheManager();
        ILogFacility logFacility = App.i().getLogFacility();
        
        String jsonAnnounces = null;
        int totalAnnouces = 0;
        if (!cacheManager.isRefreshRequired(bag.getLastDataRefresh())) {
            jsonAnnounces = cacheManager.getAnnouncesJson();
            totalAnnouces = cacheManager.getAnnouncesNumber();
            logFacility.d(LOG_HASH, "Announces should be present in cache");
        }
        
        if (StringUtils.isEmpty(jsonAnnounces)) {
            HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
            List<HouseAnnounce> announces = dao.getAllValidAndEncoded();
            //serializes the object for an easy usage inside the doc
            jsonAnnounces = App.i().getJsonHelper().toJson(announces);
            totalAnnouces = announces.size();
            cacheManager.cacheAnnounces(totalAnnouces, jsonAnnounces, bag.getLastDataRefresh());
            logFacility.d(LOG_HASH, "Loaded announces from storage");
        }

        //Pavia borders
        req.setAttribute("mapSWLat", 45.21);
        req.setAttribute("mapSWLng", 9.12);
        req.setAttribute("mapNELat", 45.17);
        req.setAttribute("mapNELng", 9.20);
        req.setAttribute("latestDataUpdate", bag.getLastDataRefresh());
        req.setAttribute("areAgentsRunning", bag.isRunningAgentsEmpty());
        req.setAttribute("totalAnnounces", totalAnnouces);
        req.setAttribute("announces", jsonAnnounces);
        req.setAttribute("priceLower", 0);
        req.setAttribute("priceUpper", 1500000);
        req.setAttribute("priceValue", 1500000);
        req.setAttribute("priceStep", 10000);
        req.setAttribute("areaLower", 0);
        req.setAttribute("areaUpper", 500);
        req.setAttribute("areaValue", 1);
        req.setAttribute("areaStep", 10);
        req.setAttribute("currency", "Eur");
        MiscUtils.addStatsForProduction(req, "trackingCode");
        
        resp.setContentType("text/html");
        RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/map.jsp");
        jsp.forward(req, resp);
        
//        <input type="hidden" name="myObjectId" value="${myObjectId}" />
//        String myObjectId = request.getParameter("myObjectId");
//        Object myObject = request.getSession().getAttribute(myObjectId);
//        request.getSession().removeAttribute(myObjectId);
    }

    // ----------------------------------------- Private Methods
    
    // ----------------------------------------- Private Classes

}
