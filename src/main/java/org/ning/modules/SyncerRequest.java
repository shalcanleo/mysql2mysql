package org.ning.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncerRequest {
    private String tableName;
    private Map<String, Object> gte;
    private Map<String, Object> lte;

    public String getSql() {
        String sql = "select * from " + tableName;
        if (gte != null && !gte.isEmpty()) {
            sql = sql + " where ";
            String first = gte.keySet().iterator().next();
            Object value = gte.get(first);
            sql = sql + first + " >= '" + value.toString().replaceAll("'","''") + "'";
        }
        if (lte != null && !lte.isEmpty()) {
            if (sql.contains("where")) sql = sql + " and ";
            else sql = sql + " where ";
            String first = lte.keySet().iterator().next();
            Object value = lte.get(first);
            sql = sql + first + " <= '" + value.toString().replaceAll("'","''") + "'";
        }
        return sql;
    }

    public String getCountSql() {
        String sql = getSql().replace("*", "count(*) as countVal");
        return sql;
    }
}
