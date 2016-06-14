package de.fhms.pvr.trafficsimulator.system.measure;

import org.apache.commons.lang3.time.StopWatch;

public class TimeMeasureController {

    private StopWatch iteration;

    private StopWatch accelerationStopWatch;

    private StopWatch trackSwitchingStopWatch;

    private StopWatch movementStopWatch;

    private StopWatch kappaStopWatch;

    private StopWatch phiStopWatch;

    private StopWatch sigmaStopWatch;

    public TimeMeasureController() {
        this.iteration = new StopWatch();
        this.accelerationStopWatch = new StopWatch();
        this.trackSwitchingStopWatch = new StopWatch();
        this.movementStopWatch = new StopWatch();
        this.kappaStopWatch = new StopWatch();
        this.phiStopWatch = new StopWatch();
        this.sigmaStopWatch = new StopWatch();
    }

    public void startOrResume(TimeMeasureType type) {
        StopWatch watch = getStopWatchByType(type);
        if (!watch.isStarted()) {
            watch.start();
        } else {
            watch.resume();
        }
    }

    public void suspend(TimeMeasureType type) {
        StopWatch watch = getStopWatchByType(type);
        watch.suspend();
    }

    public long getMeasuredTimeFor(TimeMeasureType type) {
        StopWatch watch = getStopWatchByType(type);
        return watch.getNanoTime() / 1000000;
    }

    private StopWatch getStopWatchByType(TimeMeasureType type) {
        switch(type) {
            case ITERATION:
                return iteration;
            case DRIVE_ACTION:
                return accelerationStopWatch;
            case MOVEMENT:
                return movementStopWatch;
            case TRACK_SWITCHING:
                return trackSwitchingStopWatch;
            case KAPPA:
                return kappaStopWatch;
            case PHI:
                return phiStopWatch;
            case SIGMA:
                return sigmaStopWatch;
            default:
                return null;
        }
    }

}
