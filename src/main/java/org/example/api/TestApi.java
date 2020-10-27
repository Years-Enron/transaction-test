package org.example.api;

import org.example.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestApi {

    @Resource
    private TestService testService;

    @GetMapping("/test.do")
    public Object doTest() {
        testService.doTest();
        return "SUCCESS";
    }

}
