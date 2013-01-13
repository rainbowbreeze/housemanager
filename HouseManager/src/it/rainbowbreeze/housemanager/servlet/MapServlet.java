package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.AppGlobalStatusBag;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http://www.getlatlon.com/
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
@SuppressWarnings("serial")
public class MapServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        //gets all the house data
        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        List<HouseAnnounce> announces = dao.getAllValidAndEncoded();
        //serialize the object for an easy usage inside the doc
        String jsonAnnounces = App.i().getJsonHelper().toJson(announces);
        
        //gets app status
        AppGlobalStatusBag bag = App.i().getAppGlobalStatusBagDao().get();
        
        //Pavia borders
        req.setAttribute("mapSWLat", 45.212036101115885);
        req.setAttribute("mapSWLng", 9.116249084472656);
        req.setAttribute("mapNELat", 45.168725648565285);
        req.setAttribute("mapNELng", 9.203453063964844);
        req.setAttribute("latestDataUpdate", bag.getLastDataRefresh());
        req.setAttribute("areAgentsRunning", bag.isRunningAgentsEmpty());
        req.setAttribute("announces", jsonAnnounces);
        resp.setContentType("text/html");
        RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/map.jsp");
        jsp.forward(req, resp);
        
//        <input type="hidden" name="myObjectId" value="${myObjectId}" />
//        String myObjectId = request.getParameter("myObjectId");
//        Object myObject = request.getSession().getAttribute(myObjectId);
//        request.getSession().removeAttribute(myObjectId);
    }
}
