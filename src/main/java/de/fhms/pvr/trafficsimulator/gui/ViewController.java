package de.fhms.pvr.trafficsimulator.gui;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;
import static org.fusesource.jansi.Ansi.ansi;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dave
 */
public class ViewController implements Initializable {
    private final int PIXEL_SIZE = 12;
    private final double PADDING = 1.0;
    private final double LINE_WIDTH = 1.0;
    private boolean isMoving = false;

    @FXML
    TabPane tabPaneRoot;

    @FXML
    Tab tabSimulation;

    @FXML
    Tab tabAnalysis;

    @FXML
    Canvas canvasStreetLayer;

    @FXML
    Canvas canvasCarLayer;

    @FXML
    TextField txtTracks;

    @FXML
    TextField txtIterations;

    @FXML
    TextField txtP;

    @FXML
    TextField txtP0;

    @FXML
    TextField txtRho;

    @FXML
    TextField txtSwitchProb;

    @FXML
    TextField txtSectionAmount;

    @FXML
    TextField txtFrom;

    @FXML
    TextField txtTo;

    @FXML
    Label lblLiveFrom;

    @FXML
    Label lblLiveTo;

    @FXML
    Canvas canvasTest;

    @FXML
    ScrollPane scrollPaneLeft;


    public void startSimulation(Event event) {
        if (validateInputFields()) {
            //Initialisierung der Parameter
            int trackAmount = Integer.parseInt(txtTracks.getText());
            int sectionAmount = Integer.parseInt(txtSectionAmount.getText());
            double p0 = Double.parseDouble(txtP0.getText());
            double p = Double.parseDouble(txtP.getText());
            double rho = Double.parseDouble(txtRho.getText());
            double c = Double.parseDouble(txtSwitchProb.getText());
            int iterations = Integer.parseInt(txtIterations.getText());
            int from = Integer.parseInt(txtFrom.getText());
            int to = Integer.parseInt(txtTo.getText());

            TrafficSimulator trafficSimulator = new TrafficSimulator(trackAmount, sectionAmount, rho, p0, p, c, 2);

            final AtomicInteger updateGui = new AtomicInteger(-1);

            DrawActualStateRunnable drawRunable = new DrawActualStateRunnable(from, to,
                    trafficSimulator.getStreet(),
                    trackAmount, canvasCarLayer.getGraphicsContext2D(), updateGui,
                    LINE_WIDTH, PADDING, PIXEL_SIZE, canvasTest.getGraphicsContext2D());


            SimulateTask simulateTask = new SimulateTask(iterations, trafficSimulator);

            simulateTask.intProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    //System.out.println("Iteration:" + newValue);
                    if (updateGui.getAndSet(-1) == -1) {
                        drawRunable.setIteration(newValue.intValue());
                        Platform.runLater(drawRunable);
                    }
                }
            });


            initSimulationTab(trackAmount, from, to, iterations);

            Thread workerThread = new Thread(simulateTask);
            workerThread.start();
        }
    }

    private void initSimulationTab(int trackAmount, int from, int to, int iterations) {
        drawStreet(canvasStreetLayer.getGraphicsContext2D(), trackAmount, LINE_WIDTH, from, to);
        scrollPaneLeft.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        canvasTest.setHeight(iterations);
        scrollPaneLeft.setContent(canvasTest);
        lblLiveFrom.setText(String.valueOf(from));
        lblLiveTo.setText(String.valueOf(to));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultValues();
    }

    public void stopSimulation(Event event) {
        this.isMoving = false;
    }

    /**
     * Zurücksetzen der Standardwerte für die Input-Textfelder
     *
     * @param event
     */
    public void resetToDefault(Event event) {
        this.initDefaultValues();
    }

    /**
     * Initialisierung von Standardwerten für die Parameter beim Starten der Applikation
     */
    private void initDefaultValues() {
        txtTracks.setText("2");
        txtSectionAmount.setText("1000");
        txtSwitchProb.setText("0.5");
        txtRho.setText("0.4");
        txtP.setText("0.2");
        txtP0.setText("0.2");
        txtIterations.setText("1000");
        txtFrom.setText("0");
        txtTo.setText("100");
    }

    /**
     * Setzt die beiden Canvas zurück
     */
    private void resetCanvas() {
        canvasCarLayer.getGraphicsContext2D().clearRect(0, 0, canvasCarLayer.getWidth(), canvasCarLayer.getHeight());
        canvasStreetLayer.getGraphicsContext2D().clearRect(0, 0, canvasStreetLayer.getWidth(), canvasStreetLayer.getHeight());
        canvasTest.getGraphicsContext2D().clearRect(0, 0, canvasTest.getWidth(), canvasTest.getHeight());
    }

    private class SimulateTask<Integer> extends Task<Integer> {
        private IntegerProperty intProperty;
        private final int iterations;
        private final TrafficSimulator simulator;

        public SimulateTask(int iterations, TrafficSimulator simulator) {
            this.iterations = iterations;
            this.simulator = simulator;
            this.intProperty = new SimpleIntegerProperty(this, "int", 0);
        }

        public IntegerProperty intProperty() {
            return intProperty;
        }

        @Override
        protected Integer call() throws Exception {
            for (int i = 0; i < iterations; i++) {
                simulator.iterate();
                intProperty.set(intProperty.get() + 1);
            }
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            System.out.println(ansi().reset());
            System.out.println("Beschleunigen:\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(ACCELERATION) + "ms");
            System.out.println("Bremsen:\t\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(DECELERATION) + "ms");
            System.out.println("Trödeln:\t\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(DAWDLING) + "ms");
            System.out.println("Fortbewegen:\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(MOVEMENT) + "ms");
            System.out.println("\r\nGesamt:\t\t\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(ITERATION) + "ms");
        }
    }

    private void drawStreet(GraphicsContext gc, int trackAmount, double lineWidth, int from, int to) {
        //Canvas zurücksetzen
        this.resetCanvas();
        //Straßenfläche
        gc.setFill(Color.DARKGRAY);
        double streetHeight = PIXEL_SIZE * trackAmount + 2 * lineWidth + 2 * PADDING + ((trackAmount - 1) * lineWidth + PADDING) + 1;
        double streetWidth = (to - from + PADDING) * PIXEL_SIZE;

        gc.fillRect(0, 0, streetWidth, streetHeight);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(lineWidth);
        //Oberer Randstreifen
        gc.strokeLine(0, 1, streetWidth, 1);
        gc.strokeLine(0, 1, streetWidth, 1);
        //Unterer Randstreifen
        gc.strokeLine(0, streetHeight, streetWidth, streetHeight);
        gc.strokeLine(0, streetHeight, streetWidth, streetHeight);

        //Trennstreifen
        double y = lineWidth / 4 + PADDING;
        for (int i = 0; i < trackAmount - 1; i++) {
            for (int j = 0; j <= streetWidth; j += 6) {
                gc.strokeLine(j, y + PIXEL_SIZE + PADDING + lineWidth, j - 4, y + PIXEL_SIZE + PADDING + lineWidth);
            }
            y += PIXEL_SIZE + lineWidth + PADDING;
        }

    }


    /**
     * Prüfung ob input ein Int oder Double ist
     *
     * @param str
     * @return boolean
     */
    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * Validierung der Input-Text-Felder
     *
     * @return
     */
    private boolean validateInputFields() {
        if (isNumeric(txtTracks.getText())
                && isNumeric(txtIterations.getText())
                && isNumeric(txtP.getText())
                && isNumeric(txtP0.getText())
                && isNumeric(txtRho.getText())
                && isNumeric(txtSwitchProb.getText())
                && isNumeric(txtSectionAmount.getText())
                && isNumeric(txtFrom.getText())
                && isNumeric(txtTo.getText())) {
            return true;
        }
        return false;
    }
}



