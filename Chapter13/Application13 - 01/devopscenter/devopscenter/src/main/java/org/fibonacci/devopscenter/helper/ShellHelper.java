package org.fibonacci.devopscenter.helper;

import lombok.extern.slf4j.Slf4j;
import org.fibonacci.devopscenter.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author krame
 * @description： shell脚本
 * @date ：Created in 2019-09-17 14:51
 */
@Component
@Slf4j
public class ShellHelper {

    private ShellHelper() {
    }


    public String executeShell(String shellCommand) {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer result = null;
        BufferedReader bufferedReader = null;
        // 格式化日期时间，记录日志时使用
        Process pid = null;
        try {
            stringBuffer.append(DateUtils.formatDate(new Date(), DateUtils.TIMESTAMP_FORMAT))
                    .append("准备执行Shell命令 ").append(shellCommand)
                    .append(" \r\n");

            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //给shell传递参数
            //String[] cmd = { "/bin/sh", "-c", shellCommand+" paramater" };
            // 执行Shell命令
            pid = Runtime.getRuntime().exec(cmd);
            if (pid != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);
                pid.waitFor();
            } else {
                stringBuffer.append("没有pid\r\n");
            }
            /*stringBuffer.append(DateUtils.formatDate(new Date(), DateUtils.TIMESTAMP_FORMAT)).append(
                    "Shell命令执行完毕\r\n执行结果为：\r\n");*/
            String line = null;
            // 读取Shell的输出内容，并添加到stringBuffer中
            result = new StringBuffer();
            while (bufferedReader != null
                    && (line = bufferedReader.readLine()) != null) {
                result.append(line).append("\r\n");
            }
        } catch (Exception e) {
            stringBuffer.append("执行Shell命令时发生异常：\r\n").append(e.getMessage())
                    .append("\r\n");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("关闭流失败！", e);
                }
            }

            if (pid != null) {
                while (pid.isAlive()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("调用shell异常", e);
                        Thread.currentThread().interrupt();
                    }
                    log.info("Waiting For Exit。。。");
                }
                log.info("PID EXIT: " + pid.exitValue());
            }

        }
        log.warn(stringBuffer.toString());
        return result == null ? null : result.toString();
    }


    /**
     * @param shellCommand
     * @return
     * @throws IOException
     */
    public int executeShellReturnexitValue(String shellCommand) {
        StringBuffer stringBuffer = new StringBuffer();
        //BufferedReader bufferedReader = null;
        // 格式化日期时间，记录日志时使用
        Process pid = null;
        try {
            stringBuffer.append(DateUtils.formatDate(new Date(), DateUtils.TIMESTAMP_FORMAT))
                    .append("准备执行Shell命令 ").append(shellCommand)
                    .append(" \r\n");

            String[] cmd = {"/bin/sh", "-c", shellCommand};
            pid = Runtime.getRuntime().exec(cmd);
            log.info("执行脚本结束");
            if (pid != null) {
                pid.waitFor();
            } else {
                stringBuffer.append("没有pid\r\n");
            }
        } catch (Exception e) {

        } finally {
            /*if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    log.error("关闭流异常", e);
                }
            }*/
            if (pid != null) {
                while (pid.isAlive()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("调用shell异常", e);
                        Thread.currentThread().interrupt();
                    }
                    log.info("Waiting For Exit。。。");
                }
                log.info("PID EXIT: " + pid.exitValue());
            }
        }
        log.warn(stringBuffer.toString());
        return pid == null ? 0 : pid.exitValue();
    }

}
