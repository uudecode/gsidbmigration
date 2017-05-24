package ru.spb.trak.gsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by trak on 25.01.2017.
 */
public class GeoCommand extends  Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoCommand.class);

    public GeoCommand(String fileName, Properties properties) {
        super(fileName, properties);
    }

}
