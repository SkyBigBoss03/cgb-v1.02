package com.jt.sys.vo;

import java.io.Serializable;

public class CheckBox implements Serializable {

	private static final long serialVersionUID = -265045571154506488L;

	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CheckBox [id=" + id + ", name=" + name + "]";
	}

	
	
}
