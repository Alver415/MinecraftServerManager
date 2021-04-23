package application.input;

import application.Utils;
import application.input.domain.AbstractDomain;

public class ServerProperty<T> {

	protected String key;
	protected String name;
	protected Class<T> type;
	protected T defaultValue;
	protected AbstractDomain<T> domain;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}
	
	public AbstractDomain<T> getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain<T> domain) {
		this.domain = domain;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void validate(String input) throws InvalidValueException, ConversionException {
		if (domain != null) {
			T converted = Utils.convert(input, domain.getType());
			domain.validate(converted);
		}
	}
}
