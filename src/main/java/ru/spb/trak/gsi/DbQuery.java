package ru.spb.trak.gsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * Created by trak on 25.01.2017.
 */
public class DbQuery extends  Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(DbQuery.class);

    public DbQuery(String fileName, Properties properties) {
        super(fileName, properties);
    }
//    @Override
//    public String tryQuery() throws Exception {
//        LOGGER.error("QUERY ERROR! File: [{}] Table:[{}] DbQuery not supported yet",fileName,tableName);
//        throw new Exception("DbQuery пока не поддерживается");
//
//    }
}
