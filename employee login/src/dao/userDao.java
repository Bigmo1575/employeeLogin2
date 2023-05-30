package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Contacts;
import model.User;

public class userDao {

  public userDao(int userID, String userName, String userPassword) {
    super();
  }
  public static int validateUser(String userName, String userPassword) {
    try
    {
      String sqlQuery = "SELECT * FROM users WHERE user_name = '" + userName + "' AND password = '" + userPassword +"'";

      PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sqlQuery);
      ResultSet resultSets = preparedStatement.executeQuery();
      resultSets.next();
      if (resultSets.getString("User_Name").equals(userName))
      {
        if (resultSets.getString("Password").equals(userPassword))
        {
          return resultSets.getInt("User_ID");

        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  public static int getUserId(int userID) throws SQLException {
    PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM users WHERE User_ID = ?");
    ps.setInt(1, userID);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      userID = rs.getInt("User_ID");
    }
    return userID;
  }
  public static ObservableList<User> getUsers() throws SQLException{
    ObservableList<User> userObservableList = FXCollections.observableArrayList();
    String sql = "SELECT * from users";
    PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      int userID = rs.getInt("User_ID");
      String userName = rs.getString("User_Name");
      String Password = rs.getString("Password");
      User user = new User(userID, userName, Password);
      userObservableList.add(user);
    }
    return userObservableList;
  }
}
