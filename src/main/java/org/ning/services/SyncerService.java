package org.ning.services;

import org.ning.configure.DestMySqlConnector;
import org.ning.configure.SourceMySqlConnector;
import org.ning.constants.CreateJobResult;
import org.ning.constants.SyncJobStatus;
import org.ning.modules.SyncerRequest;
import org.ning.runners.SyncerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.ning.constants.SyncJobStatus.RUNNING;

@Service
public class SyncerService {
    private Logger logger = LoggerFactory.getLogger("Syncer");

    private static Map<String, SyncJobStatus> jobMap = new HashMap<>();

    public static void setJobStatus(String table, SyncJobStatus status) {
        jobMap.put(table, status);
    }

    @Autowired
    private SourceMySqlConnector sourceMySqlConnector;

    @Autowired
    private DestMySqlConnector destMySqlConnector;

    public CreateJobResult createSyncJob(SyncerRequest request) {
        String tableName = request.getTableName();
        if (jobMap.containsKey(tableName) && jobMap.get(tableName) == RUNNING) {
            return CreateJobResult.JOB_RUNNING;
        }
        SyncerRunner runner = new SyncerRunner(sourceMySqlConnector.getPool(), destMySqlConnector.getPool(), request);
        runner.start();
        return CreateJobResult.SUCCESS;
    }

}
