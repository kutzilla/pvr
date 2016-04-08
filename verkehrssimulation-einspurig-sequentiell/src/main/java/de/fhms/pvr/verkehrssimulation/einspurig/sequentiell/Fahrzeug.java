package de.fhms.pvr.verkehrssimulation.einspurig.sequentiell;

import java.util.Objects;

/**
 * Created by Matthias on 08.04.16.
 */
public class Fahrzeug {

    public static final int MAX_GESCHWINDIGKEIT = 5;

    public static final int MIN_GESCHWINDIGKEIT = 0;

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

    public void beschleunigen() {
        if (geschwindigkeit < MAX_GESCHWINDIGKEIT) {
            geschwindigkeit++;
        }
    }

    public void bremsen(int delta) {
        if ((geschwindigkeit - delta) < MIN_GESCHWINDIGKEIT) {
            geschwindigkeit = 0;
        } else {
            geschwindigkeit -= delta;
        }
    }

    public void troedeln() {
        bremsen(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fahrzeug fahrzeug = (Fahrzeug) o;
        return geschwindigkeit == fahrzeug.geschwindigkeit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(geschwindigkeit);
    }

    @Override
    public String toString() {
        return "Fahrzeug{" +
                "geschwindigkeit=" + geschwindigkeit +
                '}';
    }
}
