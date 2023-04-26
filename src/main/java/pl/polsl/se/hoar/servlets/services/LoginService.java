/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import pl.polsl.se.hoar.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author Michał Ferenc
 */
@WebServlet(name = "LoginService", urlPatterns = {"/LoginService"})
public class LoginService extends HttpServlet {

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

            if(obj.isNull("email")|| obj.isNull("password"))
            {
                out.print(new JSONObject().put("status", "error")
                .put("message", "invalid cridentials") );
                return;                
            }
            String email = obj.getString("email");
            String password = obj.getString("password");
            if(email == null){
                email = "";
            }
            if(password == null){
                password = "";
            }
            System.out.println(email);
            System.out.println(password);
            EntityManager entityManager = factory.createEntityManager();
            Query query = entityManager.createQuery( "select u FROM User as u where u.email=:email", User.class  );
            query.setParameter("email", email);
            
            
            @SuppressWarnings("unchecked")
            List<User> userList = query.getResultList();
            entityManager.close();
            System.out.println(query.toString());
            boolean status_ok = true;
            if(userList.size() != 1){
                status_ok = false;
                System.out.println(userList.size());
            }
            User user = null;
            if(status_ok){
                user = userList.get(0);
                if( !user.getPassword().equals(password)  ){
                    status_ok = false;
                    System.out.println(user.getPassword());
                }   
            }
            HttpSession session = request.getSession();
            
            if(status_ok){

                out.print(new JSONObject().put("status", "ok"));
                session.setAttribute("user", user);
            }
            else
                out.print(new JSONObject().put("status", "error")
                .put("message", "Wrong cridentials"));
            
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
