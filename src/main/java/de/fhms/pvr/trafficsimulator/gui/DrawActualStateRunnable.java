package de.fhms.pvr.trafficsimulator.gui;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/**
 * Created by Dave on 30.05.2016.
 * Thread zum Visualisieren eines Ausschnitts des Spielfeldes
 */
public class DrawActualStateRunnable implements Runnable {
    private final GraphicsContext gcCarCanvas;
    private final GraphicsContext gcGraph;
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

    public DrawActualStateRunnable(int from, int to, Vehicle[][] street, int trackAmount, GraphicsContext gcCarCanvas,
                                   double lineWidth,
                                   double padding, double pixelSize,
                                   GraphicsContext gcTest) {
        this.from = from;
        this.to = to;
        this.street = street;
        this.pixelSize = pixelSize;
        this.gcCarCanvas = gcCarCanvas;
        this.trackAmount = trackAmount;
        this.lineWidth = lineWidth;
        this.padding = padding;
        this.gcGraph = gcTest;
        this.iteration = 0;
     }

    @Override
    public void run() {
        double xGraph;
        double yGraph = iteration;
        double x;
        double y = lineWidth + padding;
        Vehicle v;
        for (int i = 0; i < trackAmount; i++) {
            xGraph = 0;
            x = 0;
            for (int k = to; k > from; k--) {
                v = street[i][k];
                gcCarCanvas.setFill(Color.DARKGRAY);
                gcGraph.setFill(Color.BLACK);
                if (v != null) { //ein Fahrzeug
                    Color c = this.getColor(v.getCurrentSpeed());
                    gcCarCanvas.setFill(c);//Fahrzeuge
                    gcGraph.setFill(c);//Graph
                }
                gcCarCanvas.fillRect(x, y, pixelSize, pixelSize);
                gcGraph.fillRect(xGraph, yGraph, 1.0, 1.0);
                x += pixelSize + padding;
                xGraph += 1.0;

            }
            yGraph += 1;
            y += pixelSize + lineWidth + padding;//Spurtrennstreifen
        }
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
