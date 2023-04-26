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
import pl.polsl.se.hoar.entities.Category;
import pl.polsl.se.hoar.entities.Organisation;
import pl.polsl.se.hoar.entities.Task;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "CreateTask", urlPatterns = {"/CreateTask"})
public class CreateTask extends HttpServlet {

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

            User user = (User) request.getSession().getAttribute("user");

            if (user == null) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "session issue"));
                return;
            }
           
            
            
            EntityManager em = factory.createEntityManager();
            
            Task task = new Task();
            task.setCreationDate( java.sql.Date.valueOf( java.time.LocalDate.now() ) );
            Organisation organisation = null;
            try { Worker worker = em.find(Worker.class, obj.getLong("assignedWorker") );
                task.setAssignedWorker( worker ) ; 
            organisation = worker.getOrganisation(); 
            }catch( JSONException e ){}
            try { 
                Unit unit = em.find(Unit.class, obj.getLong("assignedUnit") );
                task.setAssignedUnit( unit );
                organisation = unit.getOrganisation();
            }catch( JSONException e ){}
           
            
            String description = obj.optString("description", "");
            
            task.setDescription(description);
            
            if( organisation == null ){
                out.print( new JSONObject().put("status", "error").put("message", "Incorrect assignment") );
                em.close();
                return;
            }
            
            
            user = em.merge(user);
            em.refresh(user);
            
             if (!user.checkPermission(organisation_id, Worker.PERMISSION_MANAGER)) {
                out.print(new JSONObject().put("status", "error").put("message", "You don't have permissions"));
                return;
            }
            
            task.setOrganisation(organisation);
            task.setStatus(0);
            task.setLastEdited( java.sql.Date.valueOf(java.time.LocalDate.now()) );
            task.setTitle( obj.getString("title") );
            
            
            for( Worker worker: user.getWorkers() )
                if( worker.getOrganisation().equals(organisation) )
                    task.setStatusEditor( worker );
            
            Category category = em.find(Category.class, obj.optLong("category", -1));
            
            if( category == null ){
                out.print( new JSONObject().put("status", "error").put("message", "Incorrect category") );
                em.close();
                return;
            }
            task.setCategory( category );
            
            em.getTransaction().begin();
            em.persist(task);
            em.getTransaction().commit();
            
            out.println( new JSONObject().put("status", "ok").put("id", task.getTaskID()) );
            em.flush();
            em.close();
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
