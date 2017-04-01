package ru.spb.trak.gsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by trak on 25.01.2017.
 */
public class XmlQuery extends  Query{
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlQuery.class);

    public XmlQuery(String fileName, Properties properties) {
        super(fileName, properties);
    }


    @Override
    public String tryQuery() {
        LOGGER.info("QUERY OK! File: [{}] Table:[{}] Reason we don't give a mind about xmlQuery.",fileName,tableName);
        return "IGNORE";
    }
}
