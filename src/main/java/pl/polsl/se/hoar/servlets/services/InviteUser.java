/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Invitation;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;
import pl.polsl.se.hoar.email.EmailServletResponse;
import pl.polsl.se.hoar.email.MailSender;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "InviteUser", urlPatterns = {"/InviteUser"})
public class InviteUser extends HttpServlet {

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

            if (request.getAttribute("organisation") == null) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "invalid organisation"));
                return;
            }
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "session issue"));
                return;
            }
            Long organisation_id = Long.valueOf((String) request.getAttribute("organisation"));
            String jsonString = "";
            for (String line : request.getReader().lines().toList()) {
                jsonString += line;
            }
            JSONObject obj = new JSONObject(jsonString);

            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            EntityManager em = factory.createEntityManager();

            user = em.merge(user);
            em.refresh(user);
            if (!user.checkPermission(organisation_id, Worker.PERMISSION_HR)) {
                out.print(new JSONObject().put("status", "error").put("message", "You don't have permissions"));
                return;
            }
            Invitation invitation = new Invitation();

            if (obj.isNull("email")) {
                out.print(new JSONObject().put("status", "error")
                        .put("message", "invalid email"));
                return;
            }

            String email = obj.getString("email");
            Query query = em.createQuery("select u FROM User as u where u.email=:email", User.class);
            query.setParameter("email", email);

            User currentUser = (User) request.getSession().getAttribute("user");
            User invitedUser = null;

            @SuppressWarnings("unchecked")
            List<User> userList = query.getResultList();
            if (userList.size() == 1) {
                invitedUser = userList.get(0);
            }

            invitation.setEmail(email);
            invitation.setUser(invitedUser);

            Worker worker = currentUser.getWorkerByOrganisation(organisation_id);

            invitation.setWorker(worker);

            em.getTransaction().begin();
            em.persist(invitation);
            em.getTransaction().commit();

            EmailServletResponse res = new EmailServletResponse();
            if (invitedUser == null) {
                request.setAttribute("link", "http://localhost:8080" + request.getContextPath() + "/Invitation/" + invitation.getId() + "/Register");
            } else {
                request.setAttribute("link", "http://localhost:8080" + request.getContextPath() + "/Join?invitation=" + invitation.getId());
            }
            request.setAttribute("organisation", worker.getOrganisation().getName());
            request.getRequestDispatcher("/WEB-INF/emailinvitation.jsp").include(request, res);

            MailSender.sendEmail(email, "Organisation invitation", res.toString());
            out.print(new JSONObject().put("status", "ok"));
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
