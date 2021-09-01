package org.fibonacci.routeplus.model.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2019-09-18 13:42
 */
@Getter
@Setter
public class BaseBo {

    /**
     * 每页显示条数，默认 10
     */
    private Integer size;

    /**
     * 当前页
     */
    private Integer current;



    /**
     * 总数
     */
    private Integer total;

    public Integer currentifPage() {
        if (current == null) {
            return 0;
        }
        return current == 0 ? 0 : current;
    }

    public Integer sizeif() {
        return size == null ? 10 : size;
    }

}
