package main;
import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
  private static final String protocol = "jdbc";
  private static final String vendor = ":mysql:";
  private static final String location = "//localhost/";
  private static final String databaseName = "client_schedule";
  private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
  private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
  private static final String userName = "sqlUser"; // Username
  private static String password = "Passw0rd!"; // Password
  private static Connection connection = null;  // Connection Interface
  private static PreparedStatement preparedStatement;

  public static User logInUser;

// test for git push from Lab for future
  public static  Connection makeConnection() {

    try {
      Class.forName(driver); // Locate Driver
      //password = Details.getPassword(); // Assign password
      connection = DriverManager.getConnection(jdbcUrl, userName, password); // reference Connection object
      System.out.println("Connection successful!");
    } catch (ClassNotFoundException | SQLException e) {
        e.getMessage();
    }
    return connection;

  }

  public static Connection getConnection() {

    return connection;
  }
  public static User getLogInUser(){
    return logInUser;
  }
  public static void closeConnection() {
    try {
      connection.close();
      //System.out.println("Connection closed!");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
    if (conn != null)
      preparedStatement = conn.prepareStatement(sqlStatement);
    else
      System.out.println("Prepared Statement Creation Failed!");
  }

  public static PreparedStatement getPreparedStatement() throws SQLException {
    if (preparedStatement != null)
      return preparedStatement;
    else System.out.println("Null reference to Prepared Statement");
    return null;
  }
}
