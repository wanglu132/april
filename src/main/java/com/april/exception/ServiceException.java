package com.april.exception;

/**
 * service层 抛出异常
 * @author WL
 *
 */
public class ServiceException extends RootException {

	private static final long serialVersionUID = 5226714746595860427L;

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	
}
