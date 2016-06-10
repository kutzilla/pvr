package de.fhms.pvr.trafficsimulator.system.util;

import de.fhms.pvr.trafficsimulator.system.Vehicle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class StreetConfigurationParser {

    private static final Logger LOG = LogManager.getLogger(StreetConfigurationParser.class);

    public static Vehicle[][] parseStreetConfigurationFrom(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        CSVParser parser = new CSVParser(fileReader, CSVFormat.EXCEL);
        List<CSVRecord> records = parser.getRecords();
        LOG.debug("Datei " + file.getName() + " verfügt über " + records.size() + " Zeile(n)");

        Vehicle[][] parsedStreet = new Vehicle[records.size()][];

        String tmpValue;
        for (int i = 0; i < records.size(); i++) {
            LOG.debug("Zeile " + (i + 1) + " hat " + records.get(i).size() + " Werte");
            parsedStreet[i] = new Vehicle[records.get(i).size()];
            for (int j = 0; j < records.get(i).size(); j++) {
                tmpValue = records.get(i).get(j);
                if (!tmpValue.equals("")) {
                    int vehicleSpeed = Integer.valueOf(tmpValue);
                    parsedStreet[i][j] = new Vehicle(vehicleSpeed);
                    LOG.debug("Fahrzeug mit der Geschwindigkeit " + vehicleSpeed
                            + " für die Position " + j + ":" + i + " eingelesen");
                }
            }
        }

        return parsedStreet;
    }
}
