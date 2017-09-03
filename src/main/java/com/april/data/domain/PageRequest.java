package com.april.data.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.april.exception.DomainException;

public class PageRequest implements DomainMap, Serializable {
	
	private static final long serialVersionUID = -2909661593058637251L;
	
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	private int pageNumber;
	
	private int pageSize = DEFAULT_PAGE_SIZE;
	
	private String sortColumns;
	
	private DomainMap domainMap = null;
	
	private Map<String, Object> map = null;
	
	public PageRequest() { }

	public PageRequest(DomainMap domainMap) {
		this.domainMap = domainMap;
	}

	public PageRequest(DomainMap fieldsAddor, int pageNumber, int pageSize) {
		this(fieldsAddor, pageNumber, pageSize, null);
	}

	public PageRequest(DomainMap fieldsAddor, int pageNumber, int pageSize, String sortColumns) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		setSortColumns(sortColumns);
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortColumns() {
		return this.sortColumns;
	}

	public void setSortColumns(String sortColumns) {
		checkSortColumnsSqlInjection(sortColumns);
		if ((sortColumns != null) && (sortColumns.length() > 200)) {
			throw new IllegalArgumentException(
					"sortColumns.length() <= 200 must be true");
		}
		this.sortColumns = sortColumns;
	}

	public void appendSortColumns(String sortColumns) {
		if ((this.sortColumns == null)
				|| ((this.sortColumns = this.sortColumns.trim()).length() == 0)) {
			setSortColumns(sortColumns);
		} else {
			setSortColumns(this.sortColumns + "," + sortColumns);
		}
	}

	public List<SortInfo> getSortInfos() {
		return Collections.unmodifiableList(SortInfo
				.parseSortColumns(this.sortColumns));
	}

	private void checkSortColumnsSqlInjection(String sortColumns) {
		if (sortColumns == null) {
			return;
		}
		if ((sortColumns.indexOf("'") >= 0) || (sortColumns.indexOf("\\") >= 0)) {
			throw new IllegalArgumentException("sortColumns:" + sortColumns
					+ " has SQL Injection risk");
		}
	}

	public String toString() {
		return "PageRequest [pageNumber=" + this.pageNumber + ", pageSize="
				+ this.pageSize + ", sortColumns=" + this.sortColumns + "]";
	}

	@Override
	public Map<String, Object> toMap() {
		
		try {
			if(domainMap == null) {
				this.map = PropertyUtils.describe(this);
			}else{
				this.map = domainMap.toMap();
				this.map.putAll(PropertyUtils.describe(this));
			}
		} catch (Exception e) {
			throw new DomainException(e);
		}
		return this.map;
	}
	
	public Map<String, Object> getMap() {
		if(this.map == null) {
			this.toMap();
		}
		return this.map;
	}
}
