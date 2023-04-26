/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.polsl.se.hoar.entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Szymon
 */
@Entity
@IdClass(CommentID.class)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    

    @Id
    @ManyToOne
    @JoinColumn(name="Worker_ID")
    private Worker worker;
    
    @Id
    @ManyToOne
    @JoinColumn(name="Task_ID")
    private Task task;
    
    @Id
    @Column(name="Comment_Date")
    private Date commentDate;

    @Id
    @Column(name = "Comment_Time")
    private Time commentTime;

    public Time getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Time commentTime) {
        this.commentTime = commentTime;
    }
    
    
    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }
    
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        return !((this.worker == null && other.worker != null) || (this.worker != null && !this.worker.equals(other.worker))||
                (this.task == null && other.task != null) || (this.task != null && !this.task.equals(other.task)) ||
                (this.commentDate == null && other.commentDate != null) || (this.commentDate != null && !this.commentDate.equals(other.commentDate)) ||
                (this.commentTime == null && other.commentTime != null) || (this.commentTime != null && !this.commentTime.equals(other.commentTime)));
    }
    
    @Override
    public String toString() {
        return "pl.polsl.se.hoar.entities.Comment[ task=" + task.getTaskID() + ", worker= "+ worker.getWorkerID()+ "time = " + commentDate.toString()+" " + commentTime.toString() +  " ]";
    }
    
}
