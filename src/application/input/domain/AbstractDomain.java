package application.input.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import application.input.InvalidValueException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({ @Type(value = StringDomain.class), @Type(value = IntegerDomain.class) })
public abstract class AbstractDomain<T> {

	protected Class<T> type;
	protected boolean required;

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

	public void validate(T value) throws InvalidValueException {
		if (required && value == null) {
			throw new InvalidValueException("Required field.");
		}
	}

}
