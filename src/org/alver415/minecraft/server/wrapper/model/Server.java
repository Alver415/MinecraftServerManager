package org.alver415.minecraft.server.wrapper.model;

import java.nio.file.Path;
import java.util.Map;

public class Server {

	private String name;
	private Path directory;
	private Path minecraftJar;
	private String maximumMemory;
	private String initialMemory;
	private transient Map<String, String> propertyMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Path getDirectory() {
		return directory.toAbsolutePath();
	}

	public void setDirectory(Path directory) {
		this.directory = directory.toAbsolutePath();
	}

	public Path getMinecraftJar() {
		return minecraftJar.toAbsolutePath();
	}

	public void setMinecraftJar(Path minecraftJar) {
		this.minecraftJar = minecraftJar.toAbsolutePath();
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

	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

}
