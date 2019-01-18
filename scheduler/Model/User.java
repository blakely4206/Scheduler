
package scheduler.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class User {
   
    private SimpleIntegerProperty userID;
    private SimpleStringProperty userName;

    public User(int userID, String userName) {
        this.userID = new SimpleIntegerProperty(userID);
        this.userName = new SimpleStringProperty(userName);
    }
    public int getUserID() {
        return userID.get();
    }

    public void setUserID(int userID) {
        this.userID = new SimpleIntegerProperty(userID);
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName = new SimpleStringProperty(userName);
    }

    @Override
    public String toString() {
        return userName.get();
    }
}

