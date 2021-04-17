package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

	public static Properties loadProperties(File file) {
		Properties properties = new Properties();
		try {
			InputStream inputStream = new FileInputStream(file);
			properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return properties;
	}

	public static void storeProperties(Properties props, File file, String comment) {
		try {
			if (!file.exists()) {
				File folder = file.getParentFile();
				if (!folder.exists()) {
					folder.mkdirs();
				}
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			props.store(writer, comment);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
