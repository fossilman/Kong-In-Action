package model.bo;

import lombok.Data;

/**
 * @author krame
 * @description：返回结果
 * @date ：Created in 2019-09-17 11:19
 */
@Data
public class ShellBo {

    private Long id;

    private Long listId;

    private String ip;

    private String result;
}

