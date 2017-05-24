package ru.spb.trak.gsi;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by trak on 25.01.2017.
 */
public abstract class Query {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Query.class);
    protected String fileName;

    protected HashMap<String, String> vars = new HashMap<>();
    protected String tableName;
    protected String originalQuery;
    protected String changedQuery;
    protected String queryType;
    protected Connection connection;
    protected long bindsQuantity;
    protected Properties properties;
    protected Pattern bindPattern = Pattern.compile("(?<!:):(?:(?!(MI)|(SS))[a-zA-Z0-9_]+)",Pattern.CASE_INSENSITIVE);


    public Query(String fileName, Properties properties) {
        this.fileName = fileName;
        this.properties = properties;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public HashMap<String, String> getVars() {
        return vars;
    }

    public void setVars(HashMap<String, String> vars) {
        this.vars = vars;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public String getChangedQuery() {
        return changedQuery;
    }

    public void prepareQueryText() {
        LOGGER.debug("XX prepareQuery {}", originalQuery);
        if (null != originalQuery) {
            changedQuery = new String(originalQuery);
            for (Map.Entry entry : vars.entrySet()) {
                LOGGER.debug("var[{}]:{} ", entry.getKey(), entry.getValue());
                changedQuery = changedQuery.replaceAll("\\{" + entry.getKey() + "\\}", (String) entry.getValue());
            }
            // Care about :BIND's
//            bindsQuantity = changedQuery.codePoints().filter(ch -> ch == ':').count();
            bindsQuantity = 0;
            Matcher bindMatcher = bindPattern.matcher(changedQuery);
            while(bindMatcher.find())
                bindsQuantity++;

            changedQuery = bindMatcher.replaceAll("?");
            LOGGER.debug("Found {} binds!", bindsQuantity);

//            if (0 < bindsQuantity) {
////                Pattern bindPattern = Pattern.compile(":[^\\s]*");
//                bindMatcher = bindPattern.matcher(changedQuery);
//                changedQuery = bindMatcher.replaceAll("?");
//                LOGGER.debug("Found {} binds!", bindsQuantity);
//            }


            changedQuery = changedQuery.replaceAll("\\{FILTER}","1=1");
            LOGGER.debug("Changed query: {}", changedQuery);
        }
    }

    public String tryQuery() throws Exception {
        String result = "OK";
        if (queryType.equals("select") || queryType.equals("insert")) {
            Boolean gotResult = false;
            do {
                reopenConnection();
                try (PreparedStatement preparedStatement = connection.prepareStatement(changedQuery)) {
                    for (int i = 1; i <= bindsQuantity; i++) {
                        preparedStatement.setInt(i, 0);
                    }
                    preparedStatement.executeQuery();
                    LOGGER.info("QUERY OK! File: [{}] Table:[{}]", fileName, tableName);
                    gotResult = true;
                } catch (SQLException e) {
                    LOGGER.error("QUERY ERROR! File: [{}] Type: {} Table:[{}] Query [{}]; Error:[{}]", fileName, queryType, tableName, changedQuery, e.getMessage());
//            LOGGER.error("QUERY ERROR! File: [{}] Table:[{}] ",fileName,tableName);
                    throw e;
                } catch (Exception commonException) {
                    LOGGER.error("COMMON QUERY ERROR! File: [{}] Type: {} Table:[{}] Query [{}]; Error:[{}]", fileName, queryType, tableName, changedQuery, commonException.getMessage());
                    throw commonException;
                }
            } while (!gotResult);
        } else {
            LOGGER.info("QUERY IGNORE! File: [{}]  Type: {} Table:[{}]", fileName, queryType, tableName);
            result = "IGNORE";
        }
        return result;
    }

    private void reopenConnection() {
        Boolean gotConnection = false;
        do {
            try {
                connection.close();
            } catch (Exception ignored) {
            }

            try {
                Class.forName("org.postgresql.Driver");
                Properties connectionProperty = new Properties();
                connectionProperty.setProperty("user", properties.getProperty("db.user"));
                connectionProperty.setProperty("password", properties.getProperty("db.password"));
                connectionProperty.setProperty("loglevel", "1");
                connectionProperty.setProperty("socketTimeout", properties.getProperty("db.socketTimeout"));
                connectionProperty.setProperty("connectTimeout", properties.getProperty("db.connectTimeout"));

                connection = DriverManager.getConnection(
                        "jdbc:postgresql://" + properties.getProperty("db.host") + ":" + properties.getProperty("db.port") + "/" + properties.getProperty("db.name"), connectionProperty);
                LOGGER.info("Conection info: {}", connection.getCatalog());
                gotConnection = true;
            } catch (Exception ex) {
                LOGGER.error("Got while open postgresql connection error {}", ex.getMessage());
                gotConnection = false;
            }
        } while (!gotConnection);

    }
}
