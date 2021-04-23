package application.properties;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.alver415.javafx.scene.control.TextFieldForm;
import org.apache.commons.collections4.map.HashedMap;

import application.Utils;
import application.input.ConversionException;
import application.input.InvalidValueException;
import application.input.ServerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class PropertiesDialogController implements Initializable {
	
	private final Map<String, TextField> textFieldMap = new HashedMap<>();
	private final Map<String, String> validationMessages = new HashedMap<>();
	@FXML
	private Dialog<ButtonType> dialog;

	@FXML
	private TextFieldForm textFieldForm;

	@FXML
	private Text validationMessageText;

	@FXML
	private ButtonType acceptButtonType;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Collection<ServerProperty<?>> serverProperties = Utils.loadServerProperties();
		for (ServerProperty<?> serverProperty : serverProperties) {
			addField(serverProperty);
		}
	}

	public <T> void addField(ServerProperty<T> serverProperty) {
		String key = serverProperty.getKey();
		String name = serverProperty.getName();
		String defaultValue = toString(serverProperty.getDefaultValue());

		TextField textField = new TextField();
		textField.setId(key);
		textField.setPromptText(defaultValue);
		textField.setOnKeyTyped((event) -> {
			String input = textField.getText();
			try {
				serverProperty.validate(input);
				validationMessages.remove(key);
			} catch (InvalidValueException | ConversionException e) {
				validationMessages.put(key, e.getMessage());
				event.consume();
			} finally {
				StringBuilder sb = new StringBuilder();
				for (Entry<String, String> validationMessage : validationMessages.entrySet()) {
					sb.append(String.format("%s: %s\n", validationMessage.getKey(), validationMessage.getValue()));
				}
				validationMessageText.setText(sb.toString());
			}
		});
		
		TextFieldForm.setLabel(textField, name);
		textFieldForm.getTextFields().add(textField);
		
		textFieldMap.put(key, textField);
	}
	
	public void setField(String key, String value) {
		TextField textField = textFieldMap.get(key);
		textField.setText(value);
	}

	private String toString(Object value) {
		return value == null ? null : value.toString();
	}

	public Properties asProperties() {
		Properties properties = new Properties();
		for (TextField textField : textFieldForm.getTextFields()) {
			properties.setProperty(textField.getId(), textField.getText());
		}
		return properties;
	}


}
