package org.alver415.minecraft.server.wrapper.input;

public class ConversionException extends Exception {
	private static final long serialVersionUID = 5622894485285147259L;

	public ConversionException(String string) {
		super(string);
	}

	public ConversionException(String string, Exception e) {
		super(string, e);
	}

}
