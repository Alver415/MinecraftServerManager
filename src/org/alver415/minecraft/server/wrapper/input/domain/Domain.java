package org.alver415.minecraft.server.wrapper.input.domain;

import java.util.List;

import org.alver415.minecraft.server.wrapper.input.InvalidValueException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({ @Type(value = StringDomain.class), @Type(value = BooleanDomain.class),
		@Type(value = IntegerDomain.class) })
public interface Domain<T> {

	Class<T> getType();

	T getDefaultValue();

	void validate(T value) throws InvalidValueException;

	List<T> getOptions();

}
