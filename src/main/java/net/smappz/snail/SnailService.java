package net.smappz.snail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class SnailService {

    @CrossOrigin(origins = "*")
    @RequestMapping("/snail")
    String snail(@RequestParam(value = "size") int size, Model model) {
        return Snail.render(size);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SnailService.class, args);
    }}
