/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Szymon
 */

public class CommentID implements Serializable {

    private Long worker;
    
    private Long task;
    
    private Date commentDate;
    
    private Time commentTime;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (worker != null ? worker.hashCode() : 0);
        hash += (task != null ? task.hashCode() : 0);
        hash += (commentDate != null ? commentDate.hashCode() : 0);
        hash += (commentTime != null ? commentTime.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CommentID)) {
            return false;
        }
        CommentID other = (CommentID) object;
        return !((this.worker == null && other.worker != null) || (this.worker != null && !this.worker.equals(other.worker))||
                (this.task == null && other.task != null) || (this.task != null && !this.task.equals(other.task)) ||
                (this.commentDate == null && other.commentDate != null) || (this.commentDate != null && !this.commentDate.equals(other.commentDate)) ||
                (this.commentTime == null && other.commentTime != null) || (this.commentTime != null && !this.commentTime.equals(other.commentTime)));
    }
    
}
