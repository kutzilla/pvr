package de.fhms.pvr.vekehrssimulation.mehrspurig.parallel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Matthias on 04.05.16.
 */
public class TrafficSimulationApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Verkehrssimulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
