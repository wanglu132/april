package com.april.exception;

public class DomainException extends RootException {
	
	private static final long serialVersionUID = -8341028849430253676L;
	
	public DomainException(Throwable cause) {
		super(cause);
	}

	public DomainException(String message, Throwable cause) {
		super(message, cause);
	}

}
