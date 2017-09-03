package com.april.service;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.april.dao.SqlMapBaseDao;
import com.april.data.domain.Page;
import com.april.data.domain.PageRequest;
import com.april.data.mybatis.repository.SqlMapCrudRepository;
import com.april.exception.ServiceException;

/**
 * 业务抽象类
 * @author WL
 *
 * @param <E> 实体对象
 * @param <PK> 主键
 */
public abstract class BaseService<E, PK extends Serializable> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected abstract SqlMapCrudRepository<E, PK> getEntityDao();


	/**
	 * 插入实体
	 */
	@Transactional
	public <S extends E> int insert(S entity) {
		return getEntityDao().insert(entity);
	}

	/**
	 * 根据主键更新
	 */
	@Transactional
	public <S extends E> int updateByPK(S entity) {
		return getEntityDao().updateByPK(entity);
	}
	

	/**
	 * 根据主键更新不为空的字段
	 */
	@Transactional
	public <S extends E> int updateSelectiveByPK(S entity) {
		return getEntityDao().updateSelectiveByPK(entity);
	}
	
	/**
	 * 根据主键删除
	 */
	@Transactional
	public int deleteByPK(PK pk) throws DataAccessException {
		return getEntityDao().deleteByPK(pk);
	}

	/**
	 * 根据参数对象删除
	 */
	@Transactional
	public <P> int delete(P po) {
		return getEntityDao().delete(po);
	}

	/**
	 * 根据主键查询
	 */
	public E findByPK(PK pk) {
		return getEntityDao().findByPK(pk);
	}


	/**
	 * 根据参数对象查询
	 */
	public <P, V> List<V> find(P po) {
		return getEntityDao().find(po);
	}

	/**
	 * 根据主键判断数据是否存在
	 */
	public boolean exists(PK pk) {
		return getEntityDao().exists(pk);
	}

	/**
	 * 分页查询<br>
	 * 使用默认 sqlmap id
	 */
	public <V> Page<V> findPage(PageRequest po) {
		return this.findPage(SqlMapCrudRepository.DEFAULT_FIND_PAGE, po);
	}

	/**
	 * 分页查询<br>
	 * 
	 * @param findPage_id
	 *            sqlmap select id (不含namespace)
	 */
	public <V> Page<V> findPage(String findPage_id, PageRequest po) {
		return findPage(findPage_id + "_count", findPage_id, po);
	}

	/**
	 * 分页查询<br>
	 * 
	 * @param count_id
	 *            sqlmap count id (不含namespace)
	 * @param findPage_id
	 *            sqlmap select id (不含namespace)
	 */
	@SuppressWarnings("unchecked")
	public <V> Page<V> findPage(String count_id, String findPage_id, PageRequest po) {
		
		po = po == null ? new PageRequest() : po;
		try {
			
			 if (getEntityDao() instanceof SqlMapBaseDao) {
				 SqlMapBaseDao<E,PK> dao = (SqlMapBaseDao<E,PK>) getEntityDao();
				 return dao.findPage(count_id, findPage_id, po);
			}else{
				Integer count = (Integer) (getEntityDao().getClass().getMethod(count_id, Object.class).invoke(getEntityDao(), po.getMap()));
				if (count == null || count <= 0) {
					return new Page<V>(po, 0);
				}
				Page<V> page = new Page<V>(po, count);
				List<V> ts = (List<V>) (getEntityDao().getClass().getMethod(findPage_id, Object.class, RowBounds.class).invoke(
						getEntityDao(),
						po.getMap(),
						new RowBounds(page.getThisPageFirstElementNumber(), page.getThisPageLastElementNumber() - page.getThisPageFirstElementNumber())));
				page.setResult(ts);
				return page;
			}
			
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new ServiceException(t.getMessage(), t);
		}

	}

}
