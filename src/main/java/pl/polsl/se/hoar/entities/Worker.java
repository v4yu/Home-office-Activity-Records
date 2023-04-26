/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.json.JSONObject;

/**
 *
 * @author Szymon
 */
@Entity
public class Worker implements Serializable {
    //No "Position" for now
    private static final long serialVersionUID = 1L;
    public static final int PERMISSION_MANAGER = 1;
    public static final int PERMISSION_DICTIONARY = 2;
    public static final int PERMISSION_HR = 4;
    public static final int PERMISSION_ADMIN = 8;
   
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Worker_ID")
    private Long workerID;

    public Long getWorkerID() {
        return workerID;
    }

    public void setWorkerID(Long workerID) {
        this.workerID = workerID;
    }
    
    @Column(name="Position")
    private String position;

    

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    
   @ManyToOne
   @JoinColumn(name="USER_ID")
   private User user;
   public User getUser(){return user;}
   public void setUser( User user ){this.user = user;}
    
   @ManyToOne
   @JoinColumn(name="parent_unit_id")
   private Unit unit;
   public Unit getUnit(){return unit;}
   public void setUnit(Unit unit){this.unit = unit;}
    
   @OneToMany(mappedBy="assignedWorker")
   private Set<Task> tasks;
   public Set<Task> getTasks(){return this.tasks;}
   public void setTasks( Set<Task> tasks ){this.tasks = tasks;}
    
   @Column(name="permisions")
   private int permissions;
   public int getPermissions(){return permissions;}
   public void setPermissions(int permissions){this.permissions = permissions;}
  
   @ManyToOne
   @JoinColumn(name="organisation_id")
   private Organisation organisation;
   public Organisation getOrganisation(){return organisation;}
   public void setOrganisation(Organisation organisation){this.organisation = organisation;}
  
   @Column(name="work_join_date")
     private Date workJoinDate;
    public Date getWorkJoinDate(){return workJoinDate;}
    public void setWorkJoinDate(Date workJoinDate){this.workJoinDate = workJoinDate;}
   
    
   @Column(name="is_active")
   private Boolean isActive;
   public Boolean getIsActive(){return isActive;}
   public void setIsActive(Boolean isActive){this.isActive = isActive;}
 
   
   public JSONObject getShortcut(){
            return new JSONObject()
                    .put("id", workerID)
                    .put("name", user.getName() != null ? user.getName() : user.getSurname() == null ? user.getEmail() : JSONObject.NULL ) 
                    .put("surname", user.getSurname() != null ? user.getSurname() : user.getName() == null ? "" : JSONObject.NULL );
        }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workerID != null ? workerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Worker)) {
            return false;
        }
        Worker other = (Worker) object;
        if ((this.workerID == null && other.workerID != null) || (this.workerID != null && !this.workerID.equals(other.workerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Worker[ id=" + workerID + " ]";
    }
    
}
