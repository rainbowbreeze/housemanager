package it.rainbowbreeze.housemanager.servlet;

import it.rainbowbreeze.housemanager.common.App;
import it.rainbowbreeze.housemanager.data.HouseAnnounceDao;
import it.rainbowbreeze.housemanager.domain.HouseAnnounce;

import java.io.IOException;
import java.util.ArrayList;
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

        //get all the house data
        HouseAnnounceDao dao = App.i().getHouseAnnounceDao();
        List<HouseAnnounce> announces = dao.getAll();
        for(HouseAnnounce announce : announces) {
            announce.encode();
        }

        //serialize the object for an easy usage inside the doc
        List<HouseAnnounce> a = new ArrayList<HouseAnnounce>();
        a.add(announces.get(0));
        String json = App.i().getJsonHelper().toJson(announces);
        
        req.setAttribute("user", "Alfredo");
        req.setAttribute("announces", json);
        resp.setContentType("text/html");
        RequestDispatcher jsp = req.getRequestDispatcher("/WEB-INF/map.jsp");
        jsp.forward(req, resp);
        
        
//        <input type="hidden" name="myObjectId" value="${myObjectId}" />
//        String myObjectId = request.getParameter("myObjectId");
//        Object myObject = request.getSession().getAttribute(myObjectId);
//        request.getSession().removeAttribute(myObjectId);
    }
}
