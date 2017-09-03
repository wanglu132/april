package com.april.domain;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.april.data.domain.DomainMap;
import com.april.exception.DomainException;

/**
 * domain基类
 * @author WL
 *
 */
public abstract class BaseDomain implements DomainMap, Serializable {

	private static final long serialVersionUID = -8987511156387294186L;

	@Override
	public Map<String, Object> toMap() { 
		try {
			return PropertyUtils.describe(this);
		} catch (Exception e) {
			throw new DomainException(e);
		} 
	}

}
