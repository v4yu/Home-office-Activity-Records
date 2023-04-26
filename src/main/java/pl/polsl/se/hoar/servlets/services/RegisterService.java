/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
import pl.polsl.se.hoar.email.EmailServletResponse;
import pl.polsl.se.hoar.email.MailSender;
import pl.polsl.se.hoar.entities.Invitation;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "RegisterService", urlPatterns = {"/RegistrationService"})
public class RegisterService extends HttpServlet {

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
            String jsonString = "";
            for (String line : request.getReader().lines().toList()) {
                jsonString += line;
            }
            EntityManager em = factory.createEntityManager();

            String invitationId = (String) request.getAttribute("invitation");
            Invitation invitation = null;
            if (invitationId != null) {
                invitation = em.find(Invitation.class, Long.valueOf(invitationId));
            }

            System.out.println(jsonString);
            JSONObject obj = new JSONObject(jsonString);
            String email = obj.getString("email");
            String password = obj.getString("password");
            String repeatedPassword = obj.getString("repeated_password");
            if (!password.equals(repeatedPassword)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passwords don't match");
                return;
            }

            if (invitation != null) {
                if (!email.equals(invitation.getEmail())) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email doesn't match invitation");
                    return;
                }
            }

            Query query = em.createQuery("select u FROM User as u where u.email=:email", User.class);
            query.setParameter("email", email);
            boolean status_ok = true;
            String message = null;

            if (!query.getResultList().isEmpty()) {
                status_ok = false;
                message = "Email already taken";
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setIsActive(false);
            user.setJoinDate(Date.valueOf(java.time.LocalDate.now()));

            if (status_ok) {
                try {
                    em.getTransaction().begin();
                    em.persist(user);
                    if (invitation != null) {
                        Worker worker = new Worker();
                        worker.setOrganisation(invitation.getWorker().getOrganisation());
                        worker.setUser(user);
                        worker.setPermissions(0);
                        worker.setWorkJoinDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
                        worker.setIsActive(true);

                        em.persist(worker);
                        em.remove(invitation);
                    }
                    em.getTransaction().commit();
                    System.out.println("Object persisted");
                } catch (PersistenceException e) {
                    e.printStackTrace();
                    em.getTransaction().rollback();
                }
            }

            EmailServletResponse res = new EmailServletResponse();
            request.setAttribute("link", "http://localhost:8080" + request.getContextPath() + "/Activate?id=" + user.getId() );
            request.getRequestDispatcher("/WEB-INF/emailconfirmation.jsp").include(request, res);

            MailSender.sendEmail(email, "Email confirmation", res.toString());

            JSONObject resp = new JSONObject();
            resp.put("status", status_ok ? "ok" : "error");
            if (!status_ok) {
                resp.put("message", message);
            }

            out.print(resp);
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
