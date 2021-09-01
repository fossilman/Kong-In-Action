package org.fibonacci.routeplus.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ：zachary
 * @description：
 * @date ：Created in 2020-05-14 09:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlusBo {

    /**
     * 应用名称
     */
    @NotBlank(message = "name is not null", groups = {deployKong.class,
            removeKong.class, getTargetKong.class})
    private String name;

    /**
     * 发布类型
     */
    @NotBlank(message = "publishType is not null", groups = {deployKong.class})
    private String publishType;

    //网关类型
    @NotBlank(message = "gatewayType is not null", groups = {deployKong.class})
    private String gatewayType;


    @NotNull(message = "serverList is not null", groups = {deployKong.class})
    @Valid
    private List<ServerBo> serverList;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerBo {

        @NotBlank(message = "ip is not null", groups = {deployKong.class})
        private String ip;

        @NotBlank(message = "port is not null", groups = {deployKong.class})
        private String port;

        @NotNull(message = "vagrancy is not null", groups = {deployKong.class})
        private Integer vagrancy;

    }

    public interface deployKong {

    }

    public interface removeKong {

    }

    public interface getTargetKong {

    }
}
