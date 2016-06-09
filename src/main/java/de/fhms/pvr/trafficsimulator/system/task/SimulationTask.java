package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;

import java.util.concurrent.Callable;

public abstract class SimulationTask implements Callable<Void> {

    protected int lowerBorder;

    protected int upperBorder;

    protected int bound;

    protected Vehicle[][] street;

    public SimulationTask(Vehicle[][] street, int lowerBorder, int upperBorder) {
        this.street = street;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
        this.bound = upperBorder - lowerBorder;
    }

    public Vehicle[][] getStreet() {
        return street;
    }
}
