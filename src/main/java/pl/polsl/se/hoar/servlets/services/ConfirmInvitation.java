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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
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
@WebServlet(name = "ConfirmInvitation", urlPatterns = {"/ConfirmInvitation"})
public class ConfirmInvitation extends HttpServlet {

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
            
            if( worker == null || ( (worker.getPermissions() & Worker.PERMISSION_HR) == 0 && (worker.getPermissions() & Worker.PERMISSION_ADMIN) == 0 ) ){
                out.print( new JSONObject().put("status", "error").put("message", "You don't have permissions") );
                return;
            }
            
            List<Invitation> pendingInvitations = (List<Invitation>) request.getSession().getAttribute("pendingInvitations");
            
            if( pendingInvitations == null ){
                out.print( new JSONObject().put( "status", "error" ).put( "message", "There's nobody to invite" ) );
                return;
            }
            
            String inputString = "";
            for (String line : request.getReader().lines().toList()) {
                inputString += line;
            }
            JSONObject obj = new JSONObject(inputString);
                //invite confirmed
                JSONArray confirmedInvites = obj.getJSONArray("confirmedInvites");

                em.getTransaction().begin();
                try {

                    for (int i = 0; i < confirmedInvites.length(); i++) {
                        Invitation invitation = pendingInvitations.get(confirmedInvites.getInt(i));
                        em.persist(invitation);
                    }

                    em.getTransaction().commit();

                    for (int i = 0; i < confirmedInvites.length(); i++) {
                        Invitation invitation = pendingInvitations.get(confirmedInvites.getInt(i));
                        User invitedUser = invitation.getUser();

                        EmailServletResponse res = new EmailServletResponse();
                        if (invitedUser == null) {
                            request.setAttribute("link", "http://localhost:8080" + request.getContextPath() + "/Invitation/" + invitation.getId() + "/Register");
                        } else {
                            request.setAttribute("link", "http://localhost:8080" + request.getContextPath() + "/Join?invitation=" + invitation.getId());
                        }
                        request.setAttribute("organisation", invitation.getWorker().getOrganisation().getName());
                        request.getRequestDispatcher("/WEB-INF/emailinvitation.jsp").include(request, res);

                        MailSender.sendEmail(invitation.getEmail(), "Organisation invitation", res.toString());
                    }

                } catch (PersistenceException e) {
                    em.getTransaction().rollback();
                    out.print( new JSONObject().put( "status", "error" ).put("message", "Problems with database") );
                    return;
                }

                em.close();

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
