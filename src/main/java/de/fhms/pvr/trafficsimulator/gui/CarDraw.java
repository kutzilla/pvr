package de.fhms.pvr.trafficsimulator.gui;

import javafx.beans.property.DoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Dave on 22.05.2016.
 * @author Dave
 */
public class CarDraw {

    private Rectangle rect;
    private Image image;
    private Color color;
    private int speed;


    public CarDraw(double posX, double posY, double width, double height, int speed) {
        this.rect = new Rectangle(posX,posY,width,height);
        this.speed = speed;
        //this.image = new Image(getClass().getResourceAsStream("/assets/green_car.png"));
    }

    public Color getColor(){
        switch (this.speed){
            case 0: return Color.RED;
            case 1: return Color.ORANGE;
            case 2: return Color.YELLOW;
            case 3: return Color.GREEN;
            case 4: return Color.BLUE;
            case 5: return Color.PURPLE;
            default: return Color.BLACK;
        }
    }

    public double getPosX() {
        return rect.getX();
    }

    public DoubleProperty getXProperty(){
        return rect.xProperty();
    }

    public double getPosY() {
        return rect.getY();
    }

    public double getWidth(){return rect.getWidth();}

    public double getHeight() {
        return rect.getHeight();
    }

    public void setPosX(double newPosX){
        this.rect.setX(newPosX);
    }

    public void setPosY(double newPosY){
        this.rect.setY(newPosY);
    }

    public Image getImage() {
        return image;
    }

    public int getSpeed() {
        return speed;
    }
}
