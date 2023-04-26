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
import pl.polsl.se.hoar.entities.Category;
import pl.polsl.se.hoar.entities.Organisation;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "CreateCategory", urlPatterns = {"/CreateCategory"})
public class CreateCategory extends HttpServlet {

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
            if(request.getAttribute("organisation") ==null)
            {
                out.print(new JSONObject().put("status", "error")
                    .put("message", "invalid organisation") );
                    return;
            }
            Long organisation_id = Long.valueOf( (String) request.getAttribute("organisation") );
            
            User user = (User) request.getSession().getAttribute("user");
            if(  user == null){
                out.print( new JSONObject().put( "status", "error").put("message", "session issue"));
                return;
            }
            
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            String jsonString = "";
            for( String line : request.getReader().lines().toList() ){
                jsonString += line;
            }
            JSONObject obj = new JSONObject(jsonString);
            
            if(!obj.isNull("name") && !obj.isNull("description"))
            {
            EntityManager em = factory.createEntityManager();
            
            user = em.merge(user);
            em.refresh(user);
            Worker worker = user.getWorkerByOrganisation(organisation_id);
            if( worker == null || ( (worker.getPermissions()&Worker.PERMISSION_ADMIN) == 0 && (worker.getPermissions() & Worker.PERMISSION_DICTIONARY) == 0  ) ){
                out.print( new JSONObject().put("status", "error").put("message", "You don't have permissions to do this") );
                return;
            }
            
            Category category = new Category();
            category.setCategoryName( obj.getString("name") );
            category.setDescription( obj.optString("description") );
            category.setOrganisation( em.find( Organisation.class, organisation_id) );
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
            
            out.println( new JSONObject().put("status", "ok").put("id", category.getID()) );
   
            }
            else
            {
                out.print(new JSONObject().put("status", "error")
                .put("message", "invalid name or description") );
                return;
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
