package org.fibonacci.devopscenter.controller;

import org.fibonacci.devopscenter.service.ChangeRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeRouteController {

    @Autowired
    private ChangeRouteService changeRouteService;

    @PostMapping("/change-route/{appId}/{vagrancy}")
    public void changeVagrancy(@PathVariable("appId") Long appId, @PathVariable("vagrancy") Integer vagrancy) {
        changeRouteService.changeVagrancy(appId, vagrancy);
    }
}
