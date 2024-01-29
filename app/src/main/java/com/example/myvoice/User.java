package com.example.myvoice;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * create User class
 */

public class User implements Serializable{
    private String username,password;
    private ArrayList<History>myHistory ;
    /**
     * empty constructor function
     */
    User(){}
    /**
     * constructor function
     * @param username the user's username
     * @param password the user's password
     */
    User(String username,String password){
        this.username = username;
        this.password = password;
    }
    /**
     * constructor function
     * @param username the user's username
     * @param password the user's password
     * @param myHistory the user's History ArrayList
     */
    User(String username,String password,ArrayList<History>myHistory){
        this.username = username;
        this.password = password;
        this.myHistory = myHistory;
    }
    /**
     * get the user's username
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }
    /**
     * get the user's password
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }
    /**
     * get the user's History ArrayList
     * @return the user's History ArrayList
     */
    public ArrayList<History> getMyHistory() {
        return myHistory;
    }
    /**
     * set the user's password
     * @param password the password you want to set in the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * set the user's History ArrayList
     * @param myHistory the History ArrayList you set in the user's History ArrayList
     */
    public void setMyHistory(ArrayList<History> myHistory) {
        this.myHistory = myHistory;
    }
    /**
     *check if the user's arrayList of history is empty
     * @return if the user's ArrayList is empty
     */
    public boolean HasArray(){
        if (this.getMyHistory()== null)
            return false;
        else
            return true;
    }
}
