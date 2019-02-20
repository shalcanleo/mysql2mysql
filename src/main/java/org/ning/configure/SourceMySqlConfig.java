package org.ning.configure;

import org.ning.utils.MySqlPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourceMySqlConfig {
    @Autowired
    MySqlBean connectBean;

    @Bean
    public SourceMySqlConnector getSourceConnector() throws Exception {
        MySqlPool pool = MySqlPool.apply(
                connectBean.getSourceAddress(),
                connectBean.getSourcePort(),
                connectBean.getSourceDb(),
                connectBean.getSourceUser(),
                connectBean.getSourcePassword()
        );
        return new SourceMySqlConnector(pool);
    }

    @Bean
    public DestMySqlConnector getDestConnector() throws Exception {
        MySqlPool pool = MySqlPool.apply(
                connectBean.getDestAddress(),
                connectBean.getDestPort(),
                connectBean.getDestDb(),
                connectBean.getDestUser(),
                connectBean.getDestPassword()
        );
        return new DestMySqlConnector(pool);
    }

}
