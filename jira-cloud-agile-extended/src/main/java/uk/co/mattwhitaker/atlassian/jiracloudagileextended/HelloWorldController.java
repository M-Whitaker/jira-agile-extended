package uk.co.mattwhitaker.atlassian.jiracloudagileextended;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloWorldController {

    @GetMapping("/atlaskit")
    public String helloWorld() {
        System.out.println("Hello world!!!");
        return "atlaskit";
    }
}
