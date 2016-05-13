package de.fhms.pvr.vekehrssimulation.mehrspurig.parallel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Matthias on 22.04.16.
 */
public class StreetTest {

    private Street einspurigerStreet;

    private Street zweiSpurigerStreet;

    private Street mehrSpurigerStreet;

    @Before
    public void setUp() {
        einspurigerStreet = new Street(1, 20, 0.5);
        zweiSpurigerStreet = new Street(2, 20, 0.5);
        mehrSpurigerStreet = new Street(3, 20, 0.5);
    }

    @Test
    public void testEinspurigBremsen() {
        assertTrue(false);
    }

    @Test
    public void testZweispurigBremsen() {
        assertTrue(false);
    }

    @Test
    public void testMehrspurigBremsen() {
        assertTrue(false);
    }

    @Test
    public void testEinspurigBeschleunigen() {
        assertTrue(false);
    }

    @Test
    public void testZweispurigBeschleunigen() {
        assertTrue(false);
    }

    @Test
    public void testMehrspurigBeschleunigen() {
        assertTrue(false);
    }

    @Test
    public void testEinspurigTroedeln() {
        assertTrue(false);
    }

    @Test
    public void testZweispurigTroedeln() {
        assertTrue(false);
    }

    @Test
    public void testMehrspurigTroedeln() {
        assertTrue(false);
    }

    @Test
    public void testEinspurigFortbewegen() {
        assertTrue(false);
    }

    @Test
    public void testZweispurigFortbewegen() {
        assertTrue(false);
    }

    @Test
    public void testMehrspurigFortbewegen() {
        assertTrue(false);
    }

    @Test
    public void testZweispurigUeberholen() {
        assertTrue(false);
    }

    @Test
    public void testMehrspurigUeberholen() {
        assertTrue(false);
    }
}
