package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../view/login.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 337, 307);
    stage.setTitle("Inventory Management");
    stage.setScene(scene);
    stage.show();
  }

  /**
   * The main method loads all database connection and launches the fmxl.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    DBConnection.makeConnection();
    launch(args);
    DBConnection.closeConnection();
  }

}
