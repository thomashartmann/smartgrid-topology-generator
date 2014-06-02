package lu.snt.generator;

import org.kevoree.log.Log;
import smartgridcomm.*;
import smartgridcomm.impl.DefaultSmartgridcommFactory;
import smartgridcomm.serializer.JSONModelSerializer;
import smartgridcomm.serializer.XMIModelSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by thomas on 5/12/14.
 */
public class Generator {

    public static void main(String[] args) {
        String filename = "generator.properties";
        Properties properties = new Properties();

        InputStream input;
        input = Generator.class.getClassLoader().getResourceAsStream("generator.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            Log.error("Cannot load properties file");
        }

        int nb_con = Integer.valueOf(properties.getProperty("nb_con")).intValue();
        int min_nb_sm = Integer.valueOf(properties.getProperty("min_nb_sm")).intValue();
        int max_nb_sm = Integer.valueOf(properties.getProperty("max_nb_sm")).intValue();
        int percentage_wm = Integer.valueOf(properties.getProperty("percentage_wm")).intValue();
        int percentage_gm = Integer.valueOf(properties.getProperty("percentage_gm")).intValue();
        int min_nb_hops = Integer.valueOf(properties.getProperty("min_nb_hops")).intValue();
        int max_nb_hops = Integer.valueOf(properties.getProperty("max_nb_hops")).intValue();

        Log.info("Reading Properties...");
        Log.info("nb_con: " + nb_con);
        Log.info("min_nb_sm: " + min_nb_sm);
        Log.info("max_nb_sm: " + max_nb_sm);
        Log.info("percentage_wm: " + percentage_wm);
        Log.info("percentage_gm: " + percentage_gm);
        Log.info("min_nb_hops: " + min_nb_hops);
        Log.info("max_nb_hops: " + max_nb_hops);


        Log.info("Generating...");
        SmartgridcommFactory factory = new DefaultSmartgridcommFactory();

        final SmartGridModel grid = factory.createSmartGridModel();

        // central system
        final CentralSystem cs = factory.createCentralSystem();
        cs.setId("central_system");
        grid.addEntities(cs);

        // data concentrators
        for (int i = 0; i < nb_con; i++) {
            final Concentrator c = factory.createConcentrator();
            c.setId("concentrator_" + i);
            grid.addEntities(c);
            cs.addRegisteredEntities(c);

            // smart meters
            final Map<Integer, List<SmartMeter>> smMap = new HashMap<Integer, List<SmartMeter>>();
            int nb_sm = new Random().nextInt(max_nb_sm - min_nb_sm) + min_nb_sm;
            for (int j = 0; j < nb_sm; j++) {

                final SmartMeter sm = factory.createSmartMeter();
                sm.setId("smart_meter_" + i + "_" + j);
                grid.addEntities(sm);

                int nb_hops = new Random().nextInt(max_nb_hops - min_nb_hops) + min_nb_hops;
                List<SmartMeter> sms = smMap.getOrDefault(nb_hops, new ArrayList<SmartMeter>());
                sms.add(sm);
                smMap.put(nb_hops, sms);

                // water meters
                if (j % percentage_wm == 0) {
                    final WaterMeter wm = factory.createWaterMeter();
                    wm.setId("water_meter_" + i + "_" + j);
                    grid.addEntities(wm);
                    wm.setRegisterBy(sm);
                }

                // gas meters
                if (j % percentage_gm == 0) {
                    final GasMeter gm = factory.createGasMeter();
                    gm.setId("gas_meter_" + i + "_" + j);
                    grid.addEntities(gm);
                    gm.setRegisterBy(sm);
                }
            }

            // connect smart meter tree
            connectTopology(c, smMap);

        }

        // write the topology file
        String outputFormat = properties.getProperty("output_format");
        if (outputFormat.equalsIgnoreCase("json")) {
            writeJSON(grid);
        } else if (outputFormat.equalsIgnoreCase("xmi")) {
            writeXMI(grid);
        } else {
            Log.error("Cannot write topology: unsupported format " + outputFormat);

        }

    }

    private static void connectTopology(Concentrator c, Map<Integer, List<SmartMeter>> smMap) {
        // directly connected smart meters?
        if (smMap.get(1) == null) {
            int key = smMap.keySet().iterator().next();
            List<SmartMeter> values = smMap.get(key);
            smMap.remove(key);
            smMap.put(1, values);
        }

        // sort keySet
        List<Integer> keySet = new ArrayList<Integer>(smMap.keySet());
        Collections.sort(keySet);

        for (Integer key : smMap.keySet()) {
            if (key.intValue() == 1) {
                // register at concentrator
                for (SmartMeter sm : smMap.get(key)) {
                    sm.setRegisterBy(c);
                }

            } else {
                // register at repeater
                int keyPos = key - 1;
                List<SmartMeter> sms = smMap.get(keyPos);
                while (sms == null && keyPos >= 0) {
                    sms = smMap.get(keyPos);
                    keyPos -= 1;
                }

                for (SmartMeter sm : smMap.get(key)) {
                    int randomIndex = new Random().nextInt(sms.size());
                    final SmartMeter repeater = sms.get(randomIndex);
                    sm.setRegisterBy(repeater);
                }

            }

        }
    }

    private static void writeXMI(SmartGridModel grid) {
        XMIModelSerializer saver = new XMIModelSerializer();
        File output = null;
        try {
            output = File.createTempFile("A" + Math.random(), "A" + Math.random() + ".xmi");

            FileOutputStream outputStream = new FileOutputStream(output);
            saver.serializeToStream(grid, outputStream);
            outputStream.close();

            Log.info("Writing...");
            Log.info(output.getAbsolutePath());

        } catch (IOException e) {
            Log.error("Cannot write topology file: " + e.getMessage());
        }
    }

    private static void writeJSON(SmartGridModel grid) {
        JSONModelSerializer saver = new JSONModelSerializer();
        File output = null;
        try {
            output = File.createTempFile("A" + Math.random(), "A" + Math.random() + ".json");

            FileOutputStream outputStream = new FileOutputStream(output);
            saver.serializeToStream(grid, outputStream);
            outputStream.close();

            Log.info("Writing...");
            Log.info(output.getAbsolutePath());

        } catch (IOException e) {
            Log.error("Cannot write topology file: " + e.getMessage());
        }

    }

}
