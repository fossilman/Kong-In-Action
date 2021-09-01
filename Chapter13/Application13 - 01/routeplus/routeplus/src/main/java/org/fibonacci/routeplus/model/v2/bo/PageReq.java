package org.fibonacci.routeplus.model.v2.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReq {
    /**
     * 每页显示条数，默认 10
     */
    private Integer size;

    /**
     * 当前页
     */
    private Integer current;

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
