package model;

public class User {

  public int UserID;
  public String userName;
  public String userPassword;

  public User(int userID, String userName, String userPassword) {
    UserID = userID;
    this.userName = userName;
    this.userPassword = userPassword;
  }
  public User(){}

  public int getUserID() {
    return UserID;
  }

  public void setUserID(int userID) {
    UserID = userID;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }
}
