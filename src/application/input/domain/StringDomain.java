package application.input.domain;

import java.util.List;

import application.input.InvalidValueException;

public class StringDomain extends AbstractDomain<String> {

	{
		type = String.class;
	}

	protected List<String> options;
	protected String regex;

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@Override
	public void validate(String value) throws InvalidValueException {
		super.validate(value);
		boolean contains = options.contains(value);
		boolean match = regex != null && !value.matches(regex);
		if (!contains && !match) {
			throw new InvalidValueException("does not match options=" + options.toString() + " or match regex=" + regex);
		}
	}

}