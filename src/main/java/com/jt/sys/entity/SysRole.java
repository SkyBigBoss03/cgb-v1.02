package com.jt.sys.entity;

import java.io.Serializable;

import com.jt.sys.common.entity.BaseEntity;

public class SysRole extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8525096788592322649L;

	private Integer id;
	private String name;
	private String note;

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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "SysRole [id=" + id + ", name=" + name + ", note=" + note + "]";
	}

}
