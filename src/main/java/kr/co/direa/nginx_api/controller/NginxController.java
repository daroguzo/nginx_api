package kr.co.direa.nginx_api.controller;

import kr.co.direa.nginx_api.service.NginxService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping(value = "/nginx")
@Controller
public class NginxController {

    private final NginxService nginxService;
    
    @Autowired
    public NginxController(NginxService nginxService) {
        this.nginxService = nginxService;
    }

    @GetMapping("/config")
    public String getConfig(Model model) {
        String configString = nginxService.getConfigFile();
        model.addAttribute("configString", configString);

        return "config";
    }

    @PostMapping("/config")
    public String postConfig(String configString, RedirectAttributes ra) {
        boolean isRun = nginxService.reviseConfigFile(configString);
        ra.addFlashAttribute("isRun", isRun);

        return "redirect:/nginx/config";
    }
}
