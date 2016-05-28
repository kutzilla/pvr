package de.fhms.pvr.trafficsimulator.system.measure;

import org.junit.Before;
import org.junit.Test;
import static de.fhms.pvr.trafficsimulator.system.measure.TimeMeasureType.*;
import static org.junit.Assert.assertTrue;

public class TimeMeasureControllerTest {

    private TimeMeasureController timeMeasureController;

    @Before
    public void setUp() {
        this.timeMeasureController = new TimeMeasureController();
    }

    @Test
    public void testAllStopWatchesWorkingProperly() {
        this.timeMeasureController.startOrResume(ITERATION);
        this.timeMeasureController.startOrResume(TRACK_SWITCHING);
        this.timeMeasureController.startOrResume(ACCELERATION);
        this.timeMeasureController.startOrResume(DECELERATION);
        this.timeMeasureController.startOrResume(MOVEMENT);
        this.timeMeasureController.startOrResume(DAWDLING);

        this.timeMeasureController.suspend(ITERATION);
        this.timeMeasureController.suspend(TRACK_SWITCHING);
        this.timeMeasureController.suspend(ACCELERATION);
        this.timeMeasureController.suspend(DECELERATION);
        this.timeMeasureController.suspend(MOVEMENT);
        this.timeMeasureController.suspend(DAWDLING);
    }

    @Test
    public void testGetMeasuredTime() throws Exception {
        long beforeIterationTime = this.timeMeasureController.getMeasuredTimeFor(ITERATION);
        this.timeMeasureController.startOrResume(ITERATION);
        Thread.sleep(1);
        this.timeMeasureController.suspend(ITERATION);
        this.timeMeasureController.startOrResume(ITERATION);
        Thread.sleep(1);
        long afterIterationTime = this.timeMeasureController.getMeasuredTimeFor(ITERATION);
        assertTrue(afterIterationTime > beforeIterationTime);
    }
}
