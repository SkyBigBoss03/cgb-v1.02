package com.jt.sys.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {
	// 用于统计用于每天登录了多少次(底层使用了CAS算法，比较和交换)
	private AtomicInteger aCount = new AtomicInteger(1);
	
	/**
	 * 显示web首页
	 * 
	 * @return
	 */
	@RequestMapping("indexUI")
	public String indexUI() {
		System.out.println("第"+aCount.getAndIncrement()+"次访问");
		return "starter";
	}

}
