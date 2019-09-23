package io.github.brandonjmitchell.amblur.exception;

public class ParserException extends Exception {

	private static final long serialVersionUID = 8300287087357422686L;

	public ParserException(String errorMessage) {
		super(errorMessage);
	}
	
	public ParserException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
	
	public ParserException(Throwable cause) {
		super(cause);
	}
	
}
