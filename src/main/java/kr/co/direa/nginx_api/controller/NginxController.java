package kr.co.direa.nginx_api.controller;

import kr.co.direa.nginx_api.service.NginxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/nginx")
public class NginxController {

    private final NginxService nginxService;

    @Autowired
    public NginxController(NginxService nginxService) {
        this.nginxService = nginxService;
    }


    @GetMapping("/config")
    public String getConfig() {

        return "nginx_config";
    }

    @PostMapping("/config")
    public String postConfig() {

        return "redirect:nginx/config";
    }
}
