package org.ning.configure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ning.utils.MySqlPool;

@Data
@AllArgsConstructor
public class SourceMySqlConnector {
    private MySqlPool pool;
}
