package model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author krame
 * @description：返回结果
 * @date ：Created in 2019-09-17 11:19
 */
@Data
public class GitLabBackBo {


    /**
     * id : 6a394c295050357b6f10b178b1eb4c1cb7ea6e41
     * short_id : 6a394c29
     * created_at : 2019-10-05T13:43:05.000Z
     * parent_ids : ["e096067b025d24d6efce9c726a43141f5a00521c"]
     * title : .fix(.loation) : <学习列表、视频详情直播间结束时间改动><body><footer>关联bugId:无
     * message : .fix(.loation) : <学习列表、视频详情直播间结束时间改动><body><footer>关联bugId:无
     * author_name : k
     * author_email : fangkun@puduedu.com
     * authored_date : 2019-10-05T13:43:05.000Z
     * committer_name : k
     * committer_email : fangkun@puduedu.com
     * committed_date : 2019-10-05T13:43:05.000Z
     */

    private String id;
    private String short_id;
    private String created_at;
    private String title;
    private String message;
    private String author_name;
    private String author_email;
    private String authored_date;
    private String committer_name;
    private String committer_email;
    private String committed_date;
    private List<String> parent_ids;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShort_id() {
        return short_id;
    }

    public void setShort_id(String short_id) {
        this.short_id = short_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_email() {
        return author_email;
    }

    public void setAuthor_email(String author_email) {
        this.author_email = author_email;
    }

    public String getAuthored_date() {
        return authored_date;
    }

    public void setAuthored_date(String authored_date) {
        this.authored_date = authored_date;
    }

    public String getCommitter_name() {
        return committer_name;
    }

    public void setCommitter_name(String committer_name) {
        this.committer_name = committer_name;
    }

    public String getCommitter_email() {
        return committer_email;
    }

    public void setCommitter_email(String committer_email) {
        this.committer_email = committer_email;
    }

    public String getCommitted_date() {
        return committed_date;
    }

    public void setCommitted_date(String committed_date) {
        this.committed_date = committed_date;
    }

    public List<String> getParent_ids() {
        return parent_ids;
    }

    public void setParent_ids(List<String> parent_ids) {
        this.parent_ids = parent_ids;
    }
}

