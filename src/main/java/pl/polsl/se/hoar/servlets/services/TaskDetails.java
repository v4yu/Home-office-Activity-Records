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
import pl.polsl.se.hoar.entities.Task;

import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Comment;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "TaskDetails", urlPatterns = {"/TaskDetails"})
public class TaskDetails extends HttpServlet {

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
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            EntityManager entityManager = factory.createEntityManager();
            Long organisation_id = Long.parseLong((String) request.getAttribute("organisation"));
            Task task = entityManager.find( Task.class, Long.parseLong( request.getParameter("id") ) );
            if( task == null || !task.getOrganisation().getId().equals(organisation_id) ){
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "error");
                return;
            }
            entityManager.refresh(task);
            System.out.println( task.getTaskID() );
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            JSONObject output = new JSONObject();
            
            output.put("title", task.getTitle());
            output.put( "description", task.getDescription() == null ? JSONObject.NULL : task.getDescription() );
            output.put("status", task.getStatus());
            Worker worker = task.getAssignedWorker();
            output.put("assignedWorker", 
                    worker == null ? JSONObject.NULL :
                            worker.getShortcut()
            );
            
            Unit unit = task.getAssignedUnit();
            output.put( "assignedUnit", unit == null ? JSONObject.NULL : 
                    new JSONObject()
                    .put( "name", unit.getUnitName() )
                    .put("id", unit.getUnitID())
                    );
            output.put("statusChangeReason", task.getStatusChangeReason());
            output.put( "statusEditor", task.getStatusEditor().getShortcut() );
            output.put("creationDate", task.getCreationDate());
            output.put( "editDate", task.getLastEdited());
            
            output.put( "comments", task.getComments().stream().map( 
                    (Comment comment)->{
                        return new JSONObject()
                                .put("text", comment.getText() )
                                .put( "worker", comment.getWorker().getShortcut() )
                                .put( "time", comment.getCommentTime() )
                                .put( "date", comment.getCommentDate() );
                    }
            
            ).toList()
                    );
            
            
            
            out.print( output.toString() );
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
