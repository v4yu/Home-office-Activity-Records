/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.PrintWriter;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

/**
 *
 * @author MichaĹ‚ Ferenc
 */
@Entity
@Table(name="LAB.\"User\"")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
   
    
     public boolean checkPermission(Long organisation_id, int... Flags) {
        Worker worker = getWorkerByOrganisation(organisation_id);
            
        if (worker == null ) {
          return false;
        }  
        
        if((worker.getPermissions() & Worker.PERMISSION_ADMIN)!=0)
        {
          return true;       
        } 
        
        boolean returnVal = false;
        for (int permission : Flags)
        {
            if((worker.getPermissions() & permission)!=0)
            {
                returnVal = true;       
            }
        }    


        
     return returnVal;
     }
  
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="User_ID")
    private int id;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Column(name="Email")
    private String email;
    
    public String getEmail(){return this.email;}
    
    public void setEmail( String email ){this.email = email;}
    @Column(name="Password")
    private String password;
    
    public String getPassword(){return this.password;}
    
    public void setPassword( String password ){this.password = password;}

    @Column(name="Name")
    private String name;
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    
    private String surname;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    
    @OneToMany( mappedBy="user" )
    private Set<Worker> workers;
    public Set<Worker> getWorkers(){return workers;}
    public void setWorkers(Set<Worker> workers){this.workers = workers;}

    
    @Column(name="Join_Date")
    private Date joinDate;
    public Date getJoinDate(){return joinDate;}
    public void setJoinDate(Date joinDate){this.joinDate = joinDate;}
    
    @Column(name="Description")
    private String description;
    public String getDescription(){return this.description;}
    public void setDescription(String description){this.description = description;}
    
    @Column(name="is_active")
    private boolean isActive;
    public boolean getIsActive(){return isActive;}
    public void setIsActive(boolean isActive){this.isActive = isActive;}
    
    public Worker getWorkerByOrganisation(Organisation organisation){
        Worker worker = null;
            for( Worker tmp: workers )
                if( tmp.getOrganisation().equals( organisation ) )
                    worker = tmp;
        return worker;
    }
    
    public Worker getWorkerByOrganisation(Long organisation_id){
        Worker worker = null;
            for( Worker tmp: workers )
                if( tmp.getOrganisation().getId().equals( organisation_id ) )
                    worker = tmp;
        return worker;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.User[ id=" + id + " ]";
    }
    
}
