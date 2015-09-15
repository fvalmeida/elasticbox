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

    @RequestMapping("/facetview")
    public String facetview() {
        return "redirect:/facetview/index.html";
    }

    @RequestMapping("/facetview2")
    public String facetview2() {
        return "redirect:/facetview2/fv_example.html";
    }

    @RequestMapping("/nutch")
    public String nutch() {
        return "redirect:/nutch/index.html";
    }

}
