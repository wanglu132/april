package com.april.exception;

/**
 * 自定义 根 运行时异常
 * @author WL
 *
 */
public class RootException extends RuntimeException {

	private static final long serialVersionUID = 6222887214327681472L;

	public RootException(String message, Throwable cause) {
		super(message, cause);
	}

	public RootException(String message) {
		super(message);
	}

	public RootException(Throwable cause) {
		super(cause);
	}
	
}
