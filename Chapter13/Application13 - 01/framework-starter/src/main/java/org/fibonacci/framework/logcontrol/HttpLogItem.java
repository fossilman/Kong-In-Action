package org.fibonacci.framework.logcontrol;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020-11-25
 */
public enum HttpLogItem {

    requestUri, requestMethod, requestApi, requestContentType, requestId,
    requestParameters,
    requestHeaders,
    requestBody,
    requestTime, responseStatus, responseContentType, responseBody, responseInterval, remoteAddr, userAgent,
    connectTimeout,socketTimeout,exceptionMessage,jobName,logType,
    traceId, spanId, slowSql,requestParameter;
}
