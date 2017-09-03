package com.april.data.mybatis.repository;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

/**
 * 供mybatis接口式DAO继承使用
 * 
 * @author WL
 *
 * @param <E> 实体
 * @param <PK> 主键
 */
public interface ISqlMapRepository<E, PK extends Serializable> extends SqlMapCrudRepository<E, PK>{
	
	/** 默认分页方法 */
	public <P, V> List<V> findPage(P po, RowBounds rb);

	/** 默认分页总数目  */
	public <P> int findPage_count(P po);

}