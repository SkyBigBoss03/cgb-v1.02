package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SysCommonDao {

	@Select("select COLUMN_NAME from information_schema.columns where table_name=#{tableName} and table_schema='jt_sys'")
	List<String> findTableColumns(@Param("tableName")String tableName);
	
}
