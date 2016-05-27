package de.fhms.pvr.trafficsimulator.gui;


import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 *
 * @author Dave
 */
public class ViewController implements Initializable {


    private final double LINE_WIDTH =2.0;
    private final double CAR_WIDTH = 60;
    private final double CAR_HEIGHT = 20;
    private final int PIXEL_SIZE = 5;

    private boolean isMoving = false;
    @FXML
    TabPane tabPaneRoot;

    @FXML
    Tab tabSimulation;

    @FXML
    Tab tabAnalysis;

    @FXML
    Canvas canvasStreetLayer;

    @FXML
    Canvas canvasCarLayer;

    public void start(Event event){
        drawStreet(canvasStreetLayer.getGraphicsContext2D());
        startCars(canvasCarLayer.getGraphicsContext2D());
    }

    public void stop(Event event){
        this.isMoving = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void startCars(GraphicsContext gc){
        //oben
        CarDraw car = new CarDraw(5,4*CAR_HEIGHT/4-CAR_HEIGHT/2,CAR_WIDTH,CAR_HEIGHT,1);
        CarDraw car2 = new CarDraw(100,4*CAR_HEIGHT/4-CAR_HEIGHT/2,CAR_WIDTH,CAR_HEIGHT,4);
        //unten
        CarDraw car3 = new CarDraw(350,4*CAR_HEIGHT/2+CAR_HEIGHT/2,CAR_WIDTH,CAR_HEIGHT,3);
        CarDraw car4 = new CarDraw(150,4*CAR_HEIGHT/2+CAR_HEIGHT/2,CAR_WIDTH,CAR_HEIGHT,5);


        ArrayList<CarDraw> cars = new ArrayList<CarDraw>();
        cars.add(car);
        cars.add(car2);
        cars.add(car3);
        cars.add(car4);


        isMoving = true;
        Thread drawThread = new Thread(new DrawRunnable(gc,cars));
        drawThread.start();

    }

    private void drawStreet(GraphicsContext gc){
        //Straßenfläche
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0,0,canvasStreetLayer.getWidth(),4*CAR_HEIGHT);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(LINE_WIDTH);
        //Oberer Randstreifen
        gc.strokeLine(0,2,canvasStreetLayer.getWidth(),2);
        //Unterer Randstreifen
        gc.strokeLine(0,4*CAR_HEIGHT-2,canvasStreetLayer.getWidth(),4*CAR_HEIGHT-2);
        //Mittelstreifen
        double mid = 4*CAR_HEIGHT/2;
        for (int i =0; i<=canvasStreetLayer.getWidth(); i+=10){
            gc.strokeLine(i,mid,i+5,mid);
        }
    }

    private class DrawRunnable implements Runnable {
        private GraphicsContext gc;
        private ArrayList<CarDraw> cars;

        public DrawRunnable(GraphicsContext gc,ArrayList<CarDraw> cars){
            this.gc = gc;
            this.cars = new ArrayList<CarDraw>(cars);
        }
        @Override
        public void run() {
            final AnimationTimer animationTimer = new AnimationTimer() {
                long lastUpdateTime = 0;
                @Override
                public void handle(long now) {
                    if ((lastUpdateTime > 0 && isMoving)) { //60ms
                        System.out.println("UPDATE");
                        final double elapsedSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
                        System.out.println("Elapsed Time: " + elapsedSeconds);
                        for (CarDraw car: cars) {
                            //final double deltaX = elapsedSeconds * car.getSpeed()*8;
                            final double oldX = car.getPosX();
                            gc.clearRect(oldX, car.getPosY(), car.getWidth(), car.getHeight());
                            final double newX = (oldX + car.getSpeed()) % gc.getCanvas().getWidth();
                            car.setPosX(newX);
                            gc.setFill(car.getColor());
                            gc.fillRect(newX, car.getPosY(), car.getWidth(), car.getHeight());
                            //gc.drawImage(car.getImage(),newX, car.getPosY(), car.getWidth(), car.getHeight());
                        }
                    }else if(!isMoving){
                        System.out.println("STOP");
                        this.stop();
                    }
                    lastUpdateTime = now;
                    System.out.println("TEST");
                }

            };
            animationTimer.start();

            //while (isMoving) {
                /*
                for (CarDraw car:cars) {
                    final KeyValue kv = new KeyValue(car.getXProperty(), (car.getPosX() + car.getWidth())%gc.getCanvas().getWidth(),
                            Interpolator.EASE_BOTH);
                    final KeyFrame kf = new KeyFrame(Duration.millis(60), kv);
                    timeline.getKeyFrames().add(kf);

                    //gc.setFill(car.getColor());
                    gc.drawImage(car.getImage(),car.getPosX(), car.getPosY(), car.getWidth(), car.getHeight());
                    //gc.fillRect(car.getPosX(), car.getPosY(), car.getWidth(), car.getHeight());
                    car.setPosX(car.getPosX()+car.getWidth() + 10);
                }
                timeline.play();
                /*
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (CarDraw car:cars){
                    gc.clearRect(car.getPosX(), car.getPosY(), car.getWidth(), car.getHeight());
                    car.setPosX((car.getPosX() + car.getWidth() + 10)%gc.getCanvas().getWidth());
                }
                */

           // }
        }
    }

}
