package org.fibonacci.devopscenter.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author krame
 * @description：
 * @date ：Created in 2019-09-18 15:28
 */
public interface PublishConstants {

    //构建状态
    public enum BUILD_STATUS {
        ING, SUCCESS, FAIL;
    }

    //发布状态
    public enum PUBLISH_STATUS {
        ING, SUCCESS, FAIL;
    }

    //项目类型
    public enum APPLICATION_STATUS {
        client, server_inner, server_outer;
    }


    //发布状态
    public enum DEPLOY_STATUS {
        SIMPLE("SIMPLE", "100%流量模式"), COMPATIBILITY("COMPATIBILITY", "新老版本兼容模式");

        private String name;
        private String desc;

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        DEPLOY_STATUS(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public static String getNameAndDesc(String name) {
            String desc = "";
            for (DEPLOY_STATUS ds : DEPLOY_STATUS.values()) {
                if (StringUtils.equals(ds.getName(), name)) {
                    desc = ds.getDesc();
                    break;
                }
            }
            return desc;
        }
    }

    public static void main(String[] args) {
        System.out.println(DEPLOY_STATUS.getNameAndDesc("SIMPLE"));
    }

}
