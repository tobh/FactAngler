package org.mylan.openie.utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Describe class Property here.
 *
 *
 * Created: Fri Nov  9 14:24:10 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Property {
    private static final Logger LOGGER = Logger.getLogger("Property.class");

    private static final Map<String, Properties> instances = new HashMap<String, Properties>();

    public static Properties create(String propertyFileName) {
        if (instances.get(propertyFileName) == null) {
            Properties properties = new Properties();
            try {
                FileInputStream propertiesFile = new FileInputStream(propertyFileName);
                properties.load(propertiesFile);
            } catch (Exception e) {
                LOGGER.fatal("Need properties file: " + propertyFileName);
                e.printStackTrace();
                System.exit(1);
            }
            instances.put(propertyFileName, properties);
        }

        return instances.get(propertyFileName);
    }
}
