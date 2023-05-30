package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointments {


  private static int appointmentID;
  private static String appointmentTitle;
  private static String appointmentDescription;
  private static String appointmentLocation;
  private static String appointmentType;
  private static LocalDateTime startTime;
  private static LocalDateTime endTime;
  public static int contactID;
  public static int customerID;
  private LocalDate addApptDate;
  private LocalDate addApptEndDate;
  private String appmtCust;

  public static int userID;

  public Appointments() {

  }

  public Appointments(int appointmentID, String appointmentTitle, String appointmentDescription, String appointmentLocation, String appointmentType, LocalDateTime startTime, LocalDateTime endTime, int contactID, int customerID, int userID) {
    this.appointmentID = appointmentID;
    this.appointmentTitle = appointmentTitle;
    this.appointmentDescription = appointmentDescription;
    this.appointmentLocation = appointmentLocation;
    this.appointmentType = appointmentType;
    this.startTime = startTime;
    this.endTime = endTime;
    this.contactID = contactID;
    this.customerID = customerID;
    this.userID = userID;
  }

  public LocalDate getAddApptDate() {
    return addApptDate;
  }

  public void setAddApptDate(LocalDate addApptDate) {
    this.addApptDate = addApptDate;
  }

  public LocalDate getAddApptEndDate() {
    return addApptEndDate;
  }

  public void setAddApptEndDate(LocalDate addApptEndDate) {
    this.addApptEndDate = addApptEndDate;
  }

  public static int getAppointmentID() {
    return appointmentID;
  }

  public void setAppointmentID(int appointmentID) {
    this.appointmentID = appointmentID;
  }

  public static String getAppointmentTitle() {
    return appointmentTitle;
  }

  public void setAppointmentTitle(String appointmentTitle) {
    this.appointmentTitle = appointmentTitle;
  }

  public String getAppointmentDescription() {
    return appointmentDescription;
  }

  public void setAppointmentDescription(String appointmentDescription) {
    this.appointmentDescription = appointmentDescription;
  }

  public static String getAppointmentLocation() {
    return appointmentLocation;
  }

  public void setAppointmentLocation(String appointmentLocation) {
    this.appointmentLocation = appointmentLocation;
  }

  public static String getAppointmentType() {
    return appointmentType;
  }

  public void setAppointmentType(String appointmentType) {
    this.appointmentType = appointmentType;
  }

  public static LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public static LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public static int getContactID() {
    return contactID;
  }

  public void setContactID(int contactID) {
    this.contactID = contactID;
  }

  public static int getCustomerID() {
    return customerID;
  }

  public void setCustomerID(int customerID) {
    this.customerID = customerID;
  }

  public static int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getAppmtCust() {
    return appmtCust;
  }

  public void setAppmtCust(String appmtCust) {
    this.appmtCust = appmtCust;
  }
}
