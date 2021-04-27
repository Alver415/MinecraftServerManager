package org.alver415.minecraft.server.wrapper.model;

import java.util.Map;

import org.alver415.minecraft.server.wrapper.Utils;
import org.alver415.minecraft.server.wrapper.properties.ServerProperty;

public class ServerProperties {

	public static final Map<String, ServerProperty<?>> DEFAULT_SERVER_PROPERTIES = Utils.loadServerProperties();

	public static final ServerProperty<String> SERVER_IP = getDefault("server-ip");
	public static final ServerProperty<Integer> SERVER_PORT = getDefault("server-port");
	public static final ServerProperty<Integer> QUERY_PORT = getDefault("query.port");
	public static final ServerProperty<Integer> RCON_PORT = getDefault("rcon.port");
	public static final ServerProperty<String> RCON_PASSWORD = getDefault("rcon.password");
	public static final ServerProperty<String> DIFFICULTY = getDefault("difficulty");
	public static final ServerProperty<Boolean> HARDCORE = getDefault("hardcore");
	public static final ServerProperty<String> GAMEMODE = getDefault("gamemode");
	public static final ServerProperty<Boolean> FORCE_GAMEMODE = getDefault("force-gamemode");
	public static final ServerProperty<Boolean> PVP = getDefault("pvp");
	public static final ServerProperty<Boolean> ALLOW_FLIGHT = getDefault("allow-flight");
	public static final ServerProperty<Boolean> ALLOW_NETHER = getDefault("allow-nether");
	public static final ServerProperty<Boolean> SPAWN_ANIMALS = getDefault("spawn-animals");
	public static final ServerProperty<Boolean> SPAWN_MONSTERS = getDefault("spawn-monsters");
	public static final ServerProperty<Boolean> SPAWN_NPCS = getDefault("spawn-npcs");
	public static final ServerProperty<Boolean> SPAWN_PROTECTION = getDefault("spawn-protection");
	public static final ServerProperty<String> MOTD = getDefault("motd");
	public static final ServerProperty<String> LEVEL_NAME = getDefault("level-name");
	public static final ServerProperty<String> LEVEL_SEED = getDefault("level-seed");
	public static final ServerProperty<String> LEVEL_TYPE = getDefault("level-type");
	public static final ServerProperty<Boolean> GENERATE_STRUCTURES = getDefault("generate-structures");
	public static final ServerProperty<String> GENERATOR_SETTINGS = getDefault("generator-settings");
	public static final ServerProperty<Integer> VIEW_DISTANCE = getDefault("view-distance");
	public static final ServerProperty<Integer> MAX_BUILD_HEIGHT = getDefault("max-build-height");
	public static final ServerProperty<Integer> MAX_PLAYERS = getDefault("max-players");
	public static final ServerProperty<Integer> MAX_TICK_TIME = getDefault("max-tick-time");
	public static final ServerProperty<Integer> MAX_WORLD_SIZE = getDefault("max-world-size");
	public static final ServerProperty<Boolean> WHITE_LIST = getDefault("white-list");
	public static final ServerProperty<Boolean> ENFORCE_WHITELIST = getDefault("enforce-whitelist");
	public static final ServerProperty<String> RESOURCE_PACK = getDefault("resource-pack");
	public static final ServerProperty<String> RESOURCE_PACK_SHA1 = getDefault("resource-pack-sha1");
	public static final ServerProperty<Boolean> REQUIRE_RESOURCE_PACK = getDefault("require-resource-pack");
	public static final ServerProperty<Boolean> BROADCAST_CONSOLE_TO_OPS = getDefault("broadcast-console-to-ops");
	public static final ServerProperty<Boolean> BROADCAST_RCON_TO_OPS = getDefault("broadcast-rcon-to-ops");
	public static final ServerProperty<Boolean> ENABLE_COMMAND_BLOCK = getDefault("enable-command-block");
	public static final ServerProperty<Boolean> ENABLE_JMX_MONITORING = getDefault("enable-jmx-monitoring");
	public static final ServerProperty<Boolean> ENABLE_RCON = getDefault("enable-rcon");
	public static final ServerProperty<Boolean> ENABLE_STATUS = getDefault("enable-status");
	public static final ServerProperty<Boolean> ENABLE_QUERY = getDefault("enable-query");
	public static final ServerProperty<Boolean> SYNC_CHUNK_WRITES = getDefault("sync-chunk-writes");
	public static final ServerProperty<Integer> ENTITY_BROADCAST_RANGE_PERCENTAGE = getDefault(
			"entity-broadcast-range-percentage");
	public static final ServerProperty<Integer> FUNCTION_PERMISSION_LEVEL = getDefault("function-permission-level");
	public static final ServerProperty<Integer> NETWORK_COMPRESSION_THRESHOLD = getDefault(
			"network-compression-threshold");
	public static final ServerProperty<Boolean> ONLINE_MODE = getDefault("online-mode");
	public static final ServerProperty<Integer> OP_PERMISSION_LEVEL = getDefault("op-permission-level");
	public static final ServerProperty<Integer> PLAYER_IDLE_TIMEOUT = getDefault("player-idle-timeout");
	public static final ServerProperty<Boolean> PREVENT_PROXY_CONNECTIONS = getDefault("prevent-proxy-connections");
	public static final ServerProperty<Integer> RATE_LIMIT = getDefault("rate-limit");
	public static final ServerProperty<Boolean> SNOOPER_ENABLED = getDefault("snooper-enabled");
	public static final ServerProperty<String> TEXT_FILTERING_CONFIG = getDefault("text-filtering-config");
	public static final ServerProperty<Boolean> USE_NATIVE_TRANSPORT = getDefault("use-native-transport");

