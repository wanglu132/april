package com.april.data.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Page<T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = -8665555313317034802L;
	protected final List<T> result = new ArrayList<T>();
	protected  int pageSize;
	protected  int pageNumber;
	private  int totalCount;
	private  String sortColumns;

	public Page() {
		super();
	}

	public Page(PageRequest p, int totalCount) {
		if (p.getPageSize() <= 0) {
			throw new IllegalArgumentException(
					"[pageSize] must great than zero");
		}
		this.pageSize = p.getPageSize();
		this.pageNumber = PageUtils.computePageNumber(p.getPageNumber(),
				this.pageSize, totalCount);
		this.totalCount = totalCount;
		this.sortColumns = p.getSortColumns();
	}

	public void setResult(List<T> elements) {
		if (elements == null) {
			throw new IllegalArgumentException("'result' must be not null");
		}
		this.result.addAll(elements);
	}

	public List<T> getResult() {
		return this.result;
	}

	public boolean isFirstPage() {
		return getThisPageNumber() == 1;
	}

	public boolean isLastPage() {
		return getThisPageNumber() >= getLastPageNumber();
	}

	public boolean isHasNextPage() {
		return getLastPageNumber() > getThisPageNumber();
	}

	public boolean isHasPreviousPage() {
		return getThisPageNumber() > 1;
	}

	public int getLastPageNumber() {
		return PageUtils.computeLastPageNumber(this.totalCount, this.pageSize);
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public int getThisPageFirstElementNumber() {
		return (getThisPageNumber() - 1) * getPageSize();
	}

	public int getThisPageLastElementNumber() {
		int fullPage = getThisPageFirstElementNumber() + getPageSize();
		return getTotalCount() < fullPage ? getTotalCount() : fullPage;
	}

	public int getNextPageNumber() {
		return getThisPageNumber() + 1;
	}

	public int getPreviousPageNumber() {
		return getThisPageNumber() - 1;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getThisPageNumber() {
		return this.pageNumber;
	}

	public Integer[] getLinkPageNumbers() {
		return linkPageNumbers(7);
	}

	public Integer[] linkPageNumbers(int count) {
		return PageUtils.generateLinkPageNumbers(getThisPageNumber(),
				getLastPageNumber(), count);
	}

	public int getFirstResult() {
		return PageUtils.getFirstResult(this.pageNumber, this.pageSize);
	}

	public String getSortColumns() {
		return this.sortColumns;
	}

	public String toString() {
		return "Page [pageSize=" + this.pageSize + ", pageNumber="
				+ this.pageNumber + ", totalCount=" + this.totalCount + "]"
				+ "getThisPageFirstElementNumber: "
				+ getThisPageFirstElementNumber()
				+ ", getThisPageLastElementNumber: "
				+ getThisPageLastElementNumber();
	}

	public Iterator<T> iterator() {
		return this.result.iterator();
	}
}
