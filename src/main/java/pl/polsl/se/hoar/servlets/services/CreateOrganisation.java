/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
import pl.polsl.se.hoar.entities.Organisation;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "CreateOrganisation", urlPatterns = {"/CreateOrganisation"})
public class CreateOrganisation extends HttpServlet {

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
            User user =(User) request.getSession().getAttribute("user");
            if( user == null ){
                out.print( new JSONObject().put("status", "error").put("message", "session issue") );
                return;
            }
            
            String jsonString = "";
            for( String line : request.getReader().lines().toList() ){
                jsonString += line;
            }
            JSONObject obj = new JSONObject(jsonString);
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            EntityManager em = factory.createEntityManager();
            
            user = em.merge(user);
            em.refresh(user);
            
            em.getTransaction().begin();
            
            Organisation organisation = new Organisation();
            try{organisation.setName( obj.getString("name") );}catch(JSONException e){}
            try{organisation.setDescription( obj.getString("description") );}catch(JSONException e){}
            try{organisation.setNIP(obj.getString("NIP"));}catch(JSONException e){}
            try{organisation.setREGON(obj.getString("REGON"));}catch(JSONException e){}
            em.persist(organisation);
            em.getTransaction().commit();
            em.getTransaction().begin();
            Unit unit = new Unit();
            
            unit.setIsUnitActive(true);
            unit.setUnitName("Organisation");
            unit.setOrganisation(organisation);
            organisation.setMainUnit(unit);
            
           
            
            Worker worker = new Worker();
            worker.setUser( user );
            worker.setOrganisation( organisation );
            worker.setPermissions(Worker.PERMISSION_ADMIN);
            worker.setWorkJoinDate( Date.valueOf(java.time.LocalDate.now()) );
            worker.setIsActive(true);
            worker.setPosition("Owner");
            worker.setUnit(unit);
            em.persist(worker);
            
            em.getTransaction().commit();
            
            
            JSONObject outJSON = new JSONObject();
            outJSON.put("status", "ok");
            outJSON.put("id", organisation.getId());
            outJSON.put("mainUnit", unit.getUnitID() );
            out.println(outJSON.toString());
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
