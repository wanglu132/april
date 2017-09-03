package com.april.data.repository;

import java.io.Serializable;

/**
 * CRUD接口
 * @author WL
 *
 * @param <E> 实体
 * @param <PK> 主键
 */
public interface CrudRepository<E, PK extends Serializable> {
	
  public <S extends E> int insert(S paramS);
  
  public <S extends E> int updateByPK(S paramS);
  
  public <S extends E> int updateSelectiveByPK(S paramS);
  
  public int deleteByPK(PK paramID);
  
  public E findByPK(PK paramID);
  
  public boolean exists(PK paramID);
  
}
