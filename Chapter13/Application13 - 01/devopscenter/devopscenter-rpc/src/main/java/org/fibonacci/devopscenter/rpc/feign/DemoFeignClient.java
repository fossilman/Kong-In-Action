package org.fibonacci.devopscenter.rpc.feign;

import org.fibonacci.devopscenter.common.bo.Demo;
import org.fibonacci.devopscenter.common.constant.ServiceConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * Copyright (C) 2020 Shanghai LuoJin Com., Ltd. All rights reserved.
 * <p>
 * No parts of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior written
 * permission of Shanghai LuoJin Com., Ltd.
 *
 * @author krame
 * @date 2020/11/26
 */
@FeignClient(ServiceConstant.SERVICE_NAME)
@RequestMapping(ServiceConstant.ROOT_URL)
public interface DemoFeignClient {

    @GetMapping("/demo/testGet")
    String testGet(@RequestParam("testId") String testId);

    @PostMapping("/demo/testPost")
    Demo testPost(@RequestBody Demo demo);
}
