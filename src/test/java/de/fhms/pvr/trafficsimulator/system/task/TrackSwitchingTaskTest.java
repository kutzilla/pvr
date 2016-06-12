package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TrackSwitchingTaskTest {

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    private TrackSwitchingTask trackSwitchingTask;

    @Test
    public void testSimulateTrackSwitchingIsPossible() {
        testStreet = new Vehicle[2][20];
        testStreet[0][5] = new Vehicle(5);
        testStreet[0][11] = new Vehicle(3);

        expectedStreet = new Vehicle[2][20];
        expectedStreet[1][5] = new Vehicle(5);
        expectedStreet[0][11] = new Vehicle(3);

        trackSwitchingTask = new TrackSwitchingTask(testStreet, 0, testStreet[0].length - 1, 1.0);
        trackSwitchingTask.call();
        assertArrayEquals(expectedStreet, trackSwitchingTask.street);

    }

    @Test
    public void testSimulateTrackSwitchingIsNotPossibleInBack() {
        testStreet = new Vehicle[2][20];
        testStreet[0][5] = new Vehicle(5);
        testStreet[0][8] = new Vehicle(3);
        testStreet[1][1] = new Vehicle(2);

        expectedStreet = new Vehicle[2][20];
        expectedStreet[0][5] = new Vehicle(5);
        expectedStreet[0][8] = new Vehicle(3);
        expectedStreet[1][1] = new Vehicle(2);


        trackSwitchingTask = new TrackSwitchingTask(testStreet, 0, testStreet[0].length - 1, 1.0);
        trackSwitchingTask.call();
        assertArrayEquals(expectedStreet, trackSwitchingTask.street);
    }

    @Test
    public void testSimulateTrackSwitchingIsNotPossibleInFront() {
        testStreet = new Vehicle[2][20];
        testStreet[0][5] = new Vehicle(5);
        testStreet[0][8] = new Vehicle(3);
        testStreet[1][11] = new Vehicle(2);

        expectedStreet = new Vehicle[2][20];
        expectedStreet[0][5] = new Vehicle(5);
        expectedStreet[0][8] = new Vehicle(3);
        expectedStreet[1][11] = new Vehicle(2);

        trackSwitchingTask = new TrackSwitchingTask(testStreet, 0, testStreet[0].length - 1, 1.0);
        trackSwitchingTask.call();
        assertArrayEquals(expectedStreet, trackSwitchingTask.street);
    }

    @Test
    public void testSimulateTrackSwitchingIsPossibleWithThreeTracks() {
        testStreet = new Vehicle[3][20];
        testStreet[2][2] = new Vehicle(5);
        testStreet[2][5] = new Vehicle(3);
        testStreet[0][5] = new Vehicle(1);

        expectedStreet = new Vehicle[3][20];
        expectedStreet[1][2] = new Vehicle(5);
        expectedStreet[2][5] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(1);

        trackSwitchingTask = new TrackSwitchingTask(testStreet, 0, testStreet[0].length - 1, 1.0);
        trackSwitchingTask.call();
        assertArrayEquals(expectedStreet, trackSwitchingTask.street);
    }
}
