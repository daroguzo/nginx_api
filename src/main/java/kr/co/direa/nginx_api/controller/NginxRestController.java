package kr.co.direa.nginx_api.controller;

import kr.co.direa.nginx_api.service.NginxService;
import kr.co.direa.nginx_api.vo.MonitoringStatus;
import kr.co.direa.nginx_api.vo.NginxStatus;
import kr.co.direa.nginx_api.vo.NodeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping(value = "/nginx/api", produces =  "application/json; charset=utf8")
public class NginxRestController {

    private final NginxService nginxService;

    @Autowired
    public NginxRestController(NginxService nginxService) {
        this.nginxService = nginxService;
    }

    @GetMapping("/start")
    public NginxStatus start() {
        return nginxService.start();
    }

    @GetMapping("/stop")
    public NginxStatus stop() {
        return nginxService.stop();
    }

    @GetMapping("/reload")
    public NginxStatus reload() {
        return nginxService.reload();
    }

    @GetMapping("/status")
    public MonitoringStatus status() {
        return nginxService.status();
    }

    @GetMapping("/monitoring")
    public NodeInfo monitoring() {
        return nginxService.getNodeInfo();
    }

}
