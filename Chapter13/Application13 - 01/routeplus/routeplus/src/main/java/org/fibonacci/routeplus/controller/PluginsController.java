package org.fibonacci.routeplus.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.fibonacci.routeplus.service.PluginsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/plugins")
@Slf4j
public class PluginsController {


    @Autowired
    private PluginsService pluginsService;

    @PostMapping("/service")
    public void addPlugin2Service(@RequestBody JSONObject param) {
        pluginsService.addPlugin2Service(param.getString("kongServiceName"), param.getInteger("pluginId"), param.get("pluginConfig"));
    }

    @DeleteMapping("/service/{pluginId}")
    public void deletePlugin2Service(@PathVariable("pluginId") Integer pluginId) {
        pluginsService.deletePlugin2Service(pluginId);
    }

    @PostMapping("/route")
    public void addPlugin2Route(@RequestBody JSONObject param) {
        pluginsService.addPlugin2Route(param.getString("kongRoutesName"), param.getInteger("pluginId"), param.get("pluginConfig"));
    }

    @DeleteMapping("/route/{pluginId}")
    public void deletePlugin2Route(@PathVariable("pluginId") Integer pluginId) {
        pluginsService.deletePlugin2Route(pluginId);
    }


}
