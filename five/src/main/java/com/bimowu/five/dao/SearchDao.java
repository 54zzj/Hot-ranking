package com.bimowu.five.dao;

import com.bimowu.five.model.Information;
import com.bimowu.five.utils.bean.CommonQueryBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * Information数据库操作接口类
 * 
 **/

@Repository
public interface SearchDao {

	int insertIfNotExist(Information info);

	//模糊查询
	List<Information> findLikeInfo(String word);


	/**
	 * 
	 * 查询（根据主键ID查询）
	 * 
	 **/
	Information  selectByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 删除（根据主键ID删除）
	 * 
	 **/
	int deleteByPrimaryKey ( @Param("id") Long id );

	/**
	 * 
	 * 添加
	 * 
	 **/
	int insert( Information record );

	/**
	 * 
	 * 修改 （匹配有值的字段）
	 * 
	 **/
	int updateByPrimaryKeySelective( Information record );

	/**
	 * 
	 * list分页查询
	 * 
	 **/
	List<Information> list4Page ( Information record, @Param("commonQueryParam") CommonQueryBean query);

	/**
	 * 
	 * count查询
	 * 
	 **/
	long count ( Information record);

	/**
	 * 
	 * list查询
	 * 
	 **/
	List<Information> list ( Information record);

}