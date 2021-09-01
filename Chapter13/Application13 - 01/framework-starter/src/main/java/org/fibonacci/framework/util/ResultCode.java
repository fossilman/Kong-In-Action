package org.fibonacci.framework.util;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020-11-25
 */
@Data
public class ResultCode<T> implements Serializable {

    private static final long serialVersionUID = -4859637902882356019L;

    private ResultCode(String code, String errmsg, T retval, boolean isSuccess) {
        this.errcode = code;

        this.success = isSuccess;

        this.errmsg = errmsg;

        this.retval = retval;
    }

    public static <T> ResultCode<T> getFailure(String code, String errmsg) {
        return new ResultCode(StringUtils.isEmpty(code) ? "-1" : code, errmsg, null, false);
    }

    public static <T> ResultCode<T> getFailureReturn(String code, String errmsg, T retval) {
        return new ResultCode(StringUtils.isEmpty(code) ? "-1" : code, errmsg, retval, false);
    }

    public static <T> ResultCode<T> getFailureException(Exception e) {
        return getFailure(null, "处理失败，返回系统异常");
    }

    public static <T> ResultCode<T> getFailureBusiException(RuntimeException e) {
        return getFailure(null, e.getMessage());
    }


    public static <T> ResultCode<T> getSuccess(String code, String errmsg) {
        return new ResultCode(StringUtils.isEmpty(code) ? "0" : code, errmsg, null, true);
    }

    public static <T> ResultCode<T> getSuccessReturn(String code, String errmsg, T retval) {
        return new ResultCode(StringUtils.isEmpty(code) ? "0" : code, errmsg, retval, true);
    }


    public boolean isSuccess() {
        return this.success;
    }

    public Object getRetval() {
        return this.retval;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public String getErrcode() {
        return this.errcode;
    }

    public static final ResultCode<String> SUCCESS = new ResultCode("0", "ok", null, true);
    public static final ResultCode<String> FAIL = new ResultCode("-1", "error", null, false);
    private String errcode;
    private String errmsg;
    private T retval;
    private boolean success;

    public static void main(String[] args) {

        ResultCode s=ResultCode.getFailureReturn(null,null,"s1");
        ResultCode S1= JSONObject.parseObject(JSONObject.toJSONString(s),ResultCode.class);
        System.out.println(S1.getErrcode());
        System.out.println(S1.getErrmsg());
        System.out.println(S1.getRetval());
    }

}
