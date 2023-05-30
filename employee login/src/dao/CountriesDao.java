package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.DBConnection;
import model.Countries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesDao extends Countries {

  public CountriesDao(int countryID, String countryName) {
    super(countryID, countryName);
  }

  /**
   * ObservableList that queries Country_ID and Country from the countries database table.
   * @throws SQLException
   * @return countriesObservableList
   */
  public static ObservableList<CountriesDao> getCountries() throws SQLException {
    ObservableList<CountriesDao> countriesObservableList = FXCollections.observableArrayList();
    String sql = "SELECT Country_ID, Country from countries";
    PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      int countryID = rs.getInt("Country_ID");
      String countryName = rs.getString("Country");
      CountriesDao countriesDao = new CountriesDao(countryID, countryName);
      countriesObservableList.add(countriesDao);
    }
    return countriesObservableList;
  }

}
