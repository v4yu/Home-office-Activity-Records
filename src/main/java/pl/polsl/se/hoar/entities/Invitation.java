/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author Szymon
 */
@Entity
public class Invitation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Invitation_ID")
    private Long invitationID;

    public Long getId() {
        return invitationID;
    }

    public void setId(Long invitationID) {
        this.invitationID = invitationID;
    }
    
  

   
    @OneToOne
    @JoinColumn(name="User_ID")
    private User user;
    
    
    
    @Column(name="Email")
     private String Email;
    public String getEmail(){return this.Email;}
    public void setEmail( String email ){this.Email = email;}

    @ManyToOne
    @JoinColumn(name="Worker_ID")
    private Worker worker;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (invitationID != null ? invitationID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invitation)) {
            return false;
        }
        Invitation other = (Invitation) object;
        if ((this.invitationID == null && other.invitationID != null) || (this.invitationID != null && !this.invitationID.equals(other.invitationID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Invite[ id=" + invitationID + " ]";
    }
    
}
