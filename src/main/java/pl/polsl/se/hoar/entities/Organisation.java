/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Szymon
 */
@Entity
public class Organisation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Organisation_ID")
    private Long organisationID;

    public Long getId() {
        return organisationID;
    }

    public void setId(Long organisationID) {
        this.organisationID = organisationID;
    }
    
    @Column(name="Name")
    private String name;
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    
    @Column(name="Description")
    private String description;
    public String getDescription(){return this.description;}
    public void setDescription(String description){this.description = description;}
    
    @Column(name="NIP")
    private String NIP;
    public String getNIP(){return this.NIP;}
    public void setNIP(String NIP){this.NIP = NIP;}
    
    @Column(name="REGON") // w ddl jest REGON
    private String REGON;
    public String getREGON(){return this.REGON;}
    public void setREGON(String REGON){this.REGON = REGON;}
    
    @OneToMany(mappedBy = "organisation")
    private Set<Category> categories; 

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
    

    public Long getOrganisationID() {
        return organisationID;
    }
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MAIN_UNIT_ID", referencedColumnName = "unit_id")
    private Unit mainUnit;

    public Unit getMainUnit() {
        return mainUnit;
    }

    public void setMainUnit(Unit mainUnit) {
        this.mainUnit = mainUnit;
    }
    

    public void setOrganisationID(Long organisationID) {
        this.organisationID = organisationID;
    }


    public Set<Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public void setUnits(Set<Unit> units) {
        this.units = units;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
    
    
    @OneToMany(mappedBy = "organisation")
    private Set<Worker> workers;
    
    @OneToMany(mappedBy = "organisation")
    private Set<Unit> units;

    
    @OneToMany(mappedBy = "organisation")
    private Set<Task> tasks;
    
    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (organisationID != null ? organisationID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Organisation)) {
            return false;
        }
        Organisation other = (Organisation) object;
        if ((this.organisationID == null && other.organisationID != null) || (this.organisationID != null && !this.organisationID.equals(other.organisationID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Organisation[ id=" + organisationID + " ]";
    }
    
}
