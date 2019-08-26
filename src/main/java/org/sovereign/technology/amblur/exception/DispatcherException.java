package org.sovereign.technology.amblur.exception;

public class DispatcherException extends Exception {

	private static final long serialVersionUID = 5438386326682345466L;

	public DispatcherException(String errorMessage) {
		super(errorMessage);
	}
	
	public DispatcherException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
	
	public DispatcherException(Throwable cause) {
		super(cause);
	}
}
