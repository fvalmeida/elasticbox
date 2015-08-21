package org.fvalmeida.elasticbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fvalmeida on 8/17/15.
 */
@SpringBootApplication
@Controller
public class WebApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
    
    @RequestMapping("/ESClient")
    public String ESClient() {
        return "redirect:/ESClient/index.html";
    }

    @RequestMapping("/kopf")
    public String kopf() {
        return "redirect:/kopf/index.html?location=http://localhost:9200";
    }

}