	public static final Map<String, ServerProperty<?>> SERVER_PROPERTIES = Utils.loadServerProperties();

	private ServerProperty<String> serverIp = get("server-ip");
	private ServerProperty<Integer> serverPort = get("server-port");
	private ServerProperty<Integer> queryPort = get("query.port");
	private ServerProperty<Integer> rconPort = get("rcon.port");
	private ServerProperty<String> rconPassword = get("rcon.password");
	private ServerProperty<String> difficulty = get("difficulty");
	private ServerProperty<Boolean> hardcore = get("hardcore");
	private ServerProperty<String> gamemode = get("gamemode");
	private ServerProperty<Boolean> forceGamemode = get("force-gamemode");
	private ServerProperty<Boolean> pvp = get("pvp");
	private ServerProperty<Boolean> allowFlight = get("allow-flight");
	private ServerProperty<Boolean> allowNether = get("allow-nether");
	private ServerProperty<Boolean> spawnAnimals = get("spawn-animals");
	private ServerProperty<Boolean> spawnMonsters = get("spawn-monsters");
	private ServerProperty<Boolean> spawnNpcs = get("spawn-npcs");
	private ServerProperty<Boolean> spawnProtection = get("spawn-protection");
	private ServerProperty<String> motd = get("motd");
	private ServerProperty<String> levelName = get("level-name");
	private ServerProperty<String> levelSeed = get("level-seed");
	private ServerProperty<String> levelType = get("level-type");
	private ServerProperty<Boolean> generateStructures = get("generate-structures");
	private ServerProperty<String> generatorSettings = get("generator-settings");
	private ServerProperty<Integer> viewDistance = get("view-distance");
	private ServerProperty<Integer> maxBuildHeight = get("max-build-height");
	private ServerProperty<Integer> maxPlayers = get("max-players");
	private ServerProperty<Integer> maxTickTime = get("max-tick-time");
	private ServerProperty<Integer> maxWorldSize = get("max-world-size");
	private ServerProperty<Boolean> whiteList = get("white-list");
	private ServerProperty<Boolean> enforceWhitelist = get("enforce-whitelist");
	private ServerProperty<String> resourcePack = get("resource-pack");
	private ServerProperty<String> resourcePackSha1 = get("resource-pack-sha1");
	private ServerProperty<Boolean> requireResourceRack = get("require-resource-pack");
	private ServerProperty<Boolean> broadcastConsoleToOps = get("broadcast-console-to-ops");
	private ServerProperty<Boolean> broadcastRconToOps = get("broadcast-rcon-to-ops");
	private ServerProperty<Boolean> enableCommandBlock = get("enable-command-block");
	private ServerProperty<Boolean> enableJmxMonitoring = get("enable-jmx-monitoring");
	private ServerProperty<Boolean> enableRcon = get("enable-rcon");
	private ServerProperty<Boolean> enableStatus = get("enable-status");
	private ServerProperty<Boolean> enableQuery = get("enable-query");
	private ServerProperty<Boolean> syncChunkWrites = get("sync-chunk-writes");
	private ServerProperty<Integer> entityBroadcastRangePercentage = get("entity-broadcast-range-percentage");
	private ServerProperty<Integer> functionPermissionLevel = get("function-permission-level");
	private ServerProperty<Integer> networkCompressionThreshold = get("network-compression-threshold");
	private ServerProperty<Boolean> onlineMode = get("online-mode");
	private ServerProperty<Integer> opPermissionLevel = get("op-permission-level");
	private ServerProperty<Integer> playerIdleTimeout = get("player-idle-timeout");
	private ServerProperty<Boolean> preventProxyConnections = get("prevent-proxy-connections");
	private ServerProperty<Integer> rateLimit = get("rate-limit");
	private ServerProperty<Boolean> snooperEnabled = get("snooper-enabled");
	private ServerProperty<String> textFilteringConfig = get("text-filtering-config");
	private ServerProperty<Boolean> useNativeTransport = get("use-native-transport");

