package com.jt.sys.vo;

import java.io.Serializable;

/**
 * 通过此对象对控制层数据进行封装 1)正常数据 2)异常数据
 */
public class JsonResult implements Serializable {
	private static final long serialVersionUID = 3766315591185173299L;

	/** 表示状态码 1表示ok 0表示失败 */
	private int state = 1;
	/** 具体消息 */
	private String message = "ok";
	/** 具体数据 */
	private Object data;

	public JsonResult() {
	}

	public JsonResult(Object data) {
		this.data = data;
	}

	public JsonResult(String message) {
		this.message = message;
	}

	public JsonResult(Object data, String message) {
		this.data = data;
		this.message = message;
	}

	public JsonResult(Throwable t) {
		this.state = 0;
		this.message = t.getMessage();
	}

	public static JsonResult ok() {
		JsonResult jsonResult = new JsonResult();
		return jsonResult;
	}
	
	public static JsonResult ok(String message) {
		JsonResult jsonResult = new JsonResult();
		jsonResult.setMessage(message);
		return jsonResult;
	}

	public static JsonResult error() {
		JsonResult jsonResult = new JsonResult();
		jsonResult.setState(0);
		jsonResult.setMessage("error");
		return jsonResult;
	}
	
	public static JsonResult error(Throwable t) {
		return new JsonResult(t);
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "JsonResult [state=" + state + ", message=" + message + ", data=" + data + "]";
	}

}
