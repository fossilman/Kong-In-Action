package org.fibonacci.mybatis.starter.log;

import java.util.Map;

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
public class LogInfo {

    public static final String KEY_API = "api";
    public static final ThreadLocal<Map<String, Object>> info = new ThreadLocal<>();
}
