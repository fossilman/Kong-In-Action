package org.fibonacci.framework.config;

import org.fibonacci.framework.controller.HomeController;
import org.fibonacci.framework.exceptionhandler.GlobalControllerExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@Configuration
@Import({HomeController.class, GlobalControllerExceptionHandler.class})
public class ControllerConfig {
}
