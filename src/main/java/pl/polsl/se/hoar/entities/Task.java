/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Szymon
 */
@Entity
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="task_id")
    private Long taskID;


    @Column(name="Title")
    private String title;
    public String getTitle(){return this.title;}
    public void setTitle(String title){this.title = title;}
    
    @ManyToOne
    @JoinColumn(name = "Organisation_ID")
    private Organisation organisation;

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    
    @ManyToOne
    @JoinColumn(name = "Category_ID")
    private Category category;

    
    public Long getTaskID() {
        return taskID;
    }

    public void setTaskID(Long taskID) {
        this.taskID = taskID;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    
    
    @Column(name="Description")
    private String description;
    public String getDescription(){return this.description;}
    public void setDescription(String description){this.description = description;}
    
    @ManyToOne
    @JoinColumn(name="assigned_worker_id")
    private Worker assignedWorker;
    public Worker getAssignedWorker(){return assignedWorker;}
    public void setAssignedWorker(Worker assignedWorker){this.assignedWorker = assignedWorker;}

    @ManyToOne
    @JoinColumn(name="Assigned_unit_id")
    private Unit assignedUnit;
    public Unit getAssignedUnit(){return assignedUnit;}
    public void setAssignedUnit(Unit assignedUnit){this.assignedUnit = assignedUnit;}

    @Column(name="Creation_date")
    private Date creationDate;
    public Date getCreationDate(){return creationDate;}
    public void setCreationDate(Date creationDate){this.creationDate = creationDate;}
    
    @Column(name="status")
    private int status;
    public int getStatus(){return status;}
    public void setStatus(int status){this.status = status;}
    
    @Column(name="last_edit")
    private Date lastEdited;
    public Date getLastEdited(){return lastEdited;}
    public void setLastEdited(Date lastEdited){this.lastEdited = lastEdited;}
    
    @Column(name="status_change_reason")
   private String statusChangeReason;
    public String getStatusChangeReason(){return this.statusChangeReason;}
    public void setStatusChangeReason(String statusChangeReason){this.statusChangeReason = statusChangeReason;}
   
    @ManyToOne
    @JoinColumn(name="status_editor_id")
    private Worker statusEditor;
    public Worker getStatusEditor(){return statusEditor;}
    public void setStatusEditor(Worker statusEditor){this.statusEditor = statusEditor;}

    @OneToMany(mappedBy = "task") //foren kij issues
    private Set<Comment> comments;
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskID != null ? taskID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Task)) {
            return false;
        }
        Task other = (Task) object;
        if ((this.taskID == null && other.taskID != null) || (this.taskID != null && !this.taskID.equals(other.taskID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Task[ id=" + taskID + " ]";
    }
    
}
