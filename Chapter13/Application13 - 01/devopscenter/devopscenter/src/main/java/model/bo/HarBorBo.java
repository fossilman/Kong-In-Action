package model.bo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：
 * @date ：Created in 2019-11-02 11:19
 */
@Data
public class HarBorBo {


    /**
     * digest : sha256:992c89db194095eb1b10fb1af03eb314afaf710c6c6718d99f81a03adca0e5d0
     * name : v1
     * size : 707930377
     * architecture : amd64
     * os : linux
     * os.version :
     * docker_version : 18.09.7
     * author :
     * created : 2019-07-10T07:45:22.916427705Z
     * config : {"labels":{"build-date":"20170406","license":"GPLv2","name":"CentOS Base Image","vendor":"CentOS"}}
     * signature : null
     * labels : []
     * push_time : 2019-11-01T03:55:46.608738Z
     * pull_time : 0001-01-01T00:00:00Z
     */

    private String digest;
    private String name;
    private int size;
    private String architecture;
    private String os;
    private String docker_version;
    private String author;
    private String created;
    private ConfigBean config;
    private Object signature;
    private String push_time;
    private String pull_time;
    private List<?> labels;

    @Data
    public static class ConfigBean {
        /**
         * labels : {"build-date":"20170406","license":"GPLv2","name":"CentOS Base Image","vendor":"CentOS"}
         */

        private LabelsBean labels;


        @Data
        public static class LabelsBean {
            /**
             * build-date : 20170406
             * license : GPLv2
             * name : CentOS Base Image
             * vendor : CentOS
             */

            @SerializedName("build-date")
            private String builddate;
            private String license;
            private String name;
            private String vendor;

        }
    }
}
