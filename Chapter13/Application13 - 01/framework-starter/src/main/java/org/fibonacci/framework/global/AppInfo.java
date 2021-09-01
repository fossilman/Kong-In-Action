package org.fibonacci.framework.global;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/25
 */
@Data
public class AppInfo {

    private String appName;
    private String clusterName;
    private String hostIp;
    private String hostName;
    private String serverIp;
    private Integer serverPort;
    private String env;
    private String appGroup;
    private List<String> excludeName;
}
