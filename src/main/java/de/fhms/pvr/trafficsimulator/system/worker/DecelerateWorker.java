package de.fhms.pvr.trafficsimulator.system.worker;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

public class DecelerateWorker extends AbstractWorker {

    public DecelerateWorker(Vehicle[][] street, int lowerBorder, int upperBorder) {
        super(street, lowerBorder, upperBorder);
    }

    @Override
    public void run() {
        Vehicle tmp;
        int tmpSpeed, tmpIndex;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x < upperBorder; x++) {
                if ((tmp = street[y][x]) != null) {
                    tmpSpeed = tmp.getCurrentSpeed();
                    if (tmpSpeed > 0) {
                        for (int i = 1; i <= tmp.getCurrentSpeed(); i++) {
                            tmpIndex = (x + i) % street[y].length;
                            if (street[y][tmpIndex] != null) {
                                tmp.setCurrentSpeed(i - 1);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}