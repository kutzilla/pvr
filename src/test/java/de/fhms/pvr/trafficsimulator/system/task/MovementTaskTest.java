package de.fhms.pvr.trafficsimulator.system.task;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class MovementTaskTest {

    private MovementTask movementTask;

    private Vehicle[][] testStreet;

    private Vehicle[][] expectedStreet;

    @Test
    public void testSingleTrack() {
        testStreet = new Vehicle[1][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(4);
        testStreet[0][9] = new Vehicle(2);

        expectedStreet = new Vehicle[1][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][8] = new Vehicle(4);
        expectedStreet[0][1] = new Vehicle(2);

        movementTask = new MovementTask(testStreet, 0, testStreet[0].length - 1);
        movementTask.call();
        assertArrayEquals(expectedStreet, movementTask.getStreet());
    }

    @Test
    public void testDoubleTrack() {
        testStreet = new Vehicle[2][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(4);
        testStreet[0][9] = new Vehicle(2);
        testStreet[1][3] = new Vehicle(5);
        testStreet[1][9] = new Vehicle(4);

        expectedStreet = new Vehicle[2][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][8] = new Vehicle(4);
        expectedStreet[0][1] = new Vehicle(2);
        expectedStreet[1][8] = new Vehicle(5);
        expectedStreet[1][3] = new Vehicle(4);

        movementTask = new MovementTask(testStreet, 0, testStreet[0].length - 1);
        movementTask.call();
        assertArrayEquals(expectedStreet, movementTask.getStreet());
    }

    @Test
    public void testTripleTrack() {
        testStreet = new Vehicle[3][10];
        testStreet[0][0] = new Vehicle(3);
        testStreet[0][4] = new Vehicle(4);
        testStreet[0][9] = new Vehicle(2);
        testStreet[1][3] = new Vehicle(5);
        testStreet[1][9] = new Vehicle(4);
        testStreet[2][0] = new Vehicle(5);
        testStreet[2][6] = new Vehicle(1);
        testStreet[2][8] = new Vehicle(1);

        expectedStreet = new Vehicle[3][10];
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][8] = new Vehicle(4);
        expectedStreet[0][1] = new Vehicle(2);
        expectedStreet[1][8] = new Vehicle(5);
        expectedStreet[1][3] = new Vehicle(4);
        expectedStreet[2][5] = new Vehicle(5);
        expectedStreet[2][7] = new Vehicle(1);
        expectedStreet[2][9] = new Vehicle(1);

        movementTask = new MovementTask(testStreet, 0, testStreet[0].length - 1);
        movementTask.call();
        assertArrayEquals(expectedStreet, movementTask.getStreet());
    }
}
