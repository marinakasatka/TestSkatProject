package com.in28minutes.erst.webservises.restfulwebservices;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWordControler {
	//GET
	//
	@GetMapping(path = "/hello-world")
	public String hellpWorld() {
		return "Hello world!";
	}
	

}
