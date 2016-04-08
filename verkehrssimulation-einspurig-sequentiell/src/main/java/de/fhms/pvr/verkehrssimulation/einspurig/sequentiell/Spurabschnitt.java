package de.fhms.pvr.verkehrssimulation.einspurig.sequentiell;

import java.util.Objects;

/**
 * Created by Matthias on 08.04.16.
 */
public class Spurabschnitt {

    // Länge des Spurabschnitts
    public static final double LAENGE = 7.5;

    // Referenz zum Fahrzeug. Ist null, wenn kein Fahrzeug auf dem Abschnitt positioniert ist
    private Fahrzeug fahrzeug;

    public Spurabschnitt() {
        this.fahrzeug = null;
    }

    public Fahrzeug getFahrzeug() {
        return fahrzeug;
    }

    public void setFahrzeug(Fahrzeug fahrzeug) {
        this.fahrzeug = fahrzeug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spurabschnitt that = (Spurabschnitt) o;
        return Objects.equals(fahrzeug, that.fahrzeug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fahrzeug);
    }

    @Override
    public String toString() {
        return "Spurabschnitt{" +
                "fahrzeug=" + fahrzeug +
                '}';
    }
}
