package com.april.exception;

public class LocalTransactionException extends RootException {

	private static final long serialVersionUID = 4092340972070875385L;

	public LocalTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocalTransactionException(String message) {
		super(message);
	}

	public LocalTransactionException(Throwable cause) {
		super(cause);
	}

}
