package org.fibonacci.framework.controller;

import org.fibonacci.framework.global.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
@RestController
@Slf4j
public class HomeController {

    @Autowired
    private AppInfo appInfo;

    @GetMapping(value = "/home")
    public String home(){
        return appInfo.getAppName() + " OK";
    }

    @GetMapping(value="/")
    public void index(HttpServletResponse response) throws IOException {
        response.sendRedirect("home");
    }

    @GetMapping("metrics")
    public void metrics(HttpServletResponse response) throws IOException {
        response.sendRedirect("/prometheus");
    }

}
