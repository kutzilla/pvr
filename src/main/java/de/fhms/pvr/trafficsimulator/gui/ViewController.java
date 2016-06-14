package de.fhms.pvr.trafficsimulator.gui;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.util.StreetConfigurationParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;

/**
 * @author Dave
 */
public class ViewController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ViewController.class);

    private final int PIXEL_SIZE = 12;
    private final double PADDING = 1.0;
    private final double LINE_WIDTH = 1.0;
    private volatile boolean isMoving = false;

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
    Canvas canvasFlowView;

    @FXML
    Canvas canvasStateView;

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
    ScrollPane scrollPaneStateView;

    @FXML
    ScrollPane scrollPaneSectionView;

    @FXML
    ScrollPane scrollPaneFlowView;

    @FXML
    TextField txtWorkerAmount;

    @FXML
    TextField txtTaskAmount;

    @FXML
    Button btnStartSimulation;

    @FXML
    Button btnChooseConfigurationFile;

    @FXML
    Label lblFileName;

    @FXML
    RadioButton rbtnRandomConfig;

    @FXML
    RadioButton rbtnExistingConfig;

    @FXML
    Label lblWorker;

    @FXML
    Label lblTracks;

    @FXML
    Label lblSections;

    @FXML
    Label lblTotalSections;

    @FXML
    Label lblPhi;

    @FXML
    Label lblSigma;

    @FXML
    Label lblKappa;

    @FXML
    Label lblVehicles;

    @FXML
    Label lblIterations;

    private File configurationFile;



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
            int taskAmount = Integer.parseInt(txtTaskAmount.getText());
            int workerAmount = Integer.parseInt(txtWorkerAmount.getText());

            TrafficSimulator trafficSimulator = null;
            TrafficSimulatorBuilder builder = null;
            if (rbtnExistingConfig.isSelected() && configurationFile != null) {
                try {
                    builder = new TrafficSimulatorBuilder(StreetConfigurationParser
                            .parseStreetConfigurationFrom(configurationFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                builder = new TrafficSimulatorBuilder(trackAmount, sectionAmount, rho);
            }
            trafficSimulator = builder.withSwitchProbability(c).withSlowDawdleProbability(p0)
                    .withFastDawdleProbability(p).withWorkerAmount(workerAmount).withTaskAmount(taskAmount).build();

            SimulateTask simulateTask = initSimulateTask(iterations, trafficSimulator, from, to);

            initSimulationTab(trackAmount, from, to, iterations);

            Thread workerThread = new Thread(simulateTask);
            this.isMoving = true;
            workerThread.start();
            this.enableOrDeactivateStart();
        }
    }

    private SimulateTask initSimulateTask(int iterations, TrafficSimulator simulator, int from, int to) {
        SimulateTask simulateTask = new SimulateTask(iterations, simulator, from, to);
        return simulateTask;
    }

    private void initSimulationTab(int trackAmount, int from, int to, int iterations) {
        drawStreet(canvasStreetLayer.getGraphicsContext2D(), trackAmount, LINE_WIDTH, from, to);

        //Abschnittsansicht
        Group canvasGroup = new Group(canvasStreetLayer, canvasCarLayer);
        scrollPaneSectionView.setContent(canvasGroup);
        //Statusdarstellung
        scrollPaneStateView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        canvasStateView.setHeight(iterations);
        canvasStateView.setWidth(to - from);
        scrollPaneStateView.setContent(canvasStateView);
        //Flussdarstellung
        scrollPaneFlowView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        canvasFlowView.setHeight(iterations);
        canvasFlowView.setWidth(to - from);
        scrollPaneFlowView.setContent(canvasFlowView);


        lblLiveFrom.setText(String.valueOf(from));
        lblLiveTo.setText(String.valueOf(to));
    }

    private void enableOrDeactivateStart() {
        if (btnStartSimulation.isDisable()) {
            btnStartSimulation.setDisable(false);
            return;
        }
        btnStartSimulation.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPaneSectionView.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneSectionView.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneStateView.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneStateView.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneFlowView.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPaneFlowView.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
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

    public void chooseConfigurationFile(Event event) {
        LOG.debug("Dateiauswahl angeklickt");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration File (*.csv)","*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            LOG.info(file + " wurde ausgewählt");
            this.configurationFile = file;
            this.lblFileName.setText(file.getName());
        }
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
        txtWorkerAmount.setText("1");
        txtTaskAmount.setText("1");
    }

    /**
     * Setzt die beiden Canvas zurück
     */
    private void resetCanvas() {
        canvasCarLayer.getGraphicsContext2D().clearRect(0, 0, canvasCarLayer.getWidth(), canvasCarLayer.getHeight());
        canvasStreetLayer.getGraphicsContext2D().clearRect(0, 0, canvasStreetLayer.getWidth(), canvasStreetLayer.getHeight());
        canvasStateView.getGraphicsContext2D().clearRect(0, 0, canvasStateView.getWidth(), canvasStateView.getHeight());
        canvasFlowView.getGraphicsContext2D().clearRect(0, 0, canvasStateView.getWidth(), canvasStateView.getHeight());
    }


    private void drawStreet(GraphicsContext gc, int trackAmount, double lineWidth, int from, int to) {
        //Canvas zurücksetzen
        this.resetCanvas();
        //Straßenfläche
        gc.setFill(Color.DARKGRAY);
        double streetHeight = PIXEL_SIZE * trackAmount + 2 * lineWidth + 2 * PADDING + ((trackAmount - 1) * lineWidth + PADDING) + 1;
        double streetWidth = (to - from + PADDING) * PIXEL_SIZE;

        this.canvasStreetLayer.setWidth(streetWidth);
        this.canvasCarLayer.setWidth(streetWidth);
        this.canvasStreetLayer.setHeight(streetHeight);
        this.canvasCarLayer.setHeight(streetHeight);

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
                && isNumeric(txtTo.getText())
                && isNumeric(txtWorkerAmount.getText())
                && isNumeric(txtTaskAmount.getText())) {
            return true;
        }
        return false;
    }

    private class SimulateTask<Void> extends Task<Void> {
        private final TrafficSimulator simulator;
        private final int iterations;
        private final int from;
        private final int to;

        public SimulateTask(int iterations, TrafficSimulator simulator, int from, int to) {
            this.simulator = simulator;
            this.iterations = iterations;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Void call() throws Exception {
            for (int i = 0; i < iterations; i++) {
                if (isMoving) {
                    simulator.iterate();
                    CurrentGenerationDrawer drawRunable = new CurrentGenerationDrawer(from, to,
                            simulator.getStreet(),
                            simulator.getStreet().length,
                            LINE_WIDTH, PADDING, PIXEL_SIZE,
                            canvasCarLayer.getGraphicsContext2D(),
                            canvasStateView.getGraphicsContext2D(),
                            canvasFlowView.getGraphicsContext2D());
                    drawRunable.setIteration(i);
                    FutureTask<Boolean> drawTask = new FutureTask<>(drawRunable);
                    Platform.runLater(drawTask);
                    try {
                        drawTask.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            return null;
        }


        @Override
        protected void succeeded() {
            super.succeeded();
            enableOrDeactivateStart();
            LOG.info("Spur Wechseln:\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(TRACK_SWITCHING) + "ms");
            LOG.info("Aktionen:\t\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(DRIVE_ACTION) + "ms");
            LOG.info("Fortbewegen:\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(MOVEMENT) + "ms");
            LOG.info("Gesamt:\t\t" + simulator.getTimeMeasureController().getMeasuredTimeFor(ITERATION) + "ms");
            simulator.shutdown();
            TimeMeasureController timeMeasureController = simulator.getTimeMeasureController();
            lblKappa.setText(String.valueOf(timeMeasureController.getMeasuredTimeFor(KAPPA)));
            lblPhi.setText(String.valueOf(timeMeasureController.getMeasuredTimeFor(PHI)));
            lblSigma.setText(String.valueOf(timeMeasureController.getMeasuredTimeFor(SIGMA)));
            lblTracks.setText(String.valueOf(simulator.getTrackAmount()));
            lblSections.setText(String.valueOf(simulator.getSectionAmount()));
            lblTotalSections.setText(String.valueOf(simulator.getTotalSectionAmount()));
            lblVehicles.setText(String.valueOf(simulator.getVehicleAmount()));
            lblWorker.setText(String.valueOf(simulator.getWorkerAmount()));
            lblIterations.setText(String.valueOf(this.iterations));
        }
    }
}



