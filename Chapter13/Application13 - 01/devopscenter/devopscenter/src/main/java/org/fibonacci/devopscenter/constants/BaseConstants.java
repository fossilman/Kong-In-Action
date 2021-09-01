package org.fibonacci.devopscenter.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author krame
 * @description：基本常量
 * @date ：Created in 2019-10-06 22:53
 */
public class BaseConstants {

    public enum GitEnv {
        develop("dev", "develop"),
        sit("sit", "release"),
        master("prod", "master");


        private String name;
        private String value;

        public static String throughtNameGetValue(String name) {
            String ext = "";
            for (GitEnv gitEnv : GitEnv.values()) {
                if (StringUtils.equals(gitEnv.getName(), name)) {
                    ext = gitEnv.getValue();
                }
            }
            return ext;
        }

        GitEnv(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

    }
}
