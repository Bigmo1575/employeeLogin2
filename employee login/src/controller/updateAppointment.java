package controller;

import dao.AppointmentDao;
import dao.ContactsDao;
import dao.CustomerDao;
import dao.userDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import model.Contacts;
import model.Customers;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static controller.MainController.pickedAppointments;
import static main.timeUtil.convertTimeDateUTC;

public class updateAppointment {


  @FXML
  private TextField addApptCustomerID;
  @FXML
  private ComboBox<Integer> addApptmentUserID;
  @FXML
  private TextField addAppotDescription;
  @FXML
  private DatePicker addApptEndDate;
  @FXML
  private ComboBox<String> addApptEndTime;
  @FXML
  private TextField addApptID;
  @FXML
  private TextField addApptLocation;
  @FXML
  private DatePicker addApptDate;
  @FXML
  private TextField addApptType;
  @FXML
  private ComboBox<String> addApptStartTime;
  @FXML
  private TextField addApptTitle;
  @FXML
  private ComboBox<String> addApptContact;
  @FXML
  private ComboBox<Integer> addApptUser;

  @FXML
  private TableView<Customers> customersTableView;
  @FXML
  private TableColumn<Customers, String> custNameColumn;
  @FXML
  private TableColumn<Customers, String> custphoneNumberColumn;

  private final ZoneId sysid = ZoneId.systemDefault();
  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm");
  private final ObservableList<String> startTimes = FXCollections.observableArrayList();
  private final ObservableList<String> endTimes = FXCollections.observableArrayList();


  public void initialize() throws SQLException{

    ObservableList<User> userIDName = userDao.getUsers();
    ObservableList<Contacts> contactsObservableList = ContactsDao.getAllContacts();
    ObservableList<String> allContactsName = FXCollections.observableArrayList();

    ObservableList<Integer> allUsers = userIDName.stream()
            .map(User::getUserID)
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

    contactsObservableList.forEach(contacts -> allContactsName.add(contacts.getContactName()));

    ObservableList<String> apptTimes = FXCollections.observableArrayList();

    LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
    LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

    //if statement fixed issue with infinite loop
    if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
      while (firstAppointment.isBefore(lastAppointment)) {
        apptTimes.add(String.valueOf(firstAppointment));
        firstAppointment = firstAppointment.plusMinutes(15);
      }
    }
    addApptStartTime.setItems(apptTimes);
    addApptEndTime.setItems(apptTimes);
    addApptContact.setItems(allContactsName);
    addApptUser.setItems(allUsers);

   // System.out.println("updateAppointment controller init" +pickedAppointments.toString());
    Connection connection = DBConnection.getConnection();

    System.out.println("start date " + pickedAppointments.getStartTime().toLocalDate());

    CustomerDao customerDao = new CustomerDao();
    addApptTitle.setText(pickedAppointments.getAppointmentTitle());
    addApptType.setText(pickedAppointments.getAppointmentType());
    addAppotDescription.setText(pickedAppointments.getAppointmentDescription());
    addApptLocation.setText(pickedAppointments.getAppointmentLocation());
    addApptCustomerID.setText(String.valueOf(pickedAppointments.getCustomerID()));

    ContactsDao contactsDao = new ContactsDao();

