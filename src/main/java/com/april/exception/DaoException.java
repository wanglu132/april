package com.april.exception;

/**
 * DAO层 抛出异常
 * @author WL
 *
 */
public class DaoException extends RootException {

	private static final long serialVersionUID = 5226714746595860427L;

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

	
}
