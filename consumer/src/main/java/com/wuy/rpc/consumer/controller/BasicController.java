package com.wuy.rpc.consumer.controller;


import com.wuy.rpc.consumer.service.BasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@Autowired
	private BasicService basicService;

	@RequestMapping("/saveuser")
	public String saveUser(){
		basicService.SaveUser();
		return "Hello world";
	}


}
