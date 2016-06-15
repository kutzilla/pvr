package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class DriveActionTaskTest {

    private DriveActionTask driveActionTask;

    private Vehicle[][] testStreet, expectedStreet;

    @Test
    public void testSimulateAcceleration() {
        testStreet = new Vehicle[1][10];
        testStreet[0][2] = new Vehicle(2);
        testStreet[0][5] = new Vehicle(0);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][2] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(1);
        expectedStreet[0][9] = new Vehicle(5);

        driveActionTask = new DriveActionTask(testStreet, 0, testStreet.length, 0.0, 0.0);
        for (int i = 0; i < testStreet.length; i++) {
            for (int j = 0; j < testStreet[i].length; j++) {
                if (testStreet[i][j] != null) {
                    driveActionTask.simulateAcceleration(testStreet[i][j]);
                }
            }
        }
        assertArrayEquals(expectedStreet, driveActionTask.street);
    }

    @Test
    public void testSimulateDeceleration() {
        testStreet = new Vehicle[1][10];
        testStreet[0][2] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(5);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][2] = new Vehicle(2);
        expectedStreet[0][5] = new Vehicle(3);
        expectedStreet[0][9] = new Vehicle(2);

        driveActionTask = new DriveActionTask(testStreet, 0, testStreet.length, 0.0, 0.0);
        for (int i = 0; i < testStreet.length; i++) {
            for (int j = 0; j < testStreet[i].length; j++) {
                if (testStreet[i][j] != null) {
                    driveActionTask.simulateDeceleration(testStreet[i][j], j, i);
                }
            }
        }
        assertArrayEquals(expectedStreet, driveActionTask.street);
    }

    @Test
    public void testSimulateSlowDawdling() {
        testStreet = new Vehicle[1][10];
        testStreet[0][2] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(1);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][2] = new Vehicle(4);
        expectedStreet[0][5] = new Vehicle(0);
        expectedStreet[0][9] = new Vehicle(5);

        driveActionTask = new DriveActionTask(testStreet, 0, testStreet.length, 0.0, 1.0);
        for (int i = 0; i < testStreet.length; i++) {
            for (int j = 0; j < testStreet[i].length; j++) {
                if (testStreet[i][j] != null) {
                    driveActionTask.simulateDawdling(testStreet[i][j]);
                }
            }
        }
        assertArrayEquals(expectedStreet, driveActionTask.street);
    }

    @Test
    public void testSimulateFastDawdling() {
        testStreet = new Vehicle[1][10];
        testStreet[0][2] = new Vehicle(4);
        testStreet[0][5] = new Vehicle(1);
        testStreet[0][9] = new Vehicle(5);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][2] = new Vehicle(3);
        expectedStreet[0][5] = new Vehicle(1);
        expectedStreet[0][9] = new Vehicle(4);

        driveActionTask = new DriveActionTask(testStreet, 0, testStreet.length, 1.0, 0.0);
        for (int i = 0; i < testStreet.length; i++) {
            for (int j = 0; j < testStreet[i].length; j++) {
                if (testStreet[i][j] != null) {
                    driveActionTask.simulateDawdling(testStreet[i][j]);
                }
            }
        }
        assertArrayEquals(expectedStreet, driveActionTask.street);
    }
}
