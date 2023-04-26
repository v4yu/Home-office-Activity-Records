/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
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
 * @author FRANC
 */
@Entity
public class Unit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Unit_ID")
    private Long unitID;


    @ManyToOne //??? Not sure on dis one
    @JoinColumn(name="Parent_ID")
    private Unit parent;
    public Unit getParent(){return parent;}
    public void setParent( Unit parent ){this.parent = parent;}
    
    @OneToMany(mappedBy="parent")
    private Set<Unit> children;

    public Long getUnitID() {
        return unitID;
    }

    public void setUnitID(Long unitID) {
        this.unitID = unitID;
    }

    public Set<Unit> getChildren() {
        return children;
    }

    public void setChildren(Set<Unit> children) {
        this.children = children;
    }
    
    @ManyToOne
    @JoinColumn(name="Organisation_ID", referencedColumnName="organisation_id")
    private Organisation organisation;
    
    @OneToMany(mappedBy = "assignedUnit")
    private List<Task> tasks;
    public List<Task> getTasks(){return tasks;}
    public void setTasks( List<Task> tasks ){this.tasks = tasks;}

    
    @Column(name="Unit_Name")
    private String unitName;
    
    @Column(name="Is_Unit_Active")
    private Boolean isUnitActive;

    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }
   

   @OneToOne
   @JoinColumn(name="Manager_ID")  
    private Worker manager; // Unidirectional nie ? Wiec tylko w jedna strone, bez mapowania po stronie workera
   
   @OneToMany(mappedBy = "unit")
   private Set<Worker> workers;
   

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Worker getManager() {
        return manager;
    }

    public void setManager(Worker manager) {
        this.manager = manager;
    }


    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Boolean getIsUnitActive() {
        return isUnitActive;
    }

    public void setIsUnitActive(Boolean isUnitActive) {
        this.isUnitActive = isUnitActive;
    }


    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (unitID != null ? unitID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Unit)) {
            return false;
        }
        Unit other = (Unit) object;
        if ((this.unitID == null && other.unitID != null) || (this.unitID != null && !this.unitID.equals(other.unitID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Unit[ id=" + unitID + " ]";
    }
    
}
