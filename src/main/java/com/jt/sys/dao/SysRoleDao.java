package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.jt.sys.entity.SysRole;
import com.jt.sys.vo.CheckBox;

public interface SysRoleDao {
	
	public static final String PAGE_WHERE = 
			"<where>"
			+ "<if test=\"name!=null and name!=''\">name like concat('%', #{name} ,'%')</if>"
			+ "</where>";
	
	/**
	 * 获取所有的角色信息，用sysRole封装数据
	 * @return
	 */
	@Select("<script>"
			+ "select id, name, createdTime, modifiedTime from sys_roles "
			+ PAGE_WHERE 
			+"</script>")
	List<SysRole> findObjects(@Param("name")String name);
	
//	/**
//	 * 分页查询角色记录
//	 * @param startIndex 上一页的结束位置
//	 * @param pageSize	每页要查询的数量
//	 * @return
//	 */
//	@Select("<script>"
//			+ "select id, name, createdTime, modifiedTime from sys_roles "
//			+ PAGE_WHERE
//			+"order by createdTime desc "
//			+ "limit #{startIndex}, #{pageSize}"
//			+ "</script>")
//	List<SysRole> findPageObjects(
//			@Param("startIndex")Integer startIndex, 
//			@Param("pageSize")Integer pageSize,
//			@Param("name")String name);
	
	/**
	 * 获取某个觉得的总记录数
	 * @param name
	 * @return
	 */
	@Select("<script>select count(*) from sys_roles"+PAGE_WHERE+"</script>")
	int getRowCount(@Param("name")String name);
	
	/**
	 * 根据id删除记录
	 */
	@Delete("<script>delete from sys_roles where id in"
			+ "<foreach collection=\"ids\" item=\"id\" open=\"(\" close=\")\" separator=\",\">#{id}</foreach>"
			+ "</script>")
	int deleteObject(@Param("ids")Integer[] ids);
	
	/**
	 * 根据id获取角色信息
	 * @param id
	 * @return
	 */
	@Select("select id, name, note from sys_roles where id=#{id}")
	SysRole findObjectById(@Param("id")Integer id);
	
	/**
	 * 更新角色数据
	 * @param role
	 * @return
	 */
	@Update("<script>"
			+ "update sys_roles "
			+ "<set> "
			+ "<if test=\"name != null and name != ''\"> name=#{name}, </if>"
			+ "<if test=\"note != null and note != ''\">note=#{note},</if>"
			+ "<if test=\"modifiedUser != null and modifiedUser != ''\">modifiedUser=#{modifiedUser},</if>"
			+ "modifiedTime=now()"
			+ "</set>"
			+ "where id=#{id}"
			+ "</script>")
	int updateObject(SysRole role);
	
	/**
	 * 插入新的角色数据
	 * @param role
	 * @return
	 */
	@Insert("insert into sys_roles"
			+ "(name, note, createdTime, modifiedTime, createdUser, modifiedUser) "
			+ "values "
			+ "(#{name}, #{note}, now(), now(), #{createdUser}, #{modifiedUser})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	int insertObject(SysRole role);
	
	/**
	 * 获取所有的角色信息
	 * @return
	 */
	@Select("select id, name from sys_roles")
	List<CheckBox> findAllObjects();
}


















