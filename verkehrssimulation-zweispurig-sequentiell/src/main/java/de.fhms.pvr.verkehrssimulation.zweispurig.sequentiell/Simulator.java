package de.fhms.pvr.verkehrssimulation.zweispurig.sequentiell;


import java.util.ArrayList;
import java.util.List;
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

    //Spurwechselwahrscheinlichkeit
    private double c;

    // Aktueller Iterationsschritt
    private int k;

    // Spuren für die Verkehrssimulation
    private Spur[] spuren;

    private int anzahlFahrzeuge;

    public Simulator(double p, double p0, double rho, int n, double c) {
        this.p = p;
        this.p0 = p0;
        this.rho = rho;
        this.k = 0;
        this.c = c;
        this.anzahlFahrzeuge = (int) (n * rho);
        this.spuren = new Spur[2];
        this.spuren[0] = new Spur(n);
        this.spuren[1] = new Spur(n);
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
        int anzahlSpurabschnnitte = this.spuren[0].getSpurabschnitte().length;

        int zufallsSpur;
        while(!fahrzeugStapel.empty()) {
            zufallsSpur = randomGenerator.nextInt(spuren.length);
            Spurabschnitt spurabschnitt = this.spuren[zufallsSpur].getSpurabschnitte()[index % anzahlSpurabschnnitte];
            if (randomGenerator.nextBoolean() && spurabschnitt.getFahrzeug() == null) {
                spurabschnitt.setFahrzeug(fahrzeugStapel.pop());
            }
            index++;
        }
    }

    private void initialisiere(boolean zufall) {
        System.out.println("Simulation initialisieren");
        if(zufall) {
            verteileFahrzeugeAufSpurabschnitte();
        }else{
            inititalisiereSpurwechselAusgangslage();
        }

    }

    private void inititalisiereSpurwechselAusgangslage(){
        spuren[0].getSpurabschnitte()[0].setFahrzeug(new Fahrzeug(0));
        spuren[0].getSpurabschnitte()[2].setFahrzeug(new Fahrzeug(1));
        spuren[0].getSpurabschnitte()[6].setFahrzeug(new Fahrzeug(3));
        spuren[0].getSpurabschnitte()[8].setFahrzeug(new Fahrzeug(2));
        spuren[0].getSpurabschnitte()[18].setFahrzeug(new Fahrzeug(2));

        spuren[1].getSpurabschnitte()[0].setFahrzeug(new Fahrzeug(2));
        spuren[1].getSpurabschnitte()[11].setFahrzeug(new Fahrzeug(0));
        spuren[1].getSpurabschnitte()[12].setFahrzeug(new Fahrzeug(2));
        spuren[1].getSpurabschnitte()[14].setFahrzeug(new Fahrzeug(0));
        spuren[1].getSpurabschnitte()[15].setFahrzeug(new Fahrzeug(1));
        spuren[1].getSpurabschnitte()[17].setFahrzeug(new Fahrzeug(4));
    }


    private void spurAusgeben() {
        Fahrzeug fahrzeug;
        for (Spur spur: spuren) {
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
    }


    /**
     * Beschleunigt alle Fahrzeuge auf der Spur
     */
    private void fahrzeugeBeschleunigen() {
        Fahrzeug fahrzeug;
        for (Spur spur: spuren) {
            for (Spurabschnitt spurabschnitt : spur.getSpurabschnitte()) {
                fahrzeug = spurabschnitt.getFahrzeug();
                if (fahrzeug != null) {
                    fahrzeug.beschleunigen();
                }
            }
        }
    }

    private void fahrzeugeBremsen() {
        Fahrzeug fahrzeug;
        int nachbarFahrzeugIndex;
        for (Spur spur: spuren) {
            Spurabschnitt[] spurabschnitte = spur.getSpurabschnitte();
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
    }

    private void fahrzeugeTroedeln() {
        Random randomGenerator = new Random();
        Fahrzeug fahrzeug;
        for (Spur spur: spuren) {
            Spurabschnitt[] spurabschnitte = spur.getSpurabschnitte();
            for (Spurabschnitt spurabschnitt : spurabschnitte) {
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
    }

    public void fahrzeugeFortbewegen() {
        Fahrzeug fahrzeug;
        Spur neueSpur;
        Spurabschnitt neueSpurAbschnitte[];
        for (int i = 0; i < spuren.length; i++) {
            Spurabschnitt[] aktSpurabschnitte = spuren[i].getSpurabschnitte();
            neueSpur = new Spur(aktSpurabschnitte.length);
            neueSpurAbschnitte = neueSpur.getSpurabschnitte();
            for (int j = 0; j < aktSpurabschnitte.length; j++) {
                fahrzeug = aktSpurabschnitte[j].getFahrzeug();
                if (fahrzeug != null) {
                    neueSpurAbschnitte[(j + fahrzeug.getGeschwindigkeit()) % aktSpurabschnitte.length].setFahrzeug(fahrzeug);
                }
            }
            spuren[i] = neueSpur;
        }
    }


    public void farzeugeSpurenWechseln(){
        //Behinderung auf der eigenen Spur
        Random randomGenerator = new Random();
        int spurenLaenge = spuren[0].getSpurabschnitte().length;
        List<Integer> wechselIndizes = new ArrayList<>();

        for (int i = 0; i< spuren.length; i++) { //Spuren
            for(int j = 0; j< spurenLaenge; j++){ //einzelne Spurabschnitte (Pixel)
                Spurabschnitt aktSpurabschnitt = spuren[i].getSpurabschnitte()[j];
                if(aktSpurabschnitt.getFahrzeug() != null){ //Fahrzeug auf Spurabschnitt
                    Fahrzeug aktFahrzeug = aktSpurabschnitt.getFahrzeug();
                    for(int k = 1; k <= aktFahrzeug.getGeschwindigkeit()+1; k++){ //alle Spurabschnitte vor dem Fahrzeug prüfen
                        if(spuren[i].getSpurabschnitte()[(j+k) % spurenLaenge].getFahrzeug() != null){//Fahrzeug im Weg
                            Spur wechselSpur = spuren[(i + 1) % spuren.length];
                          if(istSpurwechselMoeglich(j,wechselSpur, aktFahrzeug)){
                              double wechseln = ((double) randomGenerator.nextInt(100)) / 100;
                              if(wechseln <= c){ //wechseln

                                  wechselIndizes.add(j);
                              }
                          }
                        }
                    }
                }
            }
        }

        for (Integer index:wechselIndizes) {
            int spurIndex;
            Fahrzeug wechselndesFahrzeug;
            if(spuren[0].getSpurabschnitte()[index].getFahrzeug()!=null){
                wechselndesFahrzeug = new Fahrzeug(spuren[0].getSpurabschnitte()[index].getFahrzeug().getGeschwindigkeit());
                spuren[0].getSpurabschnitte()[index].setFahrzeug(null);
                spurIndex = 1;
            }else{
                wechselndesFahrzeug = new Fahrzeug(spuren[1].getSpurabschnitte()[index].getFahrzeug().getGeschwindigkeit());
                spuren[1].getSpurabschnitte()[index].setFahrzeug(null);
                spurIndex = 0;
            }
            spuren[spurIndex].getSpurabschnitte()[index].setFahrzeug(wechselndesFahrzeug);
        }

    }

    private boolean istSpurwechselMoeglich(int aktSpurabschnittsIndex,Spur zuPruefendeSpur, Fahrzeug wechselndesFahrzeug) {
        if(zuPruefendeSpur.getSpurabschnitte()[aktSpurabschnittsIndex].getFahrzeug()!=null){
            return false; //Fahrzeug auf der gegenüberliegenden Spur
        }


        for(int i=1; i<=Fahrzeug.MAX_GESCHWINDIGKEIT; i++){ //5 Nachfolgeabschnitte prüfen
            int neuePos = berechneSpurenpositionHinter(aktSpurabschnittsIndex, i);
            if(zuPruefendeSpur.getSpurabschnitte()[neuePos].getFahrzeug()!=null){
                return false;
            }
        }

        for(int i=1; i<=wechselndesFahrzeug.getGeschwindigkeit() +1; i++){ //j + 1 Vorgänger prüfen
            if(zuPruefendeSpur.getSpurabschnitte()[(aktSpurabschnittsIndex+i) % zuPruefendeSpur.getSpurabschnitte().length].getFahrzeug()!=null){
                return false;
            }
        }


        return true;
    }

    private int berechneSpurenpositionHinter(int aktIndex, int delta){
        int neuePos = aktIndex - delta;

        if(neuePos < 0){
            return spuren[0].getSpurabschnitte().length  - neuePos;
        }else{
            return neuePos;
        }
    }

    /**
     * Simuliert den nächsten Simulationsschritt
     */
    public void simuliere() {
        //System.out.println("Spiefeld zum Zeitpunkt " + k);
        //spurAusgeben();
        farzeugeSpurenWechseln();
        //System.out.println("Spiefeld nach Spurenwechsel");
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
        System.out.println("Spielfeld nach Fortbewegen um Zeitpunkt " + k);
        spurAusgeben();
        k++;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Es müssen sechs Parameter übergeben werden");
            System.exit(-1);
        }



        double p = Double.valueOf(args[0]);
        double p0 = Double.valueOf(args[1]);
        double rho = Double.valueOf(args[2]);
        int n = Integer.valueOf(args[3]);
        int k = Integer.valueOf(args[4]);
        double c = Double.valueOf(args[5]);
        boolean zufall = Boolean.valueOf(args[6]);

        Simulator simulator = new Simulator(p, p0, rho, n, c);

        // Erstellen der Startkonfiguration
        simulator.initialisiere(zufall);
        System.out.println("Initialer Zustand");
        simulator.spurAusgeben();
        for (int i = 0; i < k; i++) {
            simulator.simuliere();
        }
    }



}
