package org.fibonacci.devopscenter.utils;

import model.bo.ShellBo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author krame
 * @Description: 处理定时任务的工具类
 * @date 2020/4/8 10:36 上午
 */
public class FeatureUtils {

    public static List<ShellBo> getFeaturesResult(List<Future<ShellBo>> futures, Logger log) {
        List<ShellBo> shellBos=new ArrayList<>();
        futures.forEach(f->{
            try{
                shellBos.add(f.get());
            }catch(Exception exception){
                log.warn("{}服务访问异常{}",exception);
            }
        });
        return shellBos;
    }

}
