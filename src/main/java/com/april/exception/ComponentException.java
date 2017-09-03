package com.april.exception;

/**
 * Component 层抛出异常
 * @author WL
 *
 */
public class ComponentException extends RootException {

	private static final long serialVersionUID = 4643153598269581511L;

	public ComponentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComponentException(String message) {
		super(message);
	}

	public ComponentException(Throwable cause) {
		super(cause);
	}
}
