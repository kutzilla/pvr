package de.fhms.pvr.trafficsimulator.system;


public class CarPixel {

    public static final int MAX_SPEED = 5;

    private int speedState;

    private int horizontalPosition;

    private int verticalPosition;

    private StreetField streetField;

    private CarPixel[] rightNeighbours;

    
    protected CarPixel(int speedState, int verticalPosition, int horizontalPosition, StreetField streetField) {
        this.speedState = speedState;
        this.verticalPosition = verticalPosition;
        this.horizontalPosition = horizontalPosition;
        this.streetField = streetField;
        this.rightNeighbours = new CarPixel[MAX_SPEED];
    }

    protected void setNeighboursInFront() {
        int neighbourPosition;
        for (int i = 0; i < this.rightNeighbours.length; i++) {
            neighbourPosition = (horizontalPosition + i + 1) % streetField.getCarPixels()[verticalPosition].length;
            this.rightNeighbours[i] = streetField.getCarPixels()[verticalPosition][neighbourPosition];
        }
    }

    public int getDistanceToNextRightNeighbour() {
        for (int i = 0; i < rightNeighbours.length; i++) {
            if (rightNeighbours[i].getSpeedState() >= 0) {
                return i;
            }
        }
        return -1;
    }

    public CarPixel[] getRightNeighbours() {
        return this.rightNeighbours;
    }

    public int getSpeedState() {
        return speedState;
    }

    public void setSpeedState(int speedState) {
        this.speedState = speedState;
    }

    public int getHorizontalPosition() {
        return horizontalPosition;
    }

    public int getVerticalPosition() {
        return verticalPosition;
    }

    @Override
    public String toString() {
        return "CarPixel{" +
                "speedState=" + speedState +
                ", horizontalPosition=" + horizontalPosition +
                ", verticalPosition=" + verticalPosition +
                '}';
    }

    public static class SpeedMovement {

        private CarPixel fromCarPixel;

        private CarPixel toCarPixel;

        public SpeedMovement(CarPixel fromCarPixel, CarPixel toCarPixel) {
            this.fromCarPixel = fromCarPixel;
            this.toCarPixel = toCarPixel;
        }

        public CarPixel getFromCarPixel() {
            return fromCarPixel;
        }


        public CarPixel getToCarPixel() {
            return toCarPixel;
        }

        public void move() {
            int tmp = fromCarPixel.getSpeedState();
            fromCarPixel.setSpeedState(toCarPixel.getSpeedState());
            toCarPixel.setSpeedState(tmp);
        }
    }

}
