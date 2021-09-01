package org.fibonacci.framework.global;

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
 *
 * P_O: 操作系统
 * P_U: 用户ID
 * P_N: 用户名称（拼音）
 * P_S: SessionId
 * P_T: JWT Token
 * P_I: 请求Id，用于调用链分析
 * R_A: 远程调用地址
 * R_E: 应用名
 */
public enum ParamVariable {

    P_O,P_U,P_N, P_S, P_T, R_I, R_A, R_E;
}
