package de.fhms.pvr.trafficsimulator.system.measure;

import org.apache.commons.lang3.time.StopWatch;

public class TimeMeasureController {

    private StopWatch iteration;

    private StopWatch accelerationStopWatch;

    private StopWatch decelerationStopWatch;

    private StopWatch trackSwitchingStopWatch;

    private StopWatch movementStopWatch;

    private StopWatch dawdlingStopWatch;

    public TimeMeasureController() {
        this.iteration = new StopWatch();
        this.accelerationStopWatch = new StopWatch();
        this.decelerationStopWatch = new StopWatch();
        this.trackSwitchingStopWatch = new StopWatch();
        this.movementStopWatch = new StopWatch();
        this.dawdlingStopWatch = new StopWatch();
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
            default:
                return null;
        }
    }

}
