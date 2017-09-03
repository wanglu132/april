package com.april.data.mybatis.repository;

import java.io.Serializable;
import java.util.List;

import com.april.data.repository.CrudRepository;

/**
 * SqlMap CRUD 接口
 * @author WL
 *
 * @param <E> 实体
 * @param <PK> 主键
 */
public interface SqlMapCrudRepository<E, PK extends Serializable> extends CrudRepository<E, PK>{
	
	public static final String DEFAULT_FIND_PAGE = "findPage";
	
	/**
	 * 根据参数对象删除
	 * @param po 参数对象
	 * @return 删除影响行数
	 */
	public abstract <P> int delete(P po);

	/**
	 * 根据参数对象查询
	 * @param po 参数对象
	 * @return 结果对象
	 */
	public abstract <P, V> List<V> find(P po);
}
