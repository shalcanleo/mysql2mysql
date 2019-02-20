package org.ning.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MySqlBean {

    @Value("${source.address}")
    private String sourceAddress;

    @Value("${source.port}")
    private String sourcePort;

    @Value("${source.db}")
    private String sourceDb;

    @Value("${source.user}")
    private String sourceUser;

    @Value("${source.password}")
    private String sourcePassword;

    @Value("${dest.address}")
    private String destAddress;

    @Value("${dest.port}")
    private String destPort;

    @Value("${dest.db}")
    private String destDb;

    @Value("${dest.user}")
    private String destUser;

    @Value("${dest.password}")
    private String destPassword;

    public String toSourceUrl() {
        return "jdbc:mysql://" +
                sourceAddress +
                ":" +
                sourcePort +
                "/" +
                sourceDb +
                "?useUnicode=true&characterEncoding=UTF8";
    }

    public String toDestUrl() {
        return "jdbc:mysql://" +
                destAddress +
                ":" +
                destPort +
                "/" +
                destDb +
                "?useUnicode=true&characterEncoding=UTF8";
    }
}
