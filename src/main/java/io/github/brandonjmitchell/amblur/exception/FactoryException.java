package io.github.brandonjmitchell.amblur.exception;

public class FactoryException extends Exception {

	private static final long serialVersionUID = 7038590131045839070L;

	public FactoryException(String errorMessage) {
		super(errorMessage);
	}
	
	public FactoryException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
	
	public FactoryException(Throwable cause) {
		super(cause);
	}
}
