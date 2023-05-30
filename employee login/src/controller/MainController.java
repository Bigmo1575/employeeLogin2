package controller;

import dao.AppointmentDao;
import dao.CountriesDao;
import dao.CustomerDao;
import dao.FirstLevelDivisionDao;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.DBConnection;
import model.Appointments;
import model.Countries;
import model.Customers;


import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  private AppointmentDao appointmentsDao = new AppointmentDao();
  CustomerDao customerDao = new CustomerDao();
  //These items are for the customers TableView
  @FXML private TableView<Customers> customersTableView;
  @FXML private TableColumn<Customers, String> customerID;
  @FXML private TableColumn<Customers, String> custNameColumn;
  @FXML private TableColumn<Customers, String> custAddressColumn;
  @FXML private TableColumn<Customers, String> custphoneNumberColumn;
  @FXML private TableColumn<Customers, String> custState;
  @FXML private TableColumn<Customers, String> custCountryColumn;
  //These items are for the appointment radio buttons
  @FXML private RadioButton weeklyRadioButton;
  @FXML private RadioButton monthlyRadioButton;
  @FXML private RadioButton allRadioButton;
  private ToggleGroup appmntToggleGroup;
  //These items are for the appointments TableView
  @FXML private TableView<Appointments> appmntTableView;
  @FXML private TableColumn<Appointments, String> appointmentID = new TableColumn<>("appointmentID");
  @FXML private TableColumn<Appointments, String> appointmentTitle;
  @FXML private TableColumn<Appointments, String> appointmentType;
  @FXML private TableColumn<Appointments, String> appointmentLocation;
  @FXML private TableColumn<Appointments, String> appointmentDescription;
  @FXML private TableColumn<Appointments, String> startTime;
  @FXML private TableColumn<Appointments, String> endTime;
  @FXML private TableColumn<Appointments, String> contactId;
  @FXML private TableColumn<Appointments, String> customerId;
  @FXML private TableColumn<Appointments, String> userId;
  @FXML private TableView<Appointments> allAppointmentsTable;

  //These items are for modifying customers and appointments
  public static Customers modifyCustomer;
  public static int modifyCustomerIndex;
  public static Appointments pickedAppointments;
  public static int modifyAppointmentIndex;
  //date formatter formats date into more readable format
  private final DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("M/d/yy h:mm a");

  /**
   * Changes scene to add customers view when add button is pushed
   */

  public void initialize(URL url, ResourceBundle resourceBundle) {
    Connection connection = DBConnection.makeConnection();

    try {
      ObservableList<Appointments> allAppointmentsList = appointmentsDao.getAllAppointments();

      appmntTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      appointmentID.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAppointmentID()).asObject().asString());
      appointmentTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentTitle()));
      appointmentDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentDescription()));
      appointmentLocation.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentLocation()));
      appointmentType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAppointmentType()));
      startTime.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getStartTime()).asString());
      endTime.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEndTime()).asString());
      customerId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCustomerID()).asObject().asString());
      contactId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getContactID()).asObject().asString());
      userId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getUserID()).asObject().asString());

      System.out.println("maincontroller, ln 98 " + allAppointmentsList);
      appmntTableView.setItems(allAppointmentsList);
      System.out.println("Main controller check getAllAppts " + allAppointmentsList);
      System.out.println("maincontroller, ln 100 " + appmntTableView.getItems());
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    try {
      ObservableList<CountriesDao> allCountries = CountriesDao.getCountries();
      ObservableList<String> countryNames = FXCollections.observableArrayList();

      ObservableList<FirstLevelDivisionDao> allFirstLevelDivisions = FirstLevelDivisionDao.getAllFirstLevelDivisions();
      ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();

      ObservableList<Customers> allCustomersList = CustomerDao.getAllCustomers(connection);

      customersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      customerID.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCustomerID()).asObject().asString());
      custNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
      custAddressColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerAddress()));
      custState.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerPostalCode()));
      custCountryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerPhone()));
      custCountryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDivisionName()));

      allCountries.stream().map(Countries::getCountryName).forEach(countryNames::add);
      allFirstLevelDivisions.forEach(FirstLevelDivision -> firstLevelDivisionAllNames.add(FirstLevelDivision.getDivisionName()));

      customersTableView.setItems(allCustomersList);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }



  public void getSelectedAppointment() {
    Appointments appointments = (Appointments) this.appmntTableView.getSelectionModel().getSelectedItem();
    this.appointmentID.setText(String.valueOf(Appointments.getAppointmentID()));
    this.appointmentTitle.setText(Appointments.getAppointmentTitle());
    this.appointmentType.setText(String.valueOf(appointments.getAppointmentType()));
    this.appointmentDescription.setText(String.valueOf(appointments.getAppointmentDescription()));
    this.appointmentLocation.setText(String.valueOf(appointments.getAppointmentLocation()));
    this.startTime.setText(String.valueOf(appointments.getStartTime()));
    this.endTime.setText(String.valueOf(appointments.getEndTime()));
    this.contactId.setText(String.valueOf(appointments.getContactID()));
    this.customerId.setText(String.valueOf(appointments.getCustomerID()));
    this.userId.setText(String.valueOf(appointments.getUserID()));
  }

  @FXML
  void toAddAppointment(ActionEvent actionEvent) throws IOException {
    Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/Add Appointment.fxml"));
    Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();


//      Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//      Scene scene = new Scene(root);
//      stage.setScene(scene);
//      stage.show();
   // Scene scene = new Scene(root, 900.0D, 800.0D);
  }
  @FXML
  void deleteRecord(ActionEvent event) throws SQLException {
    String deleteQuery = "DELETE FROM appointments WHERE Appointment_ID=?";
    int recordId = appmntTableView.getSelectionModel().getSelectedItem().getAppointmentID();
    try (Connection connection = DBConnection.makeConnection();
         PreparedStatement ps = connection.prepareStatement(deleteQuery)) {

        ps.setInt(1, recordId);
      int rowsAffected = ps.executeUpdate();

      if (rowsAffected > 0) {
        System.out.println("Record deleted successfully.");
      } else {
        System.out.println("No record found with the given ID.");
      }

    } catch (SQLException e) {
      System.out.println("Error deleting record: " + e.getMessage());
    }
  }

  @FXML
  public void updateAppt(ActionEvent event) throws IOException, SQLException {

    AppointmentDao getAppointments = null;
    int updateIndex;
    if(appmntTableView.getSelectionModel().getSelectedItem()==null){
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setHeaderText("Please select an appointment");
      alert.showAndWait();
    }else {
      pickedAppointments = appmntTableView.getSelectionModel().getSelectedItem();
      updateIndex =  getAppointments.getAllAppointments().indexOf(pickedAppointments);
      System.out.println("update appt to send " + pickedAppointments.getStartTime());
      Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/Modify Appointment.fxml"));
      Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
  }
  @FXML
  void appointmentAllSelected(ActionEvent event) throws SQLException {
    try {
      ObservableList<Appointments> allAppointmentsList = appointmentsDao.getAllAppointments();

      if (allAppointmentsList != null)
        for (model.Appointments appointment : allAppointmentsList) {
          appmntTableView.setItems(allAppointmentsList);
        }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  void appointmentMonthSelected(ActionEvent event) throws SQLException {
    try {
      ObservableList<Appointments> allAppointmentsList = appointmentsDao.getAllAppointments();
      ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();

      LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
      LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);


      if (allAppointmentsList != null)
        //IDE converted to forEach
        allAppointmentsList.forEach(appointment -> {
          if (appointment.getEndTime().isAfter(ChronoLocalDateTime.from(currentMonthStart)) && appointment.getEndTime().isBefore(currentMonthEnd)) {
            appointmentsMonth.add(appointment);
          }
          appmntTableView.setItems(appointmentsMonth);
        });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @FXML
  void appointmentWeekSelected(ActionEvent event) throws SQLException {
    try {

      ObservableList<Appointments> allAppointmentsList = appointmentsDao.getAllAppointments();
      ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();

      LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
      LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);

      if (allAppointmentsList != null)
        //IDE converted forEach
      {
        for (Appointments appointment : allAppointmentsList) {
          if (appointment.getEndTime().isAfter(ChronoLocalDateTime.from(weekStart)) && appointment.getEndTime().isBefore(ChronoLocalDateTime.from(weekEnd))) {
            appointmentsWeek.add(appointment);
          }
          appmntTableView.setItems(appointmentsWeek);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


//  public void toAddPart(ActionEvent actionEvent) throws IOException {
//    try {
//      Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/AddPartForm.fxml"));
//      Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//      Scene scene = new Scene(root);
//      stage.setScene(scene);
//      stage.show();
//    } catch (Exception var5) {
//      System.out.println(var5);
//    }
//
//  }

//  public void toModifyProduct(ActionEvent actionEvent) throws IOException {
//    FXMLLoader loader = new FXMLLoader();
//    loader.setLocation(this.getClass().getResource("/view/ModifyProductForm.fxml"));
//    Parent root = (Parent)loader.load();
//    Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//    Scene scene = new Scene(root, 900.0D, 800.0D);
//    System.out.println(this.productTable.getSelectionModel().getSelectedItem());
//    ModifyProductController modifyProductController = (ModifyProductController)loader.getController();
//    if (this.productTable.getSelectionModel().getSelectedItem() != null) {
//      modifyProductController.loadProduct((Product)this.productTable.getSelectionModel().getSelectedItem());
//      modifyProductController.loadProductAssociatedPart((Product)this.productTable.getSelectionModel().getSelectedItem());
//      stage.setScene(scene);
//      stage.show();
//    } else {
//      Alert alert = new Alert(AlertType.ERROR);
//      alert.setHeaderText("Please select a product before pressing modify");
//      alert.showAndWait();
//    }
//
//  }
//
//  public void toModifyPart(ActionEvent actionEvent) throws IOException {
//    FXMLLoader load = new FXMLLoader();
//    load.setLocation(this.getClass().getResource("/view/ModifyPartForm.fxml"));
//    Parent root = (Parent)load.load();
//    Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//    Scene scene = new Scene(root, 600.0D, 900.0D);
//    ModifyPartController partController = (ModifyPartController)load.getController();
//    if (this.partTable.getSelectionModel().getSelectedItem() != null) {
//      partController.loadPart((Part)this.partTable.getSelectionModel().getSelectedItem());
//      stage.setScene(scene);
//      stage.show();
//    } else {
//      Alert alert = new Alert(AlertType.ERROR);
//      alert.setHeaderText("Please select a part before pressing modify");
//      alert.showAndWait();
//    }
//
//  }

//  public void deletePart() {
//    if (this.partTable.getSelectionModel().getSelectedItem() != null) {
//      Dialog<String> dialog = new Dialog();
//      dialog.setHeaderText("Are you sure you want to delete this part?");
//      dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//      dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//      Optional pressed = dialog.showAndWait();
//      if (pressed.isPresent() && pressed.get() == ButtonType.OK) {
//        this.inventory.deletePart((Part)this.partTable.getSelectionModel().getSelectedItem());
//      }
//    } else {
//      Alert alert = new Alert(AlertType.ERROR);
//      alert.setHeaderText("Please select a part before pressing delete");
//      alert.showAndWait();
//    }
//
//  }
//
//  public void deleteProduct() {
//    Alert alert = new Alert(AlertType.ERROR);
//    if (this.productTable.getSelectionModel().getSelectedItem() != null) {
//      if (((Product)this.productTable.getSelectionModel().getSelectedItem()).getAllAssociatedParts().isEmpty()) {
//        Dialog<String> dialog = new Dialog();
//        dialog.setHeaderText("Are you sure you want to delete this product?");
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//        Optional pressed = dialog.showAndWait();
//        if (pressed.isPresent() && pressed.get() == ButtonType.OK) {
//          this.inventory.deleteProduct((Product)this.productTable.getSelectionModel().getSelectedItem());
//          this.productTable.setItems(this.inventory.getAllProducts());
//        }
//      } else {
//        alert.setHeaderText("product must have associated parts removed before deleting");
//        alert.showAndWait();
//      }
//    } else {
//      alert.setHeaderText("Please select a product before pressing delete");
//      alert.showAndWait();
//    }
//
//  }
//
//  public void toAddProduct(ActionEvent actionEvent) throws IOException {
//    Parent root = (Parent)FXMLLoader.load(this.getClass().getResource("/view/AddProductForm.fxml"));
//    Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
//    Scene scene = new Scene(root);
//    stage.setScene(scene);
//    stage.show();
//  }
}
