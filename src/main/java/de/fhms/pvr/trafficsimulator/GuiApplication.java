package de.fhms.pvr.trafficsimulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Dave on 27.05.2016.
 */
public class GuiApplication extends Application {

    private static final Logger LOG = LogManager.getLogger(GuiApplication.class);

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/traffic_simulation.fxml"));
        primaryStage.setTitle("Verkehrssimulation");
        primaryStage.setScene(new Scene(root, 800, 600));
        LOG.info("Initialisiere GUI");
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
