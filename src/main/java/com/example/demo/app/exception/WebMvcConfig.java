package com.example.demo.app.exception;

public class WebMvcConfig {
	
	public static String EXCEPTION_NOTFOUND = "Can't find the same ID";

	public static NotFoundException NOT_FOUND() {
		return new NotFoundException(WebMvcConfig.EXCEPTION_NOTFOUND);
	}

}
