package org.fibonacci.routeplus.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：zachary
 * @description：基本常量
 * @date ：Created in 2019-10-06 22:53
 */
public class BaseConstants {

    public enum GatewayName {
        gateway, application;
    }

    public enum PluginName {
        rateLimiting("rate-limiting"), ipRestriction("ip-restriction");
        private String name;

        PluginName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public enum Period {
        SECOND("second", null),
        MINUTE("minute", null),
        HOUR("hour", null),
        YEAR("year", null),
        MONTH("month", null),
        DAY("day", null);

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        Period(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static Map<String, Object> getPeriods(String period, Integer quantity) {

            Map map = new HashMap();
            for (Period value : Period.values()) {
                map.put(value.getName(), value.getValue());
            }
            for (Period value : Period.values()) {
                if (StringUtils.equalsIgnoreCase(value.getName(), period)) {
                    map.put(value.getName(), quantity);
                    return map;
                }
            }
            return map;
        }
    }

//    public static LoginRsp parseJwtTokenLogin() {
//
//        String login = ParameterThreadLocal.getToken();
//        if (StringUtils.isBlank(login)) {
//            throw new ServerException("10099", "token为空");
//        }
//        LoginRsp loginRsp = JSONObject.parseObject(login, LoginRsp.class);
//        if (loginRsp == null || loginRsp.getUsername() == null) {
//            throw new ServerException("10001", "用户名为空!");
//        }
//        return loginRsp;
//    }
}
