package ru.spb.trak.gsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * Created by trak on 25.01.2017.
 */
public class GeoQuery extends  Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoQuery.class);

    public GeoQuery(String fileName, Properties properties) {
        super(fileName, properties);
    }
}
