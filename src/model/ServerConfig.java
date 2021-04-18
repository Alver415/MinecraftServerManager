package model;

import java.nio.file.Path;
import java.util.Properties;

public class ServerConfig {

	private String serverName;
	private Path serverDirectory;
	private Path minecraftServerJar;
	private String maximumMemory;
	private String initialMemory;
	private Properties serverProperties;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Path getServerDirectory() {
		return serverDirectory;
	}

	public void setServerDirectory(Path serverDirectory) {
		this.serverDirectory = serverDirectory;
	}

	public Path getMinecraftServerJar() {
		return minecraftServerJar;
	}

	public void setMinecraftServerJar(Path minecraftServerJar) {
		this.minecraftServerJar = minecraftServerJar;
	}

	public String getMaximumMemory() {
		return maximumMemory;
	}

	public void setMaximumMemory(String maximumMemory) {
		this.maximumMemory = maximumMemory;
	}

	public String getInitialMemory() {
		return initialMemory;
	}

	public void setInitialMemory(String initialMemory) {
		this.initialMemory = initialMemory;
	}

	public Properties getServerProperties() {
		return serverProperties;
	}

	public void setServerProperties(Properties serverProperties) {
		this.serverProperties = serverProperties;
	}

	@Override
	public String toString() {
		return "ServerConfig [serverName=" + serverName + ", serverDirectory=" + serverDirectory
				+ ", minecraftServerJar=" + minecraftServerJar + ", maximumMemory=" + maximumMemory + ", initialMemory="
				+ initialMemory + ", serverProperties=" + serverProperties + "]";
	}

}
