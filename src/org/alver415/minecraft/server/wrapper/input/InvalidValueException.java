package org.alver415.minecraft.server.wrapper.input;

public class InvalidValueException extends Exception {
	private static final long serialVersionUID = -6709530187599554313L;

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
