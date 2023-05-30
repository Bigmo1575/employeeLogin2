package controller;

import dao.AppointmentDao;
import dao.userDao;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.DBConnection;
import model.Appointments;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class and methods to verify login, update login log, and set locale/language based on operating system settings.
 */

public class loginController implements Initializable {


  AppointmentDao appointmentDao = new AppointmentDao();
  @FXML private Button cancelButton;
  @FXML private Button loginButton;
  @FXML private TextField loginScreenLocationField;
  @FXML private TextField loginScreenPassword;
  @FXML private TextField loginScreenUsername;
  @FXML private Label passwordField;
  @FXML private Label usernameField;
  @FXML private Label loginField;
  @FXML private Button cancelButtonField;
  @FXML private Label locationText;

  Stage stage;

  /**
   *  Login button for main screen.
   * @param event
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   **/

  @FXML
  private void login(ActionEvent event) throws SQLException, IOException, Exception {
    try {
      //definitions for +/- 15 minute appointment check
      ObservableList<Appointments> getAllAppointments = appointmentDao.getAllAppointments();
      LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
      LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
      LocalDateTime startTime;
      int getAppointmentID = 0;
      LocalDateTime displayTime = null;
      boolean appointmentWithin15Min = false;

      ResourceBundle rb = ResourceBundle.getBundle("language.login", Locale.getDefault());

      String usernameInput = loginScreenUsername.getText();
      String passwordInput = loginScreenPassword.getText();
      int userId = userDao.validateUser(usernameInput, passwordInput);

      FileWriter fileWriter = new FileWriter("login_activity.txt", true);
      PrintWriter outputFile = new PrintWriter(fileWriter);

      if (userId > 0) {

        Connection connection = DBConnection.makeConnection();
        String selectQuery = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
        preparedStatement.setString(1, usernameInput);
        preparedStatement.setString(2, passwordInput);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
          User loggedInUser = new User();
          loggedInUser.setUserName(resultSet.getString("User_Name"));
          loggedInUser.setUserID(resultSet.getInt("User_ID"));
          DBConnection.logInUser = loggedInUser;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Main Form.fxml"));
        Parent root = loader.load();
        stage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        //log the successful login
        outputFile.print("user: " + usernameInput + " successfully logged in at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

        //check for upcoming appointments if user is validated
        for (Appointments appointment: getAllAppointments) {
          startTime = LocalDateTime.from(appointment.getStartTime());
          if ((startTime.isAfter(currentTimeMinus15Min) || startTime.isEqual(currentTimeMinus15Min)) && (startTime.isBefore(currentTimePlus15Min) || (startTime.isEqual(currentTimePlus15Min)))) {
            getAppointmentID = appointment.getAppointmentID();
            displayTime = startTime;
            appointmentWithin15Min = true;
          }
        }
        if (appointmentWithin15Min !=false) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment within 15 minutes: " + getAppointmentID + " and appointment start time of: " + displayTime);
          Optional<ButtonType> confirmation = alert.showAndWait();
          System.out.println("There is an appointment within 15 minutes");
        }

        else {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No upcoming appointments.");
          Optional<ButtonType> confirmation = alert.showAndWait();
          System.out.println("no upcoming appointments");
        }
      } else if (userId < 0) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(rb.getString("Error"));
        alert.setContentText(rb.getString("Incorrect"));
        alert.show();

        //log the failed login attempt
        outputFile.print("user: " + usernameInput + " failed login attempt at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

      }
      outputFile.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

  }

  /**
   * Exits the application.
   * @param event
   */

  public void cancel(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
  }

  /**
   * Initiliaze upon screen start. Get locale info and set text on fields.
   * @param url, rb
   */

  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    try
    {

      Locale locale = Locale.getDefault();
      Locale.setDefault(locale);

      ZoneId zone = ZoneId.systemDefault();

      //removed
      //loginScreenLocationField.setText(Locale.getDefault().getDisplayCountry());
      loginScreenLocationField.setText(String.valueOf(zone));

      rb = ResourceBundle.getBundle("language.login", Locale.getDefault());
      loginField.setText(rb.getString("Login"));
      usernameField.setText(rb.getString("username"));
      passwordField.setText(rb.getString("password"));
      loginButton.setText(rb.getString("Login"));
      cancelButtonField.setText(rb.getString("Exit"));
      locationText.setText(rb.getString("Location"));

    } catch(MissingResourceException e) {
      System.out.println("Resource file missing: " + e);
    } catch (Exception e)
    {
      System.out.println(e);
    }
  }

}
