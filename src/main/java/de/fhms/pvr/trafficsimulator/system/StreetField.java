package de.fhms.pvr.trafficsimulator.system;

import java.util.Random;

public class StreetField {

    private CarPixel[][] carPixels;

    private int trackAmount;

    private int sectionAmount;

    protected StreetField(int trackAmount, int sectionAmount, double vehicleDensity) {
        this.carPixels = new CarPixel[trackAmount][sectionAmount];

        for (int i = 0; i < this.carPixels.length; i++) {
            for (int j = 0; j < this.carPixels[i].length; j++) {
                this.carPixels[i][j] = new CarPixel(-1, i, j, this);
            }
        }
        for (int i = 0; i < this.carPixels.length; i++) {
            for (int j = 0; j < this.carPixels[i].length; j++) {
                this.carPixels[i][j].setNeighboursInFront();
            }
        }

        int statesToChange = (int) ((double) trackAmount * sectionAmount * vehicleDensity);
        int randomVerticalPosition, randomHorizontalPosition, randomSpeedStateValue, i = 0;
        CarPixel tmp;
        Random random = new Random();
        while(i < statesToChange) {
            randomVerticalPosition = random.nextInt(trackAmount);
            randomHorizontalPosition = random.nextInt(sectionAmount);
            tmp = this.carPixels[randomVerticalPosition][randomHorizontalPosition];
            if (tmp.getSpeedState() < 0) {
                randomSpeedStateValue = random.nextInt(CarPixel.MAX_SPEED + 1);
                tmp.setSpeedState(randomSpeedStateValue);
                i++;
            }
        }
    }

    public CarPixel[][] getCarPixels() {
        return carPixels;
    }

    public int getTrackAmount() {
        return trackAmount;
    }

    public int getSectionAmount() {
        return sectionAmount;
    }

}
