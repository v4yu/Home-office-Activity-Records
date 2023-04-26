/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.User;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "ChangePassword", urlPatterns = {"/ChangePassword"})
public class ChangePassword extends HttpServlet {

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
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            EntityManager em = factory.createEntityManager();
            String jsonString = "";
            for (String line : request.getReader().lines().toList()) {
                jsonString += line;
            }
            JSONObject obj = new JSONObject(jsonString);
            
            String password;
            String newPassword;
            String repeatedPassword;
            
           if( !obj.isNull("password") && !obj.isNull("new_password") && !obj.isNull("repeated_new_password"))
           {
             password             = obj.getString("password");
             newPassword          = obj.getString("new_password");
             repeatedPassword     = obj.getString("repeated_new_password");
                if (!newPassword.equals(repeatedPassword)) {
                    out.print(new JSONObject().put("status", "error")
                    .put("message", "Passwords dont match") );
                    return;
                }
           }
           else
           {
                out.print(new JSONObject().put("status", "error")
                    .put("message", "Invalid input") );
                return;
           }
           
           

           
            User user = em.merge( (User) request.getSession().getAttribute("user") );
            em.refresh(user);
            
            if( !user.getPassword().equals(password) ){
                out.print( new JSONObject().put("status", "error").put("message", "wrong password") );
                return;
            }
                

                em.getTransaction().begin();
                try {
                    user.setPassword(newPassword);
                    em.getTransaction().commit();
                    System.out.println("Object persisted");
                     out.print(new JSONObject().put("status", "ok") ); 
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    em.getTransaction().rollback();
                } finally {
                    em.close();
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
