package org.fibonacci.routeplus.configuration;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author zachary
 * @date 2019-12-13
 */
@Data
@Component
public class Configs {

    /**
     * service名称
     * 格式：service_@servicename
     */
    private String sname = "@servicename";

    /**
     * route名称
     * 格式：route_@routename_@type
     */
    private String rname = "@routename";

    /**
     * route路径(内部路径)
     * 格式 /@type/@type_@path
     */
    private String rpath = "@path";


    /**
     * kong节点数
     */
    private Integer node = 2;

}
