package org.alver415.minecraft.server.wrapper.properties;

import org.alver415.minecraft.server.wrapper.Utils;
import org.alver415.minecraft.server.wrapper.input.ConversionException;
import org.alver415.minecraft.server.wrapper.input.InvalidValueException;
import org.alver415.minecraft.server.wrapper.input.domain.Domain;

public class ServerProperty<T> {

	protected String key;
	protected String name;
	protected Class<T> type;
	protected Domain<T> domain;
	protected T value;

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

	public Domain<T> getDomain() {
		return domain;
	}

	public void setDomain(Domain<T> domain) {
		this.domain = domain;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T defaultValue) {
		this.value = defaultValue;
	}

	/* == Helper Methods == */

	public T getDefaultValue() {
		return domain == null ? null : domain.getDefaultValue();
	}

	public void validate(T input) throws InvalidValueException, ConversionException {
		if (domain != null) {
			domain.validate(input);
		}
	}

	public void coerceValue(String stringValue) throws ConversionException {
		T converted = Utils.convert(stringValue, type);
		this.setValue(converted);
	}

}
