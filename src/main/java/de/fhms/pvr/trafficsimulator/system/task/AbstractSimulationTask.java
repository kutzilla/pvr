package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

import java.util.concurrent.Callable;

public abstract class AbstractSimulationTask implements Callable<Void> {

    protected int lowerBound;

    protected int upperBound;

    protected int bound;

    protected Vehicle[][] street;

    public AbstractSimulationTask(Vehicle[][] street, int lowerBorder, int upperBorder) {
        this.street = street;
        this.lowerBound = lowerBorder;
        this.upperBound = upperBorder;
        this.bound = upperBorder - lowerBorder;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }


    public int getBound() {
        return bound;
    }

    public Vehicle[][] getStreet() {
        return street;
    }
}
