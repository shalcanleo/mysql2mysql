package org.ning.runners;

import org.ning.constants.SyncJobStatus;
import org.ning.modules.SyncerRequest;
import org.ning.services.SyncerService;
import org.ning.utils.MySqlPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SyncerRunner extends Thread {
    private Logger logger = LoggerFactory.getLogger("Syncer");
    private MySqlPool sourcePool;
    private MySqlPool destPool;
    private SyncerRequest request;


    public SyncerRunner(MySqlPool sourcePool, MySqlPool destPool, SyncerRequest request) {
        this.sourcePool = sourcePool;
        this.destPool = destPool;
        this.request = request;
    }

    @Override
    public void run() {
        String tableNam = request.getTableName();
        SyncerService.setJobStatus(tableNam, SyncJobStatus.RUNNING);
        try {
            if (!tableExist(sourcePool)) {
                logger.error("Table " + tableNam + " not exist in source.");
                SyncerService.setJobStatus(tableNam, SyncJobStatus.FINISH);
            }  else {
                if (!tableExist(destPool)) {
                    if (!createTableInDest(getCreateTableStatmentFromSource())) {
                        logger.error("Cannot create table " + tableNam + " in dest.");
                        SyncerService.setJobStatus(tableNam, SyncJobStatus.FINISH);
                        return;
                    }
                }
                travleSql();
                logger.info("Syncer: " + tableNam + " is Done!");
                SyncerService.setJobStatus(tableNam, SyncJobStatus.FINISH);
            }
        } catch (SQLException ex) {
            logger.error("Error when sync table: " + tableNam + " " + ex.getMessage(), ex);
        }
    }

    private void travleSql() throws SQLException {
        String sql = request.getSql();
        Long count = getCountOfSql();
        logger.info("Count of rows: " + count);
        if (count == 0L) {
            logger.error("Count of sql is 0. ");
            return;
        }
        Long doCount = 0L;
        Connection connEx = sourcePool.getConnection();
        Statement statement = connEx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(Integer.MIN_VALUE);
        try {
            ResultSet re = statement.executeQuery(sql);
            while (re.next()) {
                insertRecordIntoDest(getMapFromRes(re));
                doCount += 1;
                if (doCount % 1000 == 0) {
                    logger.info(request.getTableName() + " Process: " + (doCount * 1.0 / count * 100) + "%");
                }
            }
        } catch (SQLException ex) {
            logger.error("Error when travel sql of table: " + request.getTableName(), ex);
        } finally {
            statement.close();
            connEx.close();
        }
    }

    private Long getCountOfSql() throws SQLException{
        Long res = 0L;
        Connection connection = sourcePool.getConnection();
        try {
            ResultSet re = connection.createStatement().executeQuery(request.getCountSql());
            if (re.next()) res = re.getLong("countVal");
        } catch (SQLException ex) {
            logger.error("Error when get count of table: " + request.getTableName(), ex);
        } finally {
            connection.close();
        }
        return res;
    }

    private Map<String, Object> getMapFromRes(ResultSet resultSet) throws SQLException {
        Map<String, Object> res = new HashMap<>();
        if (resultSet != null) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                res.put(columnName, value);
            }
        }
        return res;
    }

    private Boolean insertRecordIntoDest(Map<String, Object> values) throws SQLException{
        Boolean res = false;
        String tableName = request.getTableName();
        StringBuilder sql = new StringBuilder("REPLACE INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder();

        for (Iterator<String> iter = values.keySet().iterator(); iter.hasNext();) {
            sql.append(iter.next());
            placeholders.append("?");

            if (iter.hasNext()) {
                sql.append(",");
                placeholders.append(",");
            }
        }
        sql.append(") VALUES (").append(placeholders).append(")");
        Connection connection = destPool.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            int i = 1;

            for (Object value : values.values()) {
                preparedStatement.setObject(i++, value);
            }
            preparedStatement.executeUpdate();
            res = true;
        } catch (SQLException ex ) {
            logger.error("Error when insert record to dest. " + ex.getMessage(), ex);
        } finally {
            connection.close();
        }
        return res;
    }

    private Boolean tableExist(MySqlPool pool) throws SQLException {
        Boolean res = false;
        Connection connection = pool.getConnection();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, request.getTableName(), null);
            if (resultSet.next()) res = true;
        } catch (SQLException ex) {
            logger.error("Error when check table exist: " + request.getTableName(), ex);
        } finally {
            connection.close();
        }
        return res;
    }

    private String getCreateTableStatmentFromSource() throws SQLException {
        String tableName = request.getTableName();
        String sql = "SHOW CREATE TABLE " + tableName;
        String res = "";
        Connection connEx = sourcePool.getConnection();
        try {
            ResultSet resultSet = connEx.createStatement().executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                res = resultSet.getString(2);
            }
        } catch (SQLException ex) {
            logger.error("Error when get create statement of table: " + tableName + ex.getMessage(), ex);
        } finally {
            connEx.close();
        }
        return res;
    }

    private Boolean createTableInDest(String sql) throws SQLException {
        System.out.println("create table sql: " + sql);
        String tableName = request.getTableName();
        Boolean res = false;
        Connection connEx = destPool.getConnection();
        try {
            connEx.createStatement().executeUpdate(sql);
            res = true;
        } catch (SQLException ex) {
            logger.error("Error when create table in dest. " + tableName + " " + ex.getMessage(), ex);
        } finally {
            connEx.close();
        }
        return res;
    }
}
