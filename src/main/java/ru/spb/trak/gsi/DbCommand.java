package ru.spb.trak.gsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by trak on 25.01.2017.
 */
public class DbCommand extends  Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(DbCommand.class);

    public DbCommand(String fileName, Properties properties) {
        super(fileName, properties);
    }

//    @Override
//    public String tryQuery() throws Exception  {
//        LOGGER.error("QUERY ERROR! File: [{}] Table:[{}] DbCommand not supported yet",fileName,tableName);
//        throw new Exception("DbCommand пока не поддерживается");
//    }

}
