package org.fibonacci.routeplus.utils;

import org.fibonacci.framework.exceptions.ClientException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: zachary
 * @Date: 2020-05-20 11:32
 */
public class RoutesUtil {


    /**
     * 对象转换为集合
     *
     * @param id
     * @return
     */
    public static List<Integer> conventList(Integer id) {
        return Stream.of(id).collect(Collectors.toList());
        //return Arrays.asList(new Integer[]{id});
    }

    /**
     * 黑白名单插件封装
     *
     * @param whitelist
     * @param blacklist
     * @return
     */
    public static Map<String, Object> ipRestrictionMaps(String[] whitelist, String[] blacklist) {

        if (whitelist == null && blacklist == null) {
            throw new ClientException("10001", "插件不存在!");
        }

        Map<String, Object> map = new HashMap<>();
        if (whitelist != null) {
            map.put("whitelist", Arrays.stream(whitelist).collect(Collectors.joining(",")));
        }
        if (blacklist != null) {
            map.put("blacklist", Arrays.stream(blacklist).collect(Collectors.joining(",")));
        }
        return map;
    }

}