	@SuppressWarnings("unchecked")
	protected static <T> ServerProperty<T> getDefault(String key) {
		return (ServerProperty<T>) DEFAULT_SERVER_PROPERTIES.get(key);
	}

	@SuppressWarnings("unchecked")
	protected <T> ServerProperty<T> get(String key) {
		return (ServerProperty<T>) SERVER_PROPERTIES.get(key);
	}

	public ServerProperty<String> getServerIp() {
		return serverIp;
	}

	public void setServerIp(ServerProperty<String> serverIp) {
		this.serverIp = serverIp;
	}

	public ServerProperty<Integer> getServerPort() {
		return serverPort;
	}

	public void setServerPort(ServerProperty<Integer> serverPort) {
		this.serverPort = serverPort;
	}

	public ServerProperty<Integer> getQueryPort() {
		return queryPort;
	}

	public void setQueryPort(ServerProperty<Integer> queryPort) {
		this.queryPort = queryPort;
	}

	public ServerProperty<Integer> getRconPort() {
		return rconPort;
	}

	public void setRconPort(ServerProperty<Integer> rconPort) {
		this.rconPort = rconPort;
	}

	public ServerProperty<String> getRconPassword() {
		return rconPassword;
	}

	public void setRconPassword(ServerProperty<String> rconPassword) {
		this.rconPassword = rconPassword;
	}

	public ServerProperty<String> getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(ServerProperty<String> difficulty) {
		this.difficulty = difficulty;
	}

	public ServerProperty<Boolean> getHardcore() {
		return hardcore;
	}

	public void setHardcore(ServerProperty<Boolean> hardcore) {
		this.hardcore = hardcore;
	}

	public ServerProperty<String> getGamemode() {
		return gamemode;
	}

	public void setGamemode(ServerProperty<String> gamemode) {
		this.gamemode = gamemode;
	}

	public ServerProperty<Boolean> getForceGamemode() {
		return forceGamemode;
	}

	public void setForceGamemode(ServerProperty<Boolean> forceGamemode) {
		this.forceGamemode = forceGamemode;
	}

	public ServerProperty<Boolean> getPvp() {
		return pvp;
	}

	public void setPvp(ServerProperty<Boolean> pvp) {
		this.pvp = pvp;
	}

	public ServerProperty<Boolean> getAllowFlight() {
		return allowFlight;
	}

	public void setAllowFlight(ServerProperty<Boolean> allowFlight) {
		this.allowFlight = allowFlight;
	}

	public ServerProperty<Boolean> getAllowNether() {
		return allowNether;
	}

	public void setAllowNether(ServerProperty<Boolean> allowNether) {
		this.allowNether = allowNether;
	}

	public ServerProperty<Boolean> getSpawnAnimals() {
		return spawnAnimals;
	}

	public void setSpawnAnimals(ServerProperty<Boolean> spawnAnimals) {
		this.spawnAnimals = spawnAnimals;
	}

	public ServerProperty<Boolean> getSpawnMonsters() {
		return spawnMonsters;
	}

	public void setSpawnMonsters(ServerProperty<Boolean> spawnMonsters) {
		this.spawnMonsters = spawnMonsters;
	}

