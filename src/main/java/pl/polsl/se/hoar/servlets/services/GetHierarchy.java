/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "GetHierarchy", urlPatterns = {"/GetHierarchy"})
public class GetHierarchy extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            if(request.getAttribute("organisation")==null)
            {
                out.print(new JSONObject().put("status", "error")
                .put("message", "invalid organisation") );
                return;
            }
            if(request.getSession().getAttribute("user") == null)
            {
                out.print(new JSONObject().put("status", "error")
                .put("message", "session issue") );
                return;  
            }
            Long organisation_id = Long.parseLong((String) request.getAttribute("organisation"));
            User user = (User) request.getSession().getAttribute("user");
            
           
            
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            EntityManager em = factory.createEntityManager();
            user = em.merge(user);
            em.refresh(user);
             if( !user.checkPermission(organisation_id,Worker.PERMISSION_MANAGER ) )
                {
                out.print( new JSONObject().put("status", "error").put("message", "You don't have permissions") );
                return;
                }
            
            Worker worker = user.getWorkerByOrganisation(organisation_id);
            
            
            if (worker == null) {
                System.out.println(worker);
                out.print(new JSONObject().put("status", "error")
                .put("message", "you do not have access to this!") );
                return;
            }
            
            em.refresh(worker);
            
            Unit startingUnit = worker.getUnit();
            
            if( (worker.getPermissions() & Worker.PERMISSION_ADMIN) != 0 )
                startingUnit = worker.getOrganisation().getMainUnit();
            
            out.println( getHierarchy(em, startingUnit ).toString() );
            em.close();
        }
    }

    private JSONObject getHierarchy(EntityManager em, Unit unit ){
        em.refresh(unit);
        JSONObject out = new JSONObject();
        out.put("id", unit.getUnitID()); 
        out.put("name", unit.getUnitName());
        out.put("manager", unit.getManager() != null ? unit.getManager().getWorkerID(): null );
        out.put( "workers", unit.getWorkers().stream().map( Worker::getShortcut ).toList() );
        out.put( "children", unit.getChildren().stream().map( (Unit _unit)->{ return getHierarchy(em, _unit); } ).toList()  );
        return out;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
