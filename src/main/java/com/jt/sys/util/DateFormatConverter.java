package com.jt.sys.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateFormatConverter extends JsonSerializer<Date> {
	/**
	 * 负责相关对象的转换操作
	 * @param value 表示要转换的数据
	 * @param gen 负责将转换的数据写到json串中
	 */
	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<>();
	
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		//1.转换数据 
		String dateStr = getSimpleDateFormat().format(value);
		//2.将转换的结果写到json串中
		gen.writeString(dateStr);
		remove();
	}
	
	private static SimpleDateFormat getSimpleDateFormat() {
		if (threadLocal.get() != null) {
			return threadLocal.get();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		threadLocal.set(sdf);
		return sdf;
	}
	
	private static void remove() {
		threadLocal.remove();
	}
}
