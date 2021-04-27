package org.alver415.minecraft.server.wrapper.input.domain;

import org.alver415.minecraft.server.wrapper.input.InvalidValueException;

public class StringDomain extends AbstractDomain<String> {

	{
		type = String.class;
	}

	protected String regex;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@Override
	public void validate(String value) throws InvalidValueException {
		super.validate(value);
		if (regex != null && !value.matches(regex)) {
			throw new InvalidValueException("does not match regex=" + regex);
		}
	}

}