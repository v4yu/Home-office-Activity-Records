/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package pl.polsl.se.hoar.servlets.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import pl.polsl.se.hoar.entities.Task;
import pl.polsl.se.hoar.entities.Unit;
import pl.polsl.se.hoar.entities.User;
import pl.polsl.se.hoar.entities.Worker;

/**
 *
 * @author FRANC
 */
@WebServlet(name = "TaskList", urlPatterns = {"/TaskList"})
public class TaskList extends HttpServlet {

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
            Long organisation_id = Long.parseLong((String) request.getAttribute("organisation"));
            System.out.println(organisation_id);
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("pl.polsl.se.hoard");
            
            HttpSession session = request.getSession();
            EntityManager entityManager = factory.createEntityManager();
            try {
                User user = (User) session.getAttribute("user");
                user = entityManager.merge(user);
                entityManager.getTransaction().begin();
                entityManager.refresh(user);
                Worker worker = null;
                for (Worker tmp : user.getWorkers()) {
                    if (tmp.getOrganisation().getId().equals(organisation_id)) {
                        worker = tmp;
                        System.out.println(tmp);
                    }
                }
                System.out.println( worker );
                if( worker == null ){
                    System.out.println( worker );
                    response.sendError( HttpServletResponse.SC_FORBIDDEN, "You don't have access to this" );
                    return;
                }
                
                
                entityManager.refresh(worker);
                String dateFromString = request.getParameter("dateFrom");
                String dateToString = request.getParameter("dateTo");
                String useHierarchyString = request.getParameter("useHierarchy");
                
                int hierarchyDepth = 0;
                   
                boolean useHierarchy = Boolean.parseBoolean(useHierarchyString);
                if( useHierarchy ){
                    String hierarchyDepthString = request.getParameter("hierarchyDepth");
                    hierarchyDepth = Integer.parseInt(hierarchyDepthString);
                }
                
                Unit unit = worker.getUnit();
                
                Stream<Task> tasks = worker.getTasks().stream();
                
                Date dateFrom = dateFromString == null ? null : Date.valueOf(dateFromString);
                Date dateTo = dateToString == null ? null : Date.valueOf(dateToString);
                if( useHierarchy ){
                    if( unit != null )
                        tasks = getHierarchyTasks(unit, hierarchyDepth);
                }
                else{
                    if( unit != null )
                        tasks = Stream.concat(tasks, unit.getTasks().stream());
                }
                Stream<JSONObject> output = tasks
                        .filter( t -> {
                            if( dateFrom != null )
                                return t.getCreationDate().compareTo(dateFrom) >= 0;
                            else
                                return true;
                        } )
                        .filter( t->{
                            if( dateTo != null )
                                return t.getCreationDate().compareTo(dateTo) <= 0;
                            else
                                return true;
                        } )
                        .map( task-> {
                            return new JSONObject()
                                    .put( "title", task.getTitle() )
                                    .put("status", task.getStatus())
                                    .put("category", 
                                            new JSONObject()
                                                .put("id", task.getCategory().getID())
                                                .put( "name", task.getCategory().getCategoryName() )
                                    )
                                    .put( "assignedUnit", 
                                            task.getAssignedUnit() == null ? JSONObject.NULL:
                                                    new JSONObject().put( "id", task.getAssignedUnit().getUnitID() )
                                                    .put("name", task.getAssignedUnit().getUnitName())
                                        )
                                    .put( "assignedWorker", 
                                            task.getAssignedWorker() == null ? JSONObject.NULL : 
                                                    task.getAssignedWorker().getShortcut()
                                        )
                                    .put("id", task.getTaskID() );
                        })
                        ;
                
                out.print( new JSONObject().put( "tasks", output.toList() ).toString() );

            } catch (PersistenceException e) {

            }
            finally{
                entityManager.getTransaction().rollback();
                entityManager.close();
            }
            

        }
    }

    private Stream<Task> getHierarchyTasks( Unit unit, int depth ){
        Stream<Task> output = unit.getTasks().stream();
        for( Worker worker : unit.getWorkers() )
                output = Stream.concat( output, worker.getTasks().stream() );
        if(depth > 0){
            for( Unit child : unit.getChildren() )
                output = Stream.concat(output, getHierarchyTasks( child, depth-1 ) );
        }
        
        return output;
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
