/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Organisation;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "GetAssignables", urlPatterns = {"/GetAssignables"})
public class GetAssignables extends HttpServlet {

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
            

            Worker worker = null;
            for( Worker tmp : user.getWorkers() )
                if( tmp.getOrganisation().getId().equals(organisation_id) )
                    worker = tmp;
            em.refresh(worker);
            Organisation organisation = worker.getOrganisation();
            
            out.print( new JSONObject()
                    .put( "workers", 
                            organisation.getWorkers()
                            .stream().map( Worker::getShortcut ).toList()
                        )
                    .put("units", organisation.getUnits()
                            .stream()
                            .map( (Unit u)->{
                                return new JSONObject()
                                        .put("id", u.getUnitID())
                                        .put("name", u.getUnitName());
                            
                            }).toList() 
                    ) 
                    .toString()
            );
        }
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
