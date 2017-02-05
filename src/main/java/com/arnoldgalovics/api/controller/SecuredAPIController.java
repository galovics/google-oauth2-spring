package com.arnoldgalovics.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuredAPIController {
    @RequestMapping(value = "/secure")
    public Response secure() {
        return new Response("example");
    }

    public static class Response {
        private String str;

        public Response(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }
}
