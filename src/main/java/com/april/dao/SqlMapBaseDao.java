package com.april.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.april.data.domain.Page;
import com.april.data.domain.PageRequest;
import com.april.data.mybatis.repository.SqlMapCrudRepository;

/**
 * 抽象DAO实现<br>
 * <strong>已被DAO接口取代，一般用不着</strong>
 * 
 * @author WL
 *
 * @param <E> 实体
 * @param <PK> 主键
 */
public abstract class SqlMapBaseDao<E, PK extends Serializable> extends SqlSessionDaoSupport implements SqlMapCrudRepository<E, PK> {

    protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected static final String INSERT = "insert";
    
	protected static final String UPDATE_BY_PK = "updateByPK";
	
	protected static final String UPDATE_SELECTIVE_BY_PK = "updateSelectiveByPK";
    
	protected static final String DELETE_BY_PK = "deleteByPK";
	
	protected static final String DELETE = "delete";
    
	protected static final String FIND_BY_PK = "findByPK";
	
	protected static final String FIND = "find";
    
	protected static final String EXISTS = "exists";
    
	protected String namespace;
    
    protected String getNamespace() {
        return namespace;
    }
    
    protected String getUID(String id) {
        return namespace + "." + id;
    }
    
    /**
     * 以调用方法名作为 sqlmap id
     * @return
     */
    protected String getUIDUseMethodName() {
        return getUID(Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    

    public SqlMapBaseDao() {
        this.namespace = getClass().getName();
    }
    
    /**
     * 插入实体
     */
    @Override
    public <S extends E> int insert(S entity) {
        return getSqlSession().insert(namespace + "." + INSERT, entity);
    }

    /**
     * 插入多个实体
     * @param entities 实体集合
     */
    public <S extends E> int insert(Iterable<S> entities) {
        
        int affectedRows = 0;
        for(Iterator<S> its = entities.iterator(); its.hasNext(); ) {
            affectedRows += insert(its.next());
        }
        return affectedRows;
    }
    
    /**
     * 根据主键更新
     */
    @Override
    public <S extends E> int updateByPK(S entity) {
        return getSqlSession().update(namespace + "." + UPDATE_BY_PK, entity);
    }
    
    /**
     * 根据主键更新-不为空的字段
     */
    @Override
	public <S extends E> int updateSelectiveByPK(S entity) {
    	return getSqlSession().update(namespace + "." + UPDATE_SELECTIVE_BY_PK, entity);
	}
    
    /**
     * 根据主键删除
     */
    @Override
    public int deleteByPK(PK pk) {
        return getSqlSession().delete(namespace + "." + DELETE_BY_PK, pk);
    }
    

    /**
     * 根据视图对象删除
     */
    @Override
	public <P> int delete(P po) {
    	return getSqlSession().delete(namespace + "." + DELETE, po);
	}
    
    /**
     * 根据主键查找
     */
    @Override
    public E findByPK(PK pk) {
    	return getSqlSession().selectOne(namespace + "." + FIND_BY_PK, pk);
    }
    
    /**
     * 根据视图对象查询
     */
    @Override
	public <P, V> List<V> find(P po) {
    	return getSqlSession().selectList(namespace + "." + FIND, po);
	}

    /**
     * 根据主键判断数据是否存在
     */
    @Override
    public boolean exists(PK pk) {
        return getSqlSession().selectOne(namespace + "." + EXISTS, pk);
    }

    /**
     * 分页查询<br>
     * 使用默认 sqlmap id
     */
    public <V> Page<V> findPage(PageRequest po)  {
        return this.findPage(DEFAULT_FIND_PAGE, po);
    }
    
    /**
     * 分页查询<br>
     * @param findPage_id sqlmap select id (不含namespace)
     */
    public <V> Page<V> findPage(String findPage_id, PageRequest po) {
        return findPage(findPage_id + "_count", findPage_id, po);
    }
    
    /**
     * 分页查询<br>
     * @param count_id sqlmap count id (不含namespace)
     * @param findPage_id sqlmap select id (不含namespace)
     */
    public <V> Page<V> findPage(String count_id, String findPage_id, PageRequest po) {
    	
    	po = po == null ? new PageRequest() : po;
    	
        Integer count = (Integer)getSqlSession().selectOne(namespace + "." + count_id, po.getMap());
        if(count == null || count <= 0) {
            return new Page<V>(po, 0);
        }
            
        Page<V> page = new Page<V>(po, count);
        List<V> ts = getSqlSession().selectList(namespace + "." + findPage_id, po.getMap(), new RowBounds(page.getThisPageFirstElementNumber(), page.getThisPageLastElementNumber() - page.getThisPageFirstElementNumber()));
        page.setResult(ts);
        
        return page;
    }

}