	public ServerProperty<Boolean> getSpawnNpcs() {
		return spawnNpcs;
	}

	public void setSpawnNpcs(ServerProperty<Boolean> spawnNpcs) {
		this.spawnNpcs = spawnNpcs;
	}

	public ServerProperty<Boolean> getSpawnProtection() {
		return spawnProtection;
	}

	public void setSpawnProtection(ServerProperty<Boolean> spawnProtection) {
		this.spawnProtection = spawnProtection;
	}

	public ServerProperty<String> getMotd() {
		return motd;
	}

	public void setMotd(ServerProperty<String> motd) {
		this.motd = motd;
	}

	public ServerProperty<String> getLevelName() {
		return levelName;
	}

	public void setLevelName(ServerProperty<String> levelName) {
		this.levelName = levelName;
	}

	public ServerProperty<String> getLevelSeed() {
		return levelSeed;
	}

	public void setLevelSeed(ServerProperty<String> levelSeed) {
		this.levelSeed = levelSeed;
	}

	public ServerProperty<String> getLevelType() {
		return levelType;
	}

	public void setLevelType(ServerProperty<String> levelType) {
		this.levelType = levelType;
	}

	public ServerProperty<Boolean> getGenerateStructures() {
		return generateStructures;
	}

	public void setGenerateStructures(ServerProperty<Boolean> generateStructures) {
		this.generateStructures = generateStructures;
	}

	public ServerProperty<String> getGeneratorSettings() {
		return generatorSettings;
	}

	public void setGeneratorSettings(ServerProperty<String> generatorSettings) {
		this.generatorSettings = generatorSettings;
	}

	public ServerProperty<Integer> getViewDistance() {
		return viewDistance;
	}

	public void setViewDistance(ServerProperty<Integer> viewDistance) {
		this.viewDistance = viewDistance;
	}

	public ServerProperty<Integer> getMaxBuildHeight() {
		return maxBuildHeight;
	}

	public void setMaxBuildHeight(ServerProperty<Integer> maxBuildHeight) {
		this.maxBuildHeight = maxBuildHeight;
	}

