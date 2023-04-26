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
import org.json.JSONException;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Task;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "ReassignTask", urlPatterns = {"/ReassignTask"})
public class ReassignTask extends HttpServlet {

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
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            String jsonString = "";
            for( String line : request.getReader().lines().toList() ){
                jsonString += line;
            }
            JSONObject obj = new JSONObject(jsonString);
            
            
                        if (request.getAttribute("organisation") == null) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "invalid organisation"));
                return;
            }

            Long organisation_id = Long.valueOf((String) request.getAttribute("organisation")); 

            User currentUser = (User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "session issue"));
                return;
            }            
       
            
            EntityManager em = factory.createEntityManager();
            currentUser = em.merge(currentUser);
            em.refresh(currentUser); 
            if( !currentUser.checkPermission(organisation_id,Worker.PERMISSION_MANAGER ) )
            {
            out.print( new JSONObject().put("status", "error").put("message", "You don't have permissions") );
            return;
            }
            if(obj.isNull("task") || obj.isNull("assignedWorker")|| obj.isNull("assignedUnit"))
            {
                out.print(new JSONObject().put("status", "error")
                .put("message", "invalid task, assignedWorker, assignedUnit") );
                return;
            }
            Task task = em.find( Task.class , obj.getLong("task"));
            em.getTransaction().begin();
            task.setAssignedUnit(null);
            task.setAssignedWorker(null);
            try{ task.setAssignedWorker( 
                    em.find(Worker.class, obj.getLong("assignedWorker")) 
                    );}catch( JSONException e ){}
            try{ task.setAssignedUnit( 
                    em.find(Unit.class, obj.getLong("assignedUnit")) 
                    );}catch( JSONException e ){}
            if( task.getAssignedWorker() == null && task.getAssignedUnit()==null ){
                em.getTransaction().rollback();
                out.print( new JSONObject().put("status", "error").toString() );
            }
            else{
                em.getTransaction().commit();
                out.print( new JSONObject().put("status", "ok").toString() );
            }
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
