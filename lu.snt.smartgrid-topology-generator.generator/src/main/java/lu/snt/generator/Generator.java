package lu.snt.generator;

import smartgridcomm.*;
import smartgridcomm.impl.DefaultSmartgridcommFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

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
            e.printStackTrace();
        }

        int nb_concentrators = Integer.valueOf(properties.getProperty("nb_con")).intValue();
        int min_nb_sm = Integer.valueOf(properties.getProperty("min_nb_sm")).intValue();
        int max_nb_sm = Integer.valueOf(properties.getProperty("max_nb_sm")).intValue();
        int percentage_wm = Integer.valueOf(properties.getProperty("percentage_wm")).intValue();
        int percentage_gm = Integer.valueOf(properties.getProperty("percentage_gm")).intValue();

        SmartgridcommFactory factory = new DefaultSmartgridcommFactory();

        // central system
        final CentralSystem cs = factory.createCentralSystem();
        cs.setId("central_system");

        // data concentrators
        for (int i = 0; i < nb_concentrators; i++) {
            final Concentrator c = factory.createConcentrator();
            c.setId("concentrator_" + i);
            cs.addRegisteredEntities(c);

            // smart meters
            Random r = new Random();
            int nb_sm = r.nextInt(max_nb_sm - min_nb_sm) + min_nb_sm;
            for (int j = 0; j < nb_sm; j++) {

                final SmartMeter sm = factory.createSmartMeter();
                sm.setId("smart_meter_" + i + "_" + j);
                c.addRegisteredEntities(sm);

                // water meters
                if (j % percentage_wm == 0) {
                    final WaterMeter wm = factory.createWaterMeter();
                    wm.setId("water_meter_" + i + "_" + j);
                }

                // gas meters
                if (j % percentage_gm == 0) {
                    final GasMeter gm = factory.createGasMeter();
                    gm.setId("gas_meter_" + i + "_" + j);
                }

            }
        }

    }

    private void loadProperties() {

    }

}
