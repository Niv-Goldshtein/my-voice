package com.example.myvoice;
import java.io.Serializable;
/**
 * create History class
 */
public class History implements Serializable{
    private String time;
    private String whatIdid;
    private String details;
    /**
     * empty constructor function
     */
    History(){}
    /**
     * history contructor function
     * @param time when you did what you do
     * @param whatIdid what you did
     * @param details details of what you did
     */
    History(String time,String whatIdid,String details){
        this.time     = time;
        this.whatIdid = whatIdid;
        this.details  = details;
    }
    /**
     * tostring to be able to show history in listview
     * @return history line
     */
    @Override
    public String toString() {
        return "you "+ whatIdid + " at " + time + " details " + details;
    }
    /**
     * get the details of the history
     * @return the details
     */
    public String getDetails() {
        return details;
    }
    /**
     * get the time of the history,when it happeneed
     * @return time
     */
    public String getTime() {
        return time;
    }
    /**
     * get what you did in  history
     * @return what you did
     */
    public String getWhatIdid() {
        return whatIdid;
    }
    /**
     * set the time of what you did in history
     * @param time when you did what you did
     */
    public void setTime(String time) {
        this.time = time;
    }
    public boolean isTheSame(History other){
        if (this.whatIdid.equals(other.getWhatIdid().toString())&&this.details.equals(other.getDetails().toString())
                &&this.time.equals(other.getTime().toString()))
            return true;
        else
            return false;
    }
}
