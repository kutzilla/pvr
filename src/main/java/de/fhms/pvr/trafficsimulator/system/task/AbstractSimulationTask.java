package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

import java.util.concurrent.Callable;

public abstract class AbstractSimulationTask implements Callable<Vehicle[][]> {

    protected int lowerBound;

    protected int upperBound;

    protected Vehicle[][] street;

    public AbstractSimulationTask(Vehicle[][] street, int lowerBound, int upperBound) {
        this.street = street;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public Vehicle[][] getStreet() {
        return street;
    }
}
