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
     * Beschleunigt alle Fahrzeuge auf der Spur
     */
    private void fahrzeugeBeschleunigen() {
        Fahrzeug fahrzeug;
        for (Spurabschnitt spurabschnitt: this.spur.getSpurabschnitte()) {
            fahrzeug = spurabschnitt.getFahrzeug();
            if (fahrzeug != null) {
                fahrzeug.beschleunigen();
            }
        }
    }

    private void fahrzeugeBremsen() {
        Fahrzeug fahrzeug;
        int nachbarFahrzeugIndex;
        Spurabschnitt spurabschnitte[] = this.spur.getSpurabschnitte();
        for (int i = 0; i < spurabschnitte.length; i++) {
            fahrzeug = spurabschnitte[i].getFahrzeug();
            if (fahrzeug != null) {
                for (int j = i + 1; j <= i + fahrzeug.getGeschwindigkeit(); j++) {
                    nachbarFahrzeugIndex = j % spurabschnitte.length;
                    if (spurabschnitte[nachbarFahrzeugIndex].getFahrzeug() != null) {
                        // - 1 um vor dem Nachbarfahrzeug stehen zu bleiben
                        fahrzeug.bremsen(j - i - 1);
                        break;
                    }
                }
            }
        }
    }

    private void fahrzeugeTroedeln() {
        Random randomGenerator = new Random();
        Fahrzeug fahrzeug;
        for (Spurabschnitt spurabschnitt: this.spur.getSpurabschnitte()) {
            fahrzeug = spurabschnitt.getFahrzeug();
            if (fahrzeug != null) {
                double troedeln = ((double) randomGenerator.nextInt(100)) / 100;
                if (fahrzeug.getGeschwindigkeit() == 1) {
                    // Zufälliges trödeln
                    if (troedeln <= p0) {
                        fahrzeug.troedeln();
                    }
                }
                if (fahrzeug.getGeschwindigkeit() > 1) {
                    if (troedeln <= p) {
                        fahrzeug.troedeln();
                    }
                }
            }
        }
    }

    public void fahrzeugeFortbewegen() {
        Fahrzeug fahrzeug;
        Spurabschnitt spurabschnitte[] = this.spur.getSpurabschnitte();
        Spur neueSpur = new Spur(spurabschnitte.length);
        Spurabschnitt neueSpurAbschnitte[] = neueSpur.getSpurabschnitte();
        for (int i = 0 ; i < spurabschnitte.length; i++) {
            fahrzeug = spurabschnitte[i].getFahrzeug();
            if (fahrzeug != null) {
                //System.out.println("Spring von " + i + " zu " + (i + fahrzeug.getGeschwindigkeit()) % spurabschnitte.length);
                neueSpurAbschnitte[(i + fahrzeug.getGeschwindigkeit()) % spurabschnitte.length].setFahrzeug(fahrzeug);
            }
        }
        this.spur = neueSpur;
    }

    /**
     * Simuliert den nächsten Simulationsschritt
     */
    public void simuliere() {
        // TODO Simulation implementieren
        //System.out.println("Spiefeld zum Zeitpunkt " + k);
        //spurAusgeben();
        fahrzeugeBeschleunigen();
        //System.out.println("Spiefeld nach Beschleunigen");
        //spurAusgeben();
        fahrzeugeBremsen();
        //System.out.println("Spielfeld nach Bremsen");
        //spurAusgeben();
        fahrzeugeTroedeln();
        //System.out.println("Spielfeld nach Troedeln");
        //spurAusgeben();
        fahrzeugeFortbewegen();
        //System.out.println("Spielfeld nach Fortbewegen um Zeitpunkt " + k);
        spurAusgeben();
        k++;
    }

    public static void clear() {
        for (int i = 0; i < 54; i++) {
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Es müssen fünf Parameter übergeben werden");
            System.exit(-1);
        }



        double p = Double.valueOf(args[0]);
        double p0 = Double.valueOf(args[1]);
        double rho = Double.valueOf(args[2]);
        int n = Integer.valueOf(args[3]);
        int k = Integer.valueOf(args[4]);

        Simulator simulator = new Simulator(p, p0, rho, n);

        // Erstellen der Startkonfiguration
        simulator.initialisiere();
        simulator.spurAusgeben();
        //clear();
        for (int i = 0; i < k; i++) {
            simulator.simuliere();
            clear();
            Thread.sleep(250);
        }
    }



}
