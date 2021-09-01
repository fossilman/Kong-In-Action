package model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：返回结果
 * @date ：Created in 2019-09-17 11:19
 */
@Data
public class GitLabProjectsBo {


    /**
     * id : 29
     * description : 发布系统后端
     * name : publish
     * name_with_namespace : OnlineEdu / publish
     * path : publish
     * path_with_namespace : onlineedu/publish
     * created_at : 2019-09-16T03:28:01.118Z
     * default_branch : master
     * tag_list : []
     * ssh_url_to_repo : ssh://git@172.19.234.29:10022/onlineedu/publish.git
     * http_url_to_repo : http://172.19.234.29/onlineedu/publish.git
     * web_url : http://172.19.234.29/onlineedu/publish
     * readme_url : null
     * avatar_url : null
     * star_count : 0
     * forks_count : 0
     * last_activity_at : 2019-09-17T01:55:25.757Z
     * namespace : {"id":7,"name":"OnlineEdu","path":"onlineedu","kind":"group","full_path":"onlineedu","parent_id":null}
     * _links : {"self":"http://172.19.234.29/api/v4/projects/29","issues":"http://172.19.234.29/api/v4/projects/29/issues","merge_requests":"http://172.19.234.29/api/v4/projects/29/merge_requests","repo_branches":"http://172.19.234.29/api/v4/projects/29/repository/branches","labels":"http://172.19.234.29/api/v4/projects/29/labels","events":"http://172.19.234.29/api/v4/projects/29/events","members":"http://172.19.234.29/api/v4/projects/29/members"}
     * archived : false
     * visibility : private
     * resolve_outdated_diff_discussions : false
     * container_registry_enabled : true
     * issues_enabled : true
     * merge_requests_enabled : true
     * wiki_enabled : true
     * jobs_enabled : true
     * snippets_enabled : true
     * shared_runners_enabled : true
     * lfs_enabled : true
     * creator_id : 8
     * import_status : none
     * open_issues_count : 0
     * public_jobs : true
     * ci_config_path : null
     * shared_with_groups : []
     * only_allow_merge_if_pipeline_succeeds : false
     * request_access_enabled : false
     * only_allow_merge_if_all_discussions_are_resolved : false
     * printing_merge_request_link_enabled : true
     * merge_method : merge
     * external_authorization_classification_label : null
     * permissions : {"project_access":null,"group_access":{"access_level":30,"notification_level":3}}
     */

    private int id;
    private String description;
    private String name;
    private String name_with_namespace;
    private String path;
    private String path_with_namespace;
    private String created_at;
    private String default_branch;
    private String ssh_url_to_repo;
    private String http_url_to_repo;
    private String web_url;
    private Object readme_url;
    private Object avatar_url;
    private int star_count;
    private int forks_count;
    private String last_activity_at;
    private NamespaceBean namespace;
    private LinksBean _links;
    private boolean archived;
    private String visibility;
    private boolean resolve_outdated_diff_discussions;
    private boolean container_registry_enabled;
    private boolean issues_enabled;
    private boolean merge_requests_enabled;
    private boolean wiki_enabled;
    private boolean jobs_enabled;
    private boolean snippets_enabled;
    private boolean shared_runners_enabled;
    private boolean lfs_enabled;
    private int creator_id;
    private String import_status;
    private int open_issues_count;
    private boolean public_jobs;
    private Object ci_config_path;
    private boolean only_allow_merge_if_pipeline_succeeds;
    private boolean request_access_enabled;
    private boolean only_allow_merge_if_all_discussions_are_resolved;
    private boolean printing_merge_request_link_enabled;
    private String merge_method;
    private Object external_authorization_classification_label;
    private PermissionsBean permissions;
    private List<?> tag_list;
    private List<?> shared_with_groups;


    @Data
    public static class NamespaceBean {
        /**
         * id : 7
         * name : OnlineEdu
         * path : onlineedu
         * kind : group
         * full_path : onlineedu
         * parent_id : null
         */

        private int id;
        private String name;
        private String path;
        private String kind;
        private String full_path;
        private Object parent_id;


    }

    @Data
    public static class LinksBean {
        /**
         * self : http://172.19.234.29/api/v4/projects/29
         * issues : http://172.19.234.29/api/v4/projects/29/issues
         * merge_requests : http://172.19.234.29/api/v4/projects/29/merge_requests
         * repo_branches : http://172.19.234.29/api/v4/projects/29/repository/branches
         * labels : http://172.19.234.29/api/v4/projects/29/labels
         * events : http://172.19.234.29/api/v4/projects/29/events
         * members : http://172.19.234.29/api/v4/projects/29/members
         */

        private String self;
        private String issues;
        private String merge_requests;
        private String repo_branches;
        private String labels;
        private String events;
        private String members;


    }

    @Data
    public static class PermissionsBean {
        /**
         * project_access : null
         * group_access : {"access_level":30,"notification_level":3}
         */

        private Object project_access;
        private GroupAccessBean group_access;


        public static class GroupAccessBean {
            /**
             * access_level : 30
             * notification_level : 3
             */

            private int access_level;
            private int notification_level;


        }
    }
}

