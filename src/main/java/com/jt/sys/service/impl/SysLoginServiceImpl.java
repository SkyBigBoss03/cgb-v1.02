package com.jt.sys.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.jt.sys.common.exception.ServiceException;
import com.jt.sys.service.SysLoginService;
import com.jt.sys.util.ShiroUtils;

@Service
public class SysLoginServiceImpl implements SysLoginService {
	@Autowired
	private Producer producer;
	@Override
	public void getKaptchaImage(HttpServletResponse response) {
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");
		// 生成验证码
		String capText = producer.createText();
		ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
		// 向客户端写出
		BufferedImage bi = producer.createImage(capText);
		ServletOutputStream out;
		try {
			out = response.getOutputStream();
			ImageIO.write(bi, "jpg", out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("获取验证码失败");
		} finally {
			out = null;
		}
	}
}
