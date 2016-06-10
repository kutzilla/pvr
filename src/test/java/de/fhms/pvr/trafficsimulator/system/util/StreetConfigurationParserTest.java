package de.fhms.pvr.trafficsimulator.system.util;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class StreetConfigurationParserTest {

    private static final String TEST_SINGLE_TRACK_FILE_NAME = "test_street_single_track.csv";

    private static final String TEST_DOUBLE_TRACK_FILE_NAME = "test_street_double_track.csv";

    private File testStreetConfigurationFile;

    @Test
    public void testParseStreetConfigurationSingleTrack() throws IOException {
        testStreetConfigurationFile = new File(getClass().getClassLoader()
                .getResource(TEST_SINGLE_TRACK_FILE_NAME).getFile());
        Vehicle[][] parsedStreet = StreetConfigurationParser.parseStreetConfigurationFrom(testStreetConfigurationFile);

        Vehicle[][] expectedStreet = new Vehicle[1][10];
        expectedStreet[0][0] = new Vehicle(0);
        expectedStreet[0][1] = new Vehicle(2);
        expectedStreet[0][3] = new Vehicle(3);
        expectedStreet[0][4] = new Vehicle(5);
        expectedStreet[0][7] = new Vehicle(1);
        expectedStreet[0][9] = new Vehicle(4);

        assertArrayEquals(expectedStreet, parsedStreet);
    }

    @Test
    public void testParseStreetConfigurationDoubleTrack() throws IOException {
        testStreetConfigurationFile = new File(getClass().getClassLoader()
                .getResource(TEST_DOUBLE_TRACK_FILE_NAME).getFile());
        Vehicle[][] parsedStreet = StreetConfigurationParser.parseStreetConfigurationFrom(testStreetConfigurationFile);

        Vehicle[][] expectedStreet = new Vehicle[2][10];
        expectedStreet[0][0] = new Vehicle(5);
        expectedStreet[0][2] = new Vehicle(0);
        expectedStreet[0][4] = new Vehicle(3);
        expectedStreet[0][6] = new Vehicle(4);
        expectedStreet[0][8] = new Vehicle(1);
        expectedStreet[1][1] = new Vehicle(2);
        expectedStreet[1][3] = new Vehicle(0);
        expectedStreet[1][5] = new Vehicle(4);
        expectedStreet[1][7] = new Vehicle(3);
        expectedStreet[1][9] = new Vehicle(5);

        assertArrayEquals(expectedStreet, parsedStreet);
    }
}
