package org.fibonacci.routeplus.model.v2.bo;

import javax.validation.constraints.NotNull;

public class ServiceListReq extends PageReq {

    @NotNull(message = "applicationId 不能为空")
    private Integer applicationId;


}
