/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
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
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.polsl.se.hoar.email.EmailServletResponse;
import pl.polsl.se.hoar.email.MailSender;
import pl.polsl.se.hoar.entities.Invitation;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "MassInvite", urlPatterns = {"/MassInvite"})
public class MassInvite extends HttpServlet {

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
            EntityManager em = factory.createEntityManager();

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
            currentUser = em.merge(currentUser);
            em.refresh(currentUser);

            Worker worker = currentUser.getWorkerByOrganisation(organisation_id);

            em.refresh(worker);
            
            if( worker == null || ( (worker.getPermissions() & Worker.PERMISSION_HR) == 0 && (worker.getPermissions() & Worker.PERMISSION_ADMIN) == 0 ) ){
                out.print( new JSONObject().put("status", "error").put("message", "You don't have permissions") );
                return;
            }
            
            String inputString = "";
            for (String line : request.getReader().lines().toList()) {
                inputString += line;
            }

            //parse CSV: email, name, surname
            String[] values = inputString.split(",");
            JSONArray array = new JSONArray();
            List<Invitation> pendingInvitations = new LinkedList<Invitation>();

            for (int i = 0; i < values.length / 3; i++) {
                Invitation invitation = new Invitation();
                String email = values[i * 3];
                String regex = "(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@(([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))";
                if( !email.matches(regex) ){
                    continue;
                }
                
                Query query = em.createQuery("select u FROM User as u where u.email=:email", User.class);
                query.setParameter("email", email);

                User invitedUser = null;
                

                @SuppressWarnings("unchecked")
                List<User> userList = query.getResultList();
                if (userList.size() == 1) {
                    invitedUser = userList.get(0);
                }

                if( invitedUser != null && invitedUser.getWorkerByOrganisation( organisation_id )!= null )
                    continue;
                
                invitation.setEmail(email);
                invitation.setWorker(worker);
                invitation.setUser(invitedUser);
                pendingInvitations.add(invitation);

                array.put(new JSONObject().put("email", email)
                        .put("name", values[i * 3 + 1])
                        .put("surname", values[i * 3 + 2]));
            }

            request.getSession().setAttribute("pendingInvitations", pendingInvitations);
            
            out.print(new JSONObject().put("status", "ok")
                    .put("people", array));
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
