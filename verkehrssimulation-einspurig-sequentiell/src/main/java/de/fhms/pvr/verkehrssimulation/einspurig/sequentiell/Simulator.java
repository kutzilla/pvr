package de.fhms.pvr.verkehrssimulation.einspurig.sequentiell;

/**
 * Created by Matthias on 08.04.16.
 */
public class Simulator {

    // Troedelwahrscheinlichkeit für bereits fahrende Fahrzeuge
    private double p;

    // Troedelwahrscheinlichkeit für Fahrzeuge die im Stau stehen (v = 0)
    private double p0;

    // Fahrzeugdichte
    private double rho;

    // Aktueller Iterationsschritt
    private int k;

    // Spuren für die Verkehrssimulation
    private Spur spuren;

    public Simulator(double p, double p0, double rho, int n) {
        this.p = p;
        this.p0 = p0;
        this.rho = rho;
        this.k = 0;
        this.spuren = new Spur(n);
    }

    public double getP() {
        return p;
    }

    public double getP0() {
        return p0;
    }

    public double getRho() {
        return rho;
    }

    public int getK() {
        return k;
    }


    /**
     * Simuliert den nächsten Simulationsschritt
     */
    public void simuliere() {
        // TODO Simulation implementierens
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Es müssen vier Parameter übergeben werden");
            System.exit(-1);
        }

        double p = Double.valueOf(args[0]);
        double p0 = Double.valueOf(args[1]);
        double rho = Double.valueOf(args[2]);
        int n = Integer.valueOf(args[3]);

        Simulator simulator = new Simulator(p, p0, rho, n);

        // TODO Weitere Objekte implementieren
    }

}
