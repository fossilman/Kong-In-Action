package org.fibonacci.routeplus.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：zachary
 * @description：Gitlab请求对象
 * @date ：Created in 2019-09-17 10:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateLimitingBo {


    /**
     * quantity : 10
     * period : second
     * enabled : true
     */
    private String id;
    private Boolean enabled;

    /**
     * 限流
     */
    private Integer quantity;
    private String period;

    /**
     * 黑白名单
     */
    private String[] whitelist;
    private String[] blacklist;


}
