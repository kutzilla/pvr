package de.fhms.pvr.trafficsimulator.gui;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

/**
 * Created by Dave on 30.05.2016.
 * Zeichnet die Graphen, sowie den bestimmten Spurabschnitt
 */
public class CurrentGenerationDrawer implements Callable<Boolean> {
    private final GraphicsContext gcCarCanvas;
    private final GraphicsContext gcStateView;
    private final GraphicsContext gcFlowView;
    private final int from;
    private final int to;
    private final int trackAmount;
    private final Vehicle[][] street;
    private final double pixelSize;
    //Dicke der Linien
    private final double lineWidth;
    //Abstände zwischen den Autopixeln auf der Live-Ansicht
    private final double padding;
    //Aktuelle Iteration
    private int iteration;
    private double xGraph;
    private double yGraph;

    public CurrentGenerationDrawer(int from, int to, Vehicle[][] street, int trackAmount,
                                   double lineWidth,
                                   double padding, double pixelSize,
                                   GraphicsContext gcCarCanvas, GraphicsContext gcStateView, GraphicsContext gcFlowView) {
        this.from = from;
        this.to = to;
        this.street = street;
        this.pixelSize = pixelSize;
        this.gcCarCanvas = gcCarCanvas;
        this.gcStateView = gcStateView;
        this.gcFlowView = gcFlowView;
        this.trackAmount = trackAmount;
        this.lineWidth = lineWidth;
        this.padding = padding;
        this.iteration = 0;
    }

    @Override
    public Boolean call() {
        yGraph = iteration *trackAmount;
        double x;
        double y = lineWidth + padding;
        Color c = null;
        Vehicle v;
        if (Platform.isFxApplicationThread()) {
            for (int i = 0; i < trackAmount; i++) {
                xGraph = 0;
                x = 0;
                for (int k = from; k <= to; k++) {
                    v = street[i][k];
                    gcCarCanvas.setFill(Color.DARKGRAY);
                    gcStateView.setFill(Color.BLACK);
                    if (v != null) { //ein Fahrzeug
                        c = this.getColor(v.getCurrentSpeed());
                        gcCarCanvas.setFill(c);//Fahrzeuge
                        gcStateView.setFill(c);//Graph StateView
                    }else{
                        gcFlowView.setFill(c);
                    }
                    gcCarCanvas.fillRect(x, y, pixelSize, pixelSize);
                    gcStateView.fillRect(xGraph, yGraph%gcStateView.getCanvas().getHeight(), 1.0, 1.0);
                    gcFlowView.fillRect(xGraph, yGraph%gcFlowView.getCanvas().getHeight(), 1.0, 1.0);

                    x += pixelSize + padding;
                    xGraph += 1.0;

                }
                yGraph += 1;
                y += pixelSize + lineWidth + padding;//Spurtrennstreifen
            }
        }
        return true;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;

    }

    private Color getColor(int speed) {
        switch (speed) {
            case 0:
                return Color.RED;
            case 1:
                return Color.ORANGE;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.PURPLE;
            default:
                return Color.BLACK;
        }
    }

}
