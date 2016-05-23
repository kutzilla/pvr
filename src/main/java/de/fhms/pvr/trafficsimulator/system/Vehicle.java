package de.fhms.pvr.trafficsimulator.system;

public class Vehicle {

    public static final int MAX_SPEED = 5;

    private int currentSpeed;

    private int moveCount;

    public Vehicle(int currentSpeed) {
        this.currentSpeed = currentSpeed;
        this.moveCount = 0;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public void incrementCurrentSpeed() {
        if (this.currentSpeed < MAX_SPEED) {
            this.currentSpeed++;
        }
    }

    public void decrementCurrentSpeed() {
        if (this.currentSpeed > 0) {
            this.currentSpeed--;
        }
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void incrementMoveCount() {
        this.moveCount++;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "currentSpeed=" + currentSpeed +
                ", moveCount=" + moveCount +
                '}';
    }
}
