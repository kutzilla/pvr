package de.fhms.pvr.trafficsimulator.system.worker;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

public class AccelerateWorker extends AbstractWorker {

    public AccelerateWorker(Vehicle[][] street, int lowerBorder, int upperBorder) {
        super(street,lowerBorder,upperBorder);
    }

    @Override
    public void run() {
        Vehicle tmp;
        for (int y = 0; y < street.length; y++) {
            for (int x = lowerBorder; x < upperBorder; x++) {
                if ((tmp = street[y][x]) != null) {
                    tmp.incrementCurrentSpeed();
                }
            }
        }
    }
}
