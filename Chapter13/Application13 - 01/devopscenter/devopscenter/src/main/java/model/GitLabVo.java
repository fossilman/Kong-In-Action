package model;

import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：gitlab返回对象
 * @date ：Created in 2019-09-17 10:57
 */
@Data
public class GitLabVo {


    /**
     * 应用id
     */
    private Integer applicationId;
    private List<GitlabInfo> gitlabInfo;


    @Data
    public static class GitlabInfo {
        /**
         * 版本号
         */
        private String version;

        /**
         * 头部描述
         */
        private String headDesc;

        /**
         * 描述
         */
        private String bodyDesc;

        /**
         * 作者
         */
        private String author;
    }


}
