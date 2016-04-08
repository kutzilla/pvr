package de.fhms.pvr.verkehrssimulation.einspurig.sequentiell;

import java.util.Random;
import java.util.Stack;

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
    private Spur spur;

    private int anzahlFahrzeuge;

    public Simulator(double p, double p0, double rho, int n) {
        this.p = p;
        this.p0 = p0;
        this.rho = rho;
        this.k = 0;
        this.anzahlFahrzeuge = (int) (n * rho);
        this.spur = new Spur(n);
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

    private void verteileFahrzeugeAufSpurabschnitte() {
        Random randomGenerator = new Random();
        Stack<Fahrzeug> fahrzeugStapel = new Stack<>();
        for (int i = 0; i < anzahlFahrzeuge; i++) {
            fahrzeugStapel.push(new Fahrzeug(randomGenerator.nextInt(Fahrzeug.MAX_GESCHWINDIGKEIT + 1)));
        }

        int index = 0;
        int anzahlSpurabschnnitte = this.spur.getSpurabschnitte().length;
        while(!fahrzeugStapel.empty()) {
            Spurabschnitt spurabschnitt = this.spur.getSpurabschnitte()[index % anzahlSpurabschnnitte];
            if (randomGenerator.nextBoolean() && spurabschnitt.getFahrzeug() == null) {
                spurabschnitt.setFahrzeug(fahrzeugStapel.pop());
            }
            index++;
        }
    }

    private void initialisiere() {
        System.out.println("Simulation initialisieren");
        verteileFahrzeugeAufSpurabschnitte();
    }

    private void spurAusgeben() {
        Fahrzeug fahrzeug;
        for (int i = 0; i < spur.getSpurabschnitte().length; i++) {
            fahrzeug = spur.getSpurabschnitte()[i].getFahrzeug();
            if (fahrzeug != null) {
                System.out.print("[" + spur.getSpurabschnitte()[i].getFahrzeug().getGeschwindigkeit() + "]");
            } else {
                System.out.print("[ ]");
            }
            System.out.print("  ");
        }
        System.out.println("");
    }

    /**
     * Simuliert den nächsten Simulationsschritt
     */
    public void simuliere() {
        // TODO Simulation implementieren
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

        // Erstellen der Startkonfiguration
        simulator.initialisiere();
        simulator.spurAusgeben();
    }



}
