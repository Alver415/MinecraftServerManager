package org.alver415.minecraft.server.wrapper.input.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import org.alver415.minecraft.server.wrapper.input.InvalidValueException;

public abstract class AbstractDomain<T> implements Domain<T>{

	protected Class<T> type;
	protected boolean required;
	protected T defaultValue;
	protected List<T> options = new ArrayList<>();

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public List<T> getOptions() {
		return options;
	}

	public void setOptions(List<T> options) {
		this.options = options;
	}

	public void validate(T value) throws InvalidValueException {
		if (required && value == null) {
			throw new InvalidValueException("Required field.");
		}
		if (!CollectionUtils.isEmpty(options) && !options.contains(value)) {
			throw new InvalidValueException("does not match options=" + options.toString());
		}
	}

}
