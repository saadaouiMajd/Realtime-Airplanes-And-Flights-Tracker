package main.java.com.application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.Flight;
import main.java.com.controllers.AddFlightController;
import main.java.com.controllers.AirplanesListController;
import main.java.com.controllers.SeeFlightsController;
import main.java.com.database.DButil;
import netscape.javascript.JSObject;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class Main extends Application {

    private static Parent root;

    private static Map<String,String> AirportsList = new HashMap<>();

    private Connection conn = DButil.getConnection();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        root = FXMLLoader.load(getClass().getResource("/fxml/AirplanesList.fxml"));
        Scene scene = new Scene(root,1265,587);
        primaryStage.setTitle("Airplanes");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        Task<Void> task = new Task<Void>()
        {
            @Override
            public Void call()
            {
                DButil DBu = DButil.getCurrentInstance();

                while (true)
                {
                    try
                    {
                        DBu.cleanFlights1(conn);

                        for (int i = 0; i < AirplanesListController.getListItems().size(); i++)
                        {
                            if (!AirplanesListController.getListItems().get(i).getStatus().equals(DBu.getAirplaneById(AirplanesListController.getListItems().get(i).getAirplaneNumber()).getStatus()))
                            {
                                AirplanesListController.getListItems().set(i, DBu.getAirplaneById(AirplanesListController.getListItems().get(i).getAirplaneNumber()));
                                AirplanesListController.setItemsButtonsEvents();
                                AirplanesListController.setItemsCheckBoxesEvents();
                            }
                        }

                        if (AirplanesListController.getFlightsStage() != null)
                        {
                            for (int i = 0; i < SeeFlightsController.getListItems().size(); i++)
                            {
                                if (AddFlightController.elapsedTime(SeeFlightsController.getListItems().get(i).getDepDate(), new Date()) > 0 && SeeFlightsController.getListItems().get(i).getCancelButton() != null)
                                {
                                    Flight flight = SeeFlightsController.getListItems().get(i);
                                    flight.setCancelButton(null);
                                    SeeFlightsController.getListItems().set(i, flight);
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }


    public static void main(String[] args)
    {
        AirportsList.put("Berlin Airport","52.52437,13.41053");AirportsList.put("London Airport","51.50853,-0.12574");
        AirportsList.put("Hamburg Airport","53.57532,10.01534");AirportsList.put("Paris Airport","48.85341,2.3488");
        AirportsList.put("Brussels Airport","50.85045,4.34878");AirportsList.put("Copenhagen Airport","55.67594,12.56553");

        launch(args);
    }

    public static Parent getRoot() {
        return root;
    }

    public static Map<String, String> getAirportsList() {
        return AirportsList;
    }

}
