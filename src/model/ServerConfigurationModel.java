package model;

import java.io.File;
import java.util.Properties;

public class ServerConfigurationModel {

	private String serverName;
	private File serverDirectory;
	private File minecraftServerJar;
	private String maximumMemory;
	private String initialMemory;
	private Properties serverProperties;

	public ServerConfigurationModel() {
		this.serverName = "Minecraft Server";
		this.serverDirectory = new File(serverName);
		this.minecraftServerJar = new File("server.jar");
		this.maximumMemory = "2g";
		this.initialMemory = "512m";
		this.serverProperties = new Properties();
	}

	public File getServerDirectory() {
		return serverDirectory;
	}

	public void setServerDirectory(File serverDirectory) {
		this.serverDirectory = serverDirectory;
	}

	public File getMinecraftServerJar() {
		return minecraftServerJar;
	}

	public void setMinecraftServerJar(File minecraftServerJar) {
		this.minecraftServerJar = minecraftServerJar;
	}

	public String getMaximumMemory() {
		return maximumMemory;
	}

	public void setMaximumMemory(String xmx) {
		this.maximumMemory = xmx;
	}

	public String getInitialMemory() {
		return initialMemory;
	}

	public void setInitialMemory(String xms) {
		this.initialMemory = xms;
	}

	public Properties getServerProperties() {
		return serverProperties;
	}

	public void setServerProperties(Properties serverProperties) {
		this.serverProperties = serverProperties;
	}

	public File getServerPropertiesFile() {
		return new File(this.serverDirectory.getPath() + "/server.properties");
	}

}
