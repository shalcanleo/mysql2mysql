package org.ning;
import org.junit.Test;
import org.ning.modules.SyncerRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestTest {

    @Test
    public void tesSyncerRequestSql() {
        SyncerRequest request = new SyncerRequest();
        Map<String, Object> gte = new HashMap<>();
        gte.put("create_time", "2019-01-01 00:00:00");
        Map<String, Object> lte = new HashMap<>();
        lte.put("create_time", "2019-01-03 00:00:00");
        request.setTableName("test");
        request.setGte(gte);
        request.setLte(lte);
        System.out.println(request.getSql());
        System.out.println(request.getCountSql());
    }
}
