package org.fibonacci.devopscenter.controller;

import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(method = RequestMethod.GET, value = "/testGet")
    public String testGet(@RequestParam String testId) {

        return "OK";
    }

}
