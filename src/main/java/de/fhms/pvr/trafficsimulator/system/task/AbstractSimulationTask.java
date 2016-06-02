package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.TrafficSimulator;
import de.fhms.pvr.trafficsimulator.system.Vehicle;

import java.util.concurrent.Callable;

public abstract class AbstractSimulationTask implements Callable<Boolean> {

    protected int lowerBound;

    protected int upperBound;

    protected TrafficSimulator trafficSimulator;

    public AbstractSimulationTask(TrafficSimulator trafficSimulator, int lowerBound, int upperBound) {
        this.trafficSimulator = trafficSimulator;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public TrafficSimulator getTrafficSimulator() {
        return trafficSimulator;
    }
}
