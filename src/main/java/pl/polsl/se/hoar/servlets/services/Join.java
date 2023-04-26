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
import pl.polsl.se.hoar.entities.Invitation;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "Join", urlPatterns = {"/Join"})
public class Join extends HttpServlet {

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
        EntityManager em = factory.createEntityManager();

        String invitationIdString = request.getParameter("invitation");
        if (invitationIdString == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No invitation specified");
            return;
        }

        Invitation invitation = em.find(Invitation.class, Long.valueOf(invitationIdString));
        if (invitation == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid invitation");
            return;
        }
        
        em.getTransaction().begin();
        Worker worker = new Worker();
        worker.setOrganisation(invitation.getWorker().getOrganisation());
        worker.setUser(invitation.getUser());
        worker.setPermissions(0);
        worker.setWorkJoinDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
        worker.setIsActive(true);

        em.persist(worker);
        em.remove(invitation);
        
        em.getTransaction().commit();

        response.sendRedirect("Dashboard");
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
