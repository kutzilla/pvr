package de.fhms.pvr.verkehrssimulation.zweispurig.sequentiell;


/**
 * Created by Matthias on 08.04.16.
 */
public class Spur {

    private Spurabschnitt[] spurabschnitte;

    public Spur(int n) {
        this.spurabschnitte = new Spurabschnitt[n];
        for (int i = 0; i < this.spurabschnitte.length; i++) {
            this.spurabschnitte[i] = new Spurabschnitt();
        }
    }

    public Spurabschnitt[] getSpurabschnitte() {
        return spurabschnitte;
    }
}
