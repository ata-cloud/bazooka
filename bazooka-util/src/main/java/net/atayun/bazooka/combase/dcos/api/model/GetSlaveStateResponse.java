package net.atayun.bazooka.combase.dcos.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-09-18
 */
@Data
public class GetSlaveStateResponse {
    private String version;
    private String git_sha;
    private String build_date;
    private String build_user;
    private String id;
    private String pid;
    private String hostname;
    private String master_hostname;
    private List<Framework> frameworks;

    @Data
    public class Framework {
        private String id;
        private String name;
        private String user;
        private Double failover_timeout;
        private Boolean checkpoint;
        private String hostname;
        private String principal;
        private String role;
        private List<Executor> executors;

        @Data
        public class Executor {
            private String id;
            private String name;
            private String source;
            private String container;
            private String directory;
        }
    }
}