	public ServerProperty<Integer> getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(ServerProperty<Integer> maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public ServerProperty<Integer> getMaxTickTime() {
		return maxTickTime;
	}

	public void setMaxTickTime(ServerProperty<Integer> maxTickTime) {
		this.maxTickTime = maxTickTime;
	}

	public ServerProperty<Integer> getMaxWorldSize() {
		return maxWorldSize;
	}

	public void setMaxWorldSize(ServerProperty<Integer> maxWorldSize) {
		this.maxWorldSize = maxWorldSize;
	}

	public ServerProperty<Boolean> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(ServerProperty<Boolean> whiteList) {
		this.whiteList = whiteList;
	}

	public ServerProperty<Boolean> getEnforceWhitelist() {
		return enforceWhitelist;
	}

	public void setEnforceWhitelist(ServerProperty<Boolean> enforceWhitelist) {
		this.enforceWhitelist = enforceWhitelist;
	}

	public ServerProperty<String> getResourcePack() {
		return resourcePack;
	}

	public void setResourcePack(ServerProperty<String> resourcePack) {
		this.resourcePack = resourcePack;
	}

	public ServerProperty<String> getResourcePackSha1() {
		return resourcePackSha1;
	}

	public void setResourcePackSha1(ServerProperty<String> resourcePackSha1) {
		this.resourcePackSha1 = resourcePackSha1;
	}

	public ServerProperty<Boolean> getRequireResourceRack() {
		return requireResourceRack;
	}

	public void setRequireResourceRack(ServerProperty<Boolean> requireResourceRack) {
		this.requireResourceRack = requireResourceRack;
	}

	public ServerProperty<Boolean> getBroadcastConsoleToOps() {
		return broadcastConsoleToOps;
	}

	public void setBroadcastConsoleToOps(ServerProperty<Boolean> broadcastConsoleToOps) {
		this.broadcastConsoleToOps = broadcastConsoleToOps;
	}

	public ServerProperty<Boolean> getBroadcastRconToOps() {
		return broadcastRconToOps;
	}

	public void setBroadcastRconToOps(ServerProperty<Boolean> broadcastRconToOps) {
		this.broadcastRconToOps = broadcastRconToOps;
	}

	public ServerProperty<Boolean> getEnableCommandBlock() {
		return enableCommandBlock;
	}

	public void setEnableCommandBlock(ServerProperty<Boolean> enableCommandBlock) {
		this.enableCommandBlock = enableCommandBlock;
	}

	public ServerProperty<Boolean> getEnableJmxMonitoring() {
		return enableJmxMonitoring;
	}

	public void setEnableJmxMonitoring(ServerProperty<Boolean> enableJmxMonitoring) {
		this.enableJmxMonitoring = enableJmxMonitoring;
	}

	public ServerProperty<Boolean> getEnableRcon() {
		return enableRcon;
	}

	public void setEnableRcon(ServerProperty<Boolean> enableRcon) {
		this.enableRcon = enableRcon;
	}

	public ServerProperty<Boolean> getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(ServerProperty<Boolean> enableStatus) {
		this.enableStatus = enableStatus;
	}

	public ServerProperty<Boolean> getEnableQuery() {
		return enableQuery;
	}

	public void setEnableQuery(ServerProperty<Boolean> enableQuery) {
		this.enableQuery = enableQuery;
	}

	public ServerProperty<Boolean> getSyncChunkWrites() {
		return syncChunkWrites;
	}

	public void setSyncChunkWrites(ServerProperty<Boolean> syncChunkWrites) {
		this.syncChunkWrites = syncChunkWrites;
	}

	public ServerProperty<Integer> getEntityBroadcastRangePercentage() {
		return entityBroadcastRangePercentage;
	}

	public void setEntityBroadcastRangePercentage(ServerProperty<Integer> entityBroadcastRangePercentage) {
		this.entityBroadcastRangePercentage = entityBroadcastRangePercentage;
	}

	public ServerProperty<Integer> getFunctionPermissionLevel() {
		return functionPermissionLevel;
	}

	public void setFunctionPermissionLevel(ServerProperty<Integer> functionPermissionLevel) {
		this.functionPermissionLevel = functionPermissionLevel;
	}

	public ServerProperty<Integer> getNetworkCompressionThreshold() {
		return networkCompressionThreshold;
	}

	public void setNetworkCompressionThreshold(ServerProperty<Integer> networkCompressionThreshold) {
		this.networkCompressionThreshold = networkCompressionThreshold;
	}

	public ServerProperty<Boolean> getOnlineMode() {
		return onlineMode;
	}

	public void setOnlineMode(ServerProperty<Boolean> onlineMode) {
		this.onlineMode = onlineMode;
	}

	public ServerProperty<Integer> getOpPermissionLevel() {
		return opPermissionLevel;
	}

	public void setOpPermissionLevel(ServerProperty<Integer> opPermissionLevel) {
		this.opPermissionLevel = opPermissionLevel;
	}

	public ServerProperty<Integer> getPlayerIdleTimeout() {
		return playerIdleTimeout;
	}

	public void setPlayerIdleTimeout(ServerProperty<Integer> playerIdleTimeout) {
		this.playerIdleTimeout = playerIdleTimeout;
	}

	public ServerProperty<Boolean> getPreventProxyConnections() {
		return preventProxyConnections;
	}

	public void setPreventProxyConnections(ServerProperty<Boolean> preventProxyConnections) {
		this.preventProxyConnections = preventProxyConnections;
	}

	public ServerProperty<Integer> getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(ServerProperty<Integer> rateLimit) {
		this.rateLimit = rateLimit;
	}

	public ServerProperty<Boolean> getSnooperEnabled() {
		return snooperEnabled;
	}

	public void setSnooperEnabled(ServerProperty<Boolean> snooperEnabled) {
		this.snooperEnabled = snooperEnabled;
	}

	public ServerProperty<String> getTextFilteringConfig() {
		return textFilteringConfig;
	}

	public void setTextFilteringConfig(ServerProperty<String> textFilteringConfig) {
		this.textFilteringConfig = textFilteringConfig;
	}

	public ServerProperty<Boolean> getUseNativeTransport() {
		return useNativeTransport;
	}

	public void setUseNativeTransport(ServerProperty<Boolean> useNativeTransport) {
		this.useNativeTransport = useNativeTransport;
	}

}
