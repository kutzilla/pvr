package de.fhms.pvr.verkehrssimulation.einspurig.sequentiell;

/**
 * Created by Matthias on 08.04.16.
 */
public class Fahrzeug {

    private static final int MAX_GESCHWINDIGKEIT = 5;

    private static final int MIN_GESCHWINDIGKEIT = 0;

    private int geschwindigkeit;

    public Fahrzeug(int geschwindigkeit) {
        if (geschwindigkeit < MIN_GESCHWINDIGKEIT && geschwindigkeit > MAX_GESCHWINDIGKEIT) {
            throw new IllegalArgumentException();
        }
        this.geschwindigkeit = geschwindigkeit;
    }

    public int getGeschwindigkeit() {
        return geschwindigkeit;
    }
}
