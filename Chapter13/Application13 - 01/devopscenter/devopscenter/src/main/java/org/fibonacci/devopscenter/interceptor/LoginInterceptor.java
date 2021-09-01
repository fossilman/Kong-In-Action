package org.fibonacci.devopscenter.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.fibonacci.framework.global.AppInfo;
import org.fibonacci.framework.threadlocal.ParameterThreadLocal;
import org.fibonacci.framework.util.RequestIdUtil;
import org.fibonacci.framework.util.ResultCode;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;



/**
 * @author krame
 * @date 2019-07-08
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    //@Autowired
    //private InnerUserFeignClient innerUserFeignClient;

    @Resource
    private AppInfo appInfo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {

        log.info("ip:>>" + getIpAddress(request));
        log.info("UserAgent:>>" + RequestIdUtil.getUserAgent(request));
        //验证请求
        String url = request.getRequestURL().toString();
        if (url.indexOf("error") > -1) {
            return true;
        }
        /*if(!checkTokenSystemName()){
            isTimeOut(response, "FS20006", "token无效");
            return false;
        }*/
        ResultCode<?> verifyCode = this.verifyExe();
        if (!verifyCode.isSuccess()) {
            isTimeOut(response, verifyCode.getErrcode(), verifyCode.getErrmsg());
            return false;
        }
        return true;
    }


    /**
     * 验证请求合法性
     *
     * @param
     * @return
     */
    private ResultCode<?> verifyExe() {

        //调用inneruser鉴权
        //ResultCode<LoginRsp> authRes = null;
        try {
//            authRes = innerUserFeignClient.loginFilter(appInfo.getAppName());
//            authRes = innerUserFeignClient.loginFilter("publish");
//            if (!authRes.isSuccess()) {
//                log.info("调用inneruser鉴权失败，code:{},msg:{}", authRes.getErrcode(), authRes.getErrmsg());
//                return authRes;
//            }
        } catch (Exception e) {
            log.error("调用inneruser系统鉴权失败", e);
            return ResultCode.getFailure("-9", "调用inneruser系统鉴权失败");
        }
        //ParameterThreadLocal.setToken(JSONObject.toJSONString(authRes.getRetval()));
        return ResultCode.SUCCESS;
    }

    /**
     * 验证token系统名
     */
    private boolean checkTokenSystemName() {
        String jwtToken = ParameterThreadLocal.getToken();
        if (jwtToken == null) {
            log.info("jwtToken为空，获取用户信息失败");
            return false;
        }
        String token = null;
        if (jwtToken.indexOf("&&&") == -1) {
            token = jwtToken;
        } else {
            try {
                String[] str = jwtToken.split("&&&");
                token = str[0];
            } catch (Exception e) {
                log.error("处理权限验证失败", e);
            }
        }
//        ResultCode<Claims> jwtTokenCode = JwtUtil.getInstance().parseJWT(token, null);
//        if (jwtTokenCode.isSuccess()) {
//            Claims claims = (Claims) jwtTokenCode.getRetval();
//            JSONObject loginRsp = JSONObject.parseObject(claims.getSubject());
////            if (loginRsp != null && loginRsp.getString("systemName").equals(appInfo.getAppName())) {
//            if (loginRsp != null && loginRsp.getString("systemName").equals("publish")) {
//                return true;
//            }
//        } else {
//            return false;
//        }
        return false;
    }


    /**
     * token为空时返回
     *
     * @param response
     * @throws Exception
     */
    private void isTimeOut(HttpServletResponse response, String code, String errMsg) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(ResultCode.getFailure(code, errMsg)));

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }


    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