    //TODO match contact id with name
    addApptContact.setValue(String.valueOf(pickedAppointments.getContactID()));
    addApptUser.setValue(pickedAppointments.getUserID());
    addApptDate.setValue(pickedAppointments.getStartTime().toLocalDate());
    addApptEndDate.setValue(pickedAppointments.getEndTime().toLocalDate());
    addApptStartTime.getSelectionModel().select(String.valueOf(pickedAppointments.getStartTime().toLocalTime()));
    addApptEndTime.getSelectionModel().select(String.valueOf(pickedAppointments.getEndTime().toLocalTime()));

  }

  @FXML
  public void cancelToMain(ActionEvent event) throws IOException {

    Parent root = FXMLLoader.load(getClass().getResource("../view/Main Form.fxml"));
    Scene scene = new Scene(root);
    Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
    MainScreenReturn.setScene(scene);
    MainScreenReturn.show();
  }

  public void checkAppt() throws Exception {
    AppointmentDao appointmentDao = new AppointmentDao();
    try {
      Connection connection = DBConnection.makeConnection();
      if (!addApptTitle.getText().isEmpty() && !addAppotDescription.getText().isEmpty() && !addApptLocation.getText().isEmpty() && !addApptType.getText().isEmpty() && addApptDate.getValue() != null && addApptEndDate.getValue() != null && !addApptStartTime.getValue().isEmpty() && !addApptEndTime.getValue().isEmpty() && !addApptCustomerID.getText().isEmpty()) {
        ObservableList<Integer> storeCustIDs = FXCollections.observableArrayList();
        ObservableList<User> getUsers = userDao.getUsers();
        ObservableList<Customers> getAllCust = CustomerDao.getAllCustomers(connection);
        ObservableList<Appointments> getAppointments = appointmentDao.getAllAppointments();
        ObservableList<Integer> storeUserID = FXCollections.observableArrayList();

        getAllCust.stream().map(Customers::getCustomerID).forEach(storeCustIDs::add);
        getUsers.stream().map(User::getUserID).forEach(storeUserID::add);
        LocalDate localDateStart = addApptDate.getValue();
        LocalDate localDateEnd = addApptEndDate.getValue();

        DateTimeFormatter minHrFormat = DateTimeFormatter.ofPattern("HH:mm");
        String apptStartTime = addApptStartTime.getValue();
        System.out.println("updateAppoint check appointment updated start time " + apptStartTime.toString());
        String apptStartDate = addApptDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String endDate = addApptEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endTime = addApptEndTime.getValue();

        System.out.println("thisDate + thisStart " + apptStartDate + " " + apptStartTime + ":00");
        String startUTC = convertTimeDateUTC(apptStartDate + " " + apptStartTime + ":00");
        String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");
        System.out.println("startUTC " + startUTC);
        LocalTime LocalTimeEnd = LocalTime.parse(addApptEndTime.getValue(), minHrFormat);
        LocalTime localTimeStart = LocalTime.parse(addApptStartTime.getValue(), minHrFormat);


        LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
        LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

        ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
        ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

        ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

        LocalTime startTimeCheck = convertStartEST.toLocalTime();
        LocalTime endTimeCheck = convertEndEST.toLocalTime();

        DayOfWeek startDayCheck = convertStartEST.toLocalDate().getDayOfWeek();
        DayOfWeek endDayCheck = convertEndEST.toLocalDate().getDayOfWeek();

        int startApptCheckInt = startDayCheck.getValue();
        int endApptDayCheckInt = endDayCheck.getValue();

        int weekStart = DayOfWeek.MONDAY.getValue();
        int weekEnd = DayOfWeek.FRIDAY.getValue();

        LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
        LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);

        if (startApptCheckInt < weekStart || startApptCheckInt > weekEnd || endApptDayCheckInt < weekStart || endApptDayCheckInt > weekEnd) {
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is outside of business operations (Monday-Friday)");
          Optional<ButtonType> confirmation = alert.showAndWait();
          System.out.println("day is outside of normal business hours");
          return;
        }

        if (startTimeCheck.isBefore(estBusinessStart) || startTimeCheck.isAfter(estBusinessEnd) || endTimeCheck.isBefore(estBusinessStart) || endTimeCheck.isAfter(estBusinessEnd)) {
          System.out.println("time is outside of normal business hours");
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of normal business hours (8am-10pm EST): " + startTimeCheck + " - " + endTimeCheck + " EST");
          Optional<ButtonType> confirmation = alert.showAndWait();
          return;
        }

        int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
        int customerID = Integer.parseInt(addApptCustomerID.getText());

        if (dateTimeStart.isAfter(dateTimeEnd)) {
          System.out.println("Appointment has start time after end time");
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has start time after end time");
          Optional<ButtonType> confirmation = alert.showAndWait();
          return;
        }

        if (dateTimeStart.isEqual(dateTimeEnd)) {
          System.out.println("Appointment has same start and end time");
          Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has same start and end time");
          Optional<ButtonType> confirmation = alert.showAndWait();
          return;
        }
        for (Appointments appointment : getAppointments) {
          LocalDateTime checkStart = appointment.getStartTime();
          LocalDateTime checkEnd = appointment.getEndTime();

          //"outer verify" meaning check to see if an appointment exists between start and end.
          if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                  (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
            Optional<ButtonType> confirmation = alert.showAndWait();
            System.out.println("Appointment overlaps with existing appointment.");
            return;
          }

          if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
//                            (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                  (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time overlaps with existing appointment.");
            Optional<ButtonType> confirmation = alert.showAndWait();
            System.out.println("Start time overlaps with existing appointment.");
            return;
          }


          if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeEnd.isEqual(checkStart) || dateTimeEnd.isAfter(checkStart)) &&
//                            (dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd)))
                  (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "End time overlaps with existing appointment.");
            Optional<ButtonType> confirmation = alert.showAndWait();
            System.out.println("End time overlaps with existing appointment.");
            return;
          }
        }

       // String insertQuery = "UPDATE appointments set Appointment_ID = " + addApptID.getText() + ", Title = " + addApptTitle.getText() +", Description = " + addAppotDescription.getText() +", Location = " + addApptLocation.getText() +", Type = " + addApptType.getText() +", Start = "+ Timestamp.valueOf(apptStartDate + " " + apptStartTime + ":00" )+ ", End = " + Timestamp.valueOf(endDate + " " + endTime + ":00") + ", Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String loggedin = DBConnection.logInUser.getUserName();
       // String insertQuery = "UPDATE appointment SET customerId = ?, title = ?, type = ?, start = ?, end = ?, lastUpdate = ?, lastupdateby = ? \nWHERE appointmentId = ?" ;
        String insertQuery = "UPDATE appointments SET Customer_ID = " + addApptCustomerID.getText() + ", Title = '" + addApptTitle.getText() + "', " +"Type = '" + addApptType.getText() + "', Start = '"+ Timestamp.valueOf(apptStartDate + " " + apptStartTime + ":00" ) +"' , End = '"+ Timestamp.valueOf(endDate + " " + endTime + ":00") +"', Last_Update = NOW(), " +"Last_Updated_By = '" + DBConnection.logInUser.getUserName() + "'\nWHERE Appointment_ID = " + pickedAppointments.getAppointmentID() + "";
        DBConnection.makePreparedStatement(insertQuery, DBConnection.getConnection());
        System.out.println("appointment id" + pickedAppointments.getAppointmentID());
        PreparedStatement ps = DBConnection.getPreparedStatement();

        System.out.println("ps " + ps);
        ps.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (NumberFormatException exception) {
      exception.printStackTrace();
    }
  }
  @FXML
  void saveAppt(ActionEvent event) throws Exception {
    try{

      // removed this logic to its own function to make the save event function easier to manage and read.
      checkAppt();

      Parent root = FXMLLoader.load(getClass().getResource("../view/Main Form.fxml"));
      Scene scene = new Scene(root);
      Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
      MainScreenReturn.setScene(scene);
      MainScreenReturn.show();

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

  }







}
