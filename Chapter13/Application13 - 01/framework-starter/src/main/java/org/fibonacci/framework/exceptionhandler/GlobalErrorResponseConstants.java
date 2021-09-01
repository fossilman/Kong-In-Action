package org.fibonacci.framework.exceptionhandler;

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
public class GlobalErrorResponseConstants {

    public static final int COMMON_SERVER_ERROR_CODE = 1001;
    public static final String COMMON_SERVER_ERROR_MESSAGE = "系统忙不过来啦，稍等一下";

    public static final int COMMON_CLIENT_ERROR_CODE = 1005;
    public static final String COMMON_CLIENT_ERROR_MESSAGE = "您的操作有误，重新试试吧";

    public static final int REQUEST_ERROR_CODE = 1002;
    public static final String REQUEST_ERROR_MESSAGE = "输入信息有误，重新试试吧";

    public static final int COMMON_BIZ_ERROR_CODE = 1003;

    public static final int INTERNAL_CALL_ERROR_CODE = 1004;
    public static final String INTERNAL_CALL_ERROR_MESSAGE = "系统忙不过来啦，稍等一下";

    public static final int SESSION_TIMEOUT_ERROR_CODE = 1006;
    public static final String SESSION_TIMEOUT_ERROR_MESSAGE = "发呆的时间太长，请先登录哦";

}
