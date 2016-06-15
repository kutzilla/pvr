package de.fhms.pvr.trafficsimulator.gui;


import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.TrafficSimulator.TrafficSimulatorBuilder;
import de.fhms.pvr.trafficsimulator.system.Vehicle;
import de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureController;
import de.fhms.pvr.trafficsimulator.system.util.StreetConfigurationParser;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;

/**
 *
 */
public class ViewController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(ViewController.class);

    private final int PIXEL_SIZE = 12;
    private final double PADDING = 1.0;
    private final double LINE_WIDTH = 1.0;
    private volatile boolean isMoving = false;

    private int trackAmount;
    private int sectionAmount;
    private double p0;
    private double p;
    private double c;
    private int iterations;
    private int from;
    private int to;
    private int taskAmount;
    private int workerAmount;
    private TrafficSimulator trafficSimulator;
    private TrafficSimulatorBuilder builder;

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
    TextField txtRelativeRho;

    @FXML
    TextField txtAbsoluteRho;

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
    RadioButton rbtnAbsoluteRho;

    @FXML
    RadioButton rbtnRelativeRho;

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

    @FXML
    Button btnSaveConfiguration;

    private File configurationFile;


    public void startSimulation(Event event) {
        if (this.initSimulation()) {
            SimulateTask simulateTask = initSimulateTask(iterations, trafficSimulator, from, to);
            Thread workerThread = new Thread(simulateTask);
            this.isMoving = true;
            workerThread.start();
            this.enableOrDeactivateStart();
        }
    }

    /**
     * Initialisierung der Parameter sowie des Trafficsimulators
     */
    private boolean initSimulation() {
        if (validateInputFields()) {
            //Initialisierung der Parameter
            trackAmount = Integer.parseInt(txtTracks.getText());
            sectionAmount = Integer.parseInt(txtSectionAmount.getText());
            p0 = Double.parseDouble(txtP0.getText());
            p = Double.parseDouble(txtP.getText());
            c = Double.parseDouble(txtSwitchProb.getText());
            iterations = Integer.parseInt(txtIterations.getText());
            from = Integer.parseInt(txtFrom.getText());
            to = Integer.parseInt(txtTo.getText());
            taskAmount = Integer.parseInt(txtTaskAmount.getText());
            workerAmount = Integer.parseInt(txtWorkerAmount.getText());

            trafficSimulator = null;
            builder = null;
            if (rbtnExistingConfig.isSelected() && configurationFile != null) {
                try {
                    builder = new TrafficSimulatorBuilder(StreetConfigurationParser
                            .parseStreetConfigurationFrom(configurationFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                builder = new TrafficSimulatorBuilder(trackAmount, sectionAmount);
                LOG.info("NEW");
            }


            if (rbtnRelativeRho.isSelected()) {
                double rho = Double.parseDouble(txtRelativeRho.getText());
                trafficSimulator = builder.withSwitchProbability(c).withSlowDawdleProbability(p0).withRelativeVehicleDensity(rho)
                        .withFastDawdleProbability(p).withWorkerAmount(workerAmount).withTaskAmount(taskAmount).build();
            } else {
                int vehicleAmount = Integer.parseInt(txtAbsoluteRho.getText());
                trafficSimulator = builder.withSwitchProbability(c).withSlowDawdleProbability(p0).withAbsoluteVehicleDensity(vehicleAmount)
                        .withFastDawdleProbability(p).withWorkerAmount(workerAmount).withTaskAmount(taskAmount).build();
            }


            initSimulationTab(trackAmount, from, to, iterations);
            return true;
        }
        return false;
    }

    private SimulateTask initSimulateTask(int iterations, TrafficSimulator simulator, int from, int to) {
        SimulateTask simulateTask = new SimulateTask(iterations, simulator, from, to);
        return simulateTask;
    }

    /**
     * Initialisierung des Simulationstabs
     *
     * @param trackAmount
     * @param from
     * @param to
     * @param iterations
     */
    private void initSimulationTab(int trackAmount, int from, int to, int iterations) {
        drawStreet(canvasStreetLayer.getGraphicsContext2D(), trackAmount, LINE_WIDTH, from, to);

        //Abschnittsansicht
        Group canvasGroup = new Group(canvasStreetLayer, canvasCarLayer);
        scrollPaneSectionView.setContent(canvasGroup);
        //Statusdarstellung
        scrollPaneStateView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //Vermeidung von Nullpointern bei zu großem Wert für die Höhe der Canvas'
        int height = 4000;

        canvasStateView.setHeight(height);
        canvasStateView.setWidth(to - from);
        scrollPaneStateView.setContent(canvasStateView);
        //Flussdarstellung
        scrollPaneFlowView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        canvasFlowView.setHeight(height);
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

        //Listener für die Radio-Buttons zur Wahl der Fahrzeugdichte
        final ToggleGroup vehicleDensityToggleGroup = rbtnRelativeRho.getToggleGroup();
        vehicleDensityToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (vehicleDensityToggleGroup.getSelectedToggle() != null) {
                    if (newValue.selectedProperty().getBean().equals(rbtnRelativeRho)) {
                        txtRelativeRho.setDisable(false);
                        txtAbsoluteRho.setDisable(true);
                    } else {
                        txtAbsoluteRho.setDisable(false);
                        txtRelativeRho.setDisable(true);
                    }

                }
            }
        });

        //Listener für die Radio-Buttons zur Wahl der Straßenkonfiguration
        final ToggleGroup configToggleGroup = rbtnExistingConfig.getToggleGroup();
        configToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (configToggleGroup.getSelectedToggle() != null) {
                if (newValue.selectedProperty().getBean().equals(rbtnExistingConfig)) {
                    txtAbsoluteRho.setDisable(true);
                    txtRelativeRho.setDisable(true);
                    txtTracks.setDisable(true);
                    txtSectionAmount.setDisable(true);
                    rbtnAbsoluteRho.setDisable(true);
                    rbtnRelativeRho.setDisable(true);
                } else {
                    txtAbsoluteRho.setDisable(false);
                    txtRelativeRho.setDisable(false);
                    txtTracks.setDisable(false);
                    txtSectionAmount.setDisable(false);
                    rbtnAbsoluteRho.setDisable(false);
                    rbtnRelativeRho.setDisable(false);
                }
            }
        });
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

    public void saveCurrentConfiguration(Event event) {
        this.initSimulation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration File (*.csv)", "*.csv"));
        fileChooser.setTitle("Konfiguration speichern unter");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            this.saveFile(file);
        }

    }

    private void saveFile(File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            Vehicle[][] street = trafficSimulator.getStreet();
            Vehicle tmp;
            for (int y = 0; y < street.length; y++) {
                for (int x = 0; x < street[y].length; x++) {
                    tmp = street[y][x];
                    if (tmp != null) {
                       writer.write(String.valueOf(tmp.getCurrentSpeed()));
                    }
                    if(!(x == street[y].length-1)) {
                        writer.write(",");
                    }
                }
                writer.write("\r\n");
            }
            writer.close();
        } catch (IOException ex) {
            LOG.error("Speichern nicht erfolgreich: " + ex.getStackTrace());
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Konfiguration speichern");
        alert.setHeaderText("Info");
        alert.setContentText("Konfiguration erfolgreich gespeichert: " + file.getName());
        alert.showAndWait();

        configurationFile = file;
        rbtnExistingConfig.setSelected(true);
        this.lblFileName.setText(file.getName());
            }

    public void chooseConfigurationFile(Event event) {
        LOG.debug("Dateiauswahl angeklickt");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Configuration File (*.csv)", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            LOG.info(file + " wurde ausgewählt");
            this.configurationFile = file;
            this.lblFileName.setText(file.getName());
            this.rbtnExistingConfig.setSelected(true);
        }
    }

    /**
     * Initialisierung von Standardwerten für die Parameter beim Starten der Applikation
     */
    private void initDefaultValues() {
        txtTracks.setText("2");
        txtSectionAmount.setText("1000");
        txtSwitchProb.setText("0.5");
        txtRelativeRho.setText("0.4");
        txtP.setText("0.2");
        txtP0.setText("0.2");
        txtIterations.setText("1000");
        txtFrom.setText("0");
        txtTo.setText("100");
        txtWorkerAmount.setText("1");
        txtTaskAmount.setText("1");
        txtAbsoluteRho.setDisable(true);
        rbtnRelativeRho.setSelected(true);
    }

    /**
     * Setzt die beiden Canvas zurück
     */
    private void resetCanvas() {
        canvasCarLayer.getGraphicsContext2D().clearRect(0, 0, canvasCarLayer.getWidth(), canvasCarLayer.getHeight());
        canvasStreetLayer.getGraphicsContext2D().clearRect(0, 0, canvasStreetLayer.getWidth(), canvasStreetLayer.getHeight());
        canvasStateView.getGraphicsContext2D().clearRect(0, 0, canvasStateView.getWidth(), canvasStateView.getHeight());
        canvasFlowView.getGraphicsContext2D().clearRect(0, 0, canvasFlowView.getWidth(), canvasFlowView.getHeight());
    }


    private void drawStreet(GraphicsContext gc, int trackAmount, double lineWidth, int from, int to) {
        //Canvas zurücksetzen
        this.resetCanvas();

        int pixelSize = calculatePixelSize(from, to);

        //Straßenfläche
        gc.setFill(Color.DARKGRAY);
        double streetHeight = pixelSize * trackAmount + 2 * lineWidth + 2 * PADDING + ((trackAmount - 1) * lineWidth + PADDING) + 1;
        double streetWidth = (to - from + PADDING) * pixelSize;

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
                gc.strokeLine(j, y + pixelSize + PADDING + lineWidth, j - 4, y + pixelSize + PADDING + lineWidth);
            }
            y += pixelSize + lineWidth + PADDING;
        }
    }

    /**
     * Prüfung ob input ein Int oder Double ist
     *
     * @param str
     * @return boolean
     */
    private static boolean isNumeric(String str) {
        return str.matches("\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private int calculatePixelSize(int from, int to) {
        int canvasWidth = to - from;
        if (canvasWidth > 1000) {
            return PIXEL_SIZE / 4;
        } else if (canvasWidth > 600) {
            return PIXEL_SIZE / 2;
        }
        return PIXEL_SIZE;
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
                && isNumeric(txtSwitchProb.getText())
                && isNumeric(txtSectionAmount.getText())
                && isNumeric(txtFrom.getText())
                && isNumeric(txtTo.getText())
                && isNumeric(txtWorkerAmount.getText())
                && isNumeric(txtTaskAmount.getText())) {

            if (rbtnRelativeRho.isSelected()) {
                if (!isNumeric(txtRelativeRho.getText())) return false;
            }

            if (rbtnAbsoluteRho.isSelected()) {
                if (!isNumeric(txtAbsoluteRho.getText())) return false;
            }

            //Prüfung "von" "bis" des zu betrachteten Straßenabschnittes
            if (Double.parseDouble(txtFrom.getText()) >= Double.parseDouble(txtTo.getText())) return false;

            //Zu betrachtende Abschnitte prüfen, Aufgrund der Breite der Canvas auf max 2100 begrenzt
            if ((Double.parseDouble(txtTo.getText()) - Double.parseDouble(txtFrom.getText()) > 2100)) return false;

            //Prüfung Wahrscheinlichkeiten
            if (Double.parseDouble(txtP0.getText()) > 1.0 || Double.parseDouble(txtP.getText()) > 1.0
                    || Double.parseDouble(txtRelativeRho.getText()) > 1.0
                    || Double.parseDouble(txtSwitchProb.getText()) > 1.0) {
                return false;
            }

            if (Double.parseDouble(txtTo.getText()) > Double.parseDouble(txtSectionAmount.getText())) return false;

            return true;
        }
        return false;
    }

    private class SimulateTask<Void> extends Task<Void> {
        private final TrafficSimulator simulator;
        private final int iterations;
        private final int from;
        private final int to;
        private final int pixelSize;

        public SimulateTask(int iterations, TrafficSimulator simulator, int from, int to) {
            this.simulator = simulator;
            this.iterations = iterations;
            this.from = from;
            this.to = to;
            this.pixelSize = calculatePixelSize(from, to);
        }

        @Override
        protected Void call() throws Exception {
            CurrentGenerationDrawer drawRunable = new CurrentGenerationDrawer(from, to,
                    simulator.getStreet(),
                    simulator.getStreet().length,
                    LINE_WIDTH, PADDING, pixelSize,
                    canvasCarLayer.getGraphicsContext2D(),
                    canvasStateView.getGraphicsContext2D(),
                    canvasFlowView.getGraphicsContext2D());
            for (int i = 0; i < iterations; i++) {
                if (isMoving) {
                    simulator.iterate();
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



