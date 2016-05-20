package de.fhms.pvr.trafficsimulator.system;

import java.util.ArrayList;

public class TrafficSimulator {

    private StreetField streetField;

    private double fastLingerProbability;

    private double slowLingerProbability;

    private int iteration;

    public TrafficSimulator(int trackAmount, int sectionAmount, double vehicleDensity,
                                double slowLingerProbability, double fastLingerProbability) {
        this.streetField = new StreetField(trackAmount, sectionAmount, vehicleDensity);
        this.fastLingerProbability = fastLingerProbability;
        this.slowLingerProbability = slowLingerProbability;
        this.iteration = 0;
    }

    public void iterate() {
        accelerateCarPixels();
        breakCarPixels();
        linderCarPixels();
        moveCarPixelSpeedStates();
        this.iteration++;
    }

    private void accelerateCarPixels() {
        CarPixel[][] carPixels = streetField.getCarPixels();
        CarPixel currentPixel;
        int currentPixelSpeedState;
        for (int i = 0; i < carPixels.length; i++) {
            for (int j = 0; j < carPixels[i].length; j++) {
                currentPixel = carPixels[i][j];
                currentPixelSpeedState = currentPixel.getSpeedState();
                // Prüft ob sich ein Fahrzeug auf dem Pixel befindet
                // und bisher nicht die Maximal Geschwindigkeit erreicht hat
                if (currentPixelSpeedState >= 0 && currentPixelSpeedState < CarPixel.MAX_SPEED) {
                    // Erhöht die aktuelle Geschwindigkeit um 1
                    currentPixel.setSpeedState(currentPixelSpeedState + 1);
                }
            }
        }
    }

    private void breakCarPixels() {
        CarPixel[][] carPixels = streetField.getCarPixels();
        CarPixel currentPixel;
        int currentPixelSpeedState;
        for (int i = 0; i < carPixels.length; i++) {
            for (int j = 0; j < carPixels[i].length; j++) {
                currentPixel = carPixels[i][j];
                currentPixelSpeedState = currentPixel.getSpeedState();
                // Prüft ob das aktuelle Fahrzeug bremsen kann
                if (currentPixelSpeedState > 0) {
                    int distance = currentPixel.getDistanceToNextRightNeighbour();
                    // Prüft ob die Distanz zum nächten Fahrzeug positiv ist und kleiner
                    // als die aktuelle Geschwingkeit ist
                    if (distance >= 0 && distance < currentPixelSpeedState) {
                        // Setzt die aktuelle Geschwindigkeit auf die Distanz zum nächsten Fahrzeug
                        currentPixel.setSpeedState(distance);
                    }
                }
            }
        }
    }

    private void linderCarPixels() {
        CarPixel[][] carPixels = streetField.getCarPixels();
        CarPixel currentPixel;
        int currentPixelSpeedState;
        for (int i = 0; i < carPixels.length; i++) {
            for (int j = 0; j < carPixels[i].length; j++) {
                currentPixel = carPixels[i][j];
                currentPixelSpeedState = currentPixel.getSpeedState();
                // Prüft ob das aktuelle Fahrzeug über eine Geschwindigkeit von 1 verfügt
                // und bestimmt per Zufall ob es mit der entsprechenden Wahrscheinlichkeit trödelt oder nicht
                if (currentPixelSpeedState == 1 && TrafficSimulationUtil.isCarPixelLindering(slowLingerProbability)) {
                    currentPixel.setSpeedState(currentPixelSpeedState - 1);
                }
                // Prüft ob das aktuelle Fahrzeug über eine Geschwindigkeit von mehr als 1 verfügt
                // und bestimmt per Zufall ob es mit der entsprechenden Wahrscheinlichkeit trödelt oder nicht
                if (currentPixelSpeedState > 1 && TrafficSimulationUtil.isCarPixelLindering(fastLingerProbability)) {
                    currentPixel.setSpeedState(currentPixelSpeedState - 1);
                }
            }
        }
    }

    private void moveCarPixelSpeedStates() {
        CarPixel[][] carPixels = streetField.getCarPixels();
        CarPixel currentPixel;
        int currentPixelSpeedState;
        CarPixel switchingCarPixel;
        ArrayList<CarPixel.SpeedMovement> moves = new ArrayList<CarPixel.SpeedMovement>();
        for (int i = 0; i < carPixels.length; i++) {
            for (int j = 0; j < carPixels[i].length; j++) {
                currentPixel = carPixels[i][j];
                currentPixelSpeedState = currentPixel.getSpeedState();
                if (currentPixelSpeedState > 0) {
                    switchingCarPixel = currentPixel.getRightNeighbours()[currentPixelSpeedState - 1];
                    moves.add(new CarPixel.SpeedMovement(currentPixel, switchingCarPixel));
                }
            }
        }
        for (CarPixel.SpeedMovement sm: moves) {
            sm.move();
        }
    }

    public void printStreetField() {
        CarPixel[][] carPixels = this.streetField.getCarPixels();
        CarPixel tmp;
        for (int i = 0; i < carPixels.length; i++) {
            for (int j = 0; j < carPixels[i].length; j++) {
                tmp = carPixels[i][j];
                if (tmp.getSpeedState() >= 0) {
                    System.out.print("[" + carPixels[i][j].getSpeedState() + "] ");
                } else {
                    System.out.print("[ ] ");
                }
            }
            System.out.println();
        }
    }

    public StreetField getStreetField() {
        return streetField;
    }

    public int getIteration() {
        return iteration;
    }
}
