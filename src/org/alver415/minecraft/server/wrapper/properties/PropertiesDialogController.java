package org.alver415.minecraft.server.wrapper.properties;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.alver415.javafx.scene.control.input.InputCheckbox;
import org.alver415.javafx.scene.control.input.InputComboBox;
import org.alver415.javafx.scene.control.input.InputField;
import org.alver415.javafx.scene.control.input.InputTextField;
import org.alver415.javafx.scene.control.input.InputToggle;
import org.alver415.minecraft.server.wrapper.input.ConversionException;
import org.alver415.minecraft.server.wrapper.input.InvalidValueException;
import org.alver415.minecraft.server.wrapper.input.Property;
import org.alver415.minecraft.server.wrapper.input.domain.Domain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.TextFields;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PropertiesDialogController {

	private final Map<String, InputField<?>> fieldMap = new HashedMap<>();

	@FXML
	private VBox vbox;

	public void setProperties(Collection<Property<?>> properties) {
		for (Property<?> serverProperty : properties) {
			setupField(serverProperty);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void setupField(Property<T> serverProperty) {
		Domain<T> domain = serverProperty.getDomain();
		if (serverProperty.getType() == Boolean.class) {
			setupInputCheckBox((Property<Boolean>)serverProperty);
		} else if (domain != null && !CollectionUtils.isEmpty(domain.getOptions())) {
			setupInputComboBox(serverProperty);
		} else {
			setupInputTextField(serverProperty);
		}
	}

	private <T> void setupInputTextField(Property<T> serverProperty) {
		String key = serverProperty.getKey();
		String name = serverProperty.getName();
		T value = serverProperty.getValue();
		T defaultValue = serverProperty.getDefaultValue();

		InputTextField field = new InputTextField(name, toString(value), toString(defaultValue));
		fieldMap.put(key, field);

		Domain<T> domain = serverProperty.getDomain();
		if (domain != null) {
			TextFields.bindAutoCompletion(field.getTextField(), domain.getOptions());
		}

		field.getTextField().setOnKeyTyped(event -> {
			validate(serverProperty, field);
		});
		validate(serverProperty, field);

		vbox.getChildren().add(field);
	}

	private <T> void validate(Property<T> serverProperty, InputTextField field) {
		try {
			String input = getValue(field.getTextField());
			serverProperty.validate(input);
			field.getMessage().setText(null);
		} catch (InvalidValueException | ConversionException e) {
			field.getMessage().setText(e.getMessage());
		}
	}

	private <T> void setupInputCheckBox(Property<Boolean> serverProperty) {
		String key = serverProperty.getKey();
		String name = serverProperty.getName();
		Boolean value = serverProperty.getValue();
		Boolean defaultValue = serverProperty.getDefaultValue();

		Boolean val = value != null ? value : defaultValue;
		InputCheckbox field = new InputCheckbox(name, val);
		fieldMap.put(key, field);
		vbox.getChildren().add(field);
	}
	private <T> void setupInputToggle(Property<Boolean> serverProperty) {
		String key = serverProperty.getKey();
		String name = serverProperty.getName();
		Boolean value = serverProperty.getValue();
		Boolean defaultValue = serverProperty.getDefaultValue();

		Boolean val = value != null ? value : defaultValue;
		InputToggle field = new InputToggle(name, val);
		fieldMap.put(key, field);
		vbox.getChildren().add(field);
	}
	
	private <T> void setupInputComboBox(Property<T> serverProperty) {
		String key = serverProperty.getKey();
		String name = serverProperty.getName();
		T value = serverProperty.getValue();
		T defaultValue = serverProperty.getDefaultValue();

		Domain<T> domain = serverProperty.getDomain();
		List<T> options = domain.getOptions();

		InputComboBox<T> field = new InputComboBox<>(name, options, value != null ? value : defaultValue);
		fieldMap.put(key, field);
		vbox.getChildren().add(field);
	}

	public Properties asProperties() {
		Properties properties = new Properties();
		for (Entry<String, InputField<?>> entry : fieldMap.entrySet()) {
			String value = toString(entry.getValue().getValue());
			properties.setProperty(entry.getKey(), value);
		}
		return properties;
	}

	private String getValue(TextField textField) {
		String userValue = textField.getText();
		String defaultValue = textField.getPromptText();
		String value = userValue != null ? userValue : defaultValue;
		return toString(value);
	}

	private String toString(Object value) {
		return value != null ? value.toString() : StringUtils.EMPTY;
	}

}