package org.alver415.minecraft.server.wrapper.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathWatcher {

	private static final Logger LOGGER = LogManager.getLogger(PathWatcher.class);

	private final WatchService watchService;
	private final Map<WatchKey, Map<Kind<?>, Collection<Listener<WatchEvent<Path>>>>> map;

	public PathWatcher() throws IOException {
		this.watchService = FileSystems.getDefault().newWatchService();
		this.map = new ConcurrentHashMap<>();

		@SuppressWarnings("unchecked")
		Thread thread = new Thread(() -> {
			while (true) {
				try {
					WatchKey key = watchService.take();
					Map<Kind<?>, Collection<Listener<WatchEvent<Path>>>> kindMap = map.get(key);
					for (WatchEvent<?> event : key.pollEvents()) {
						Kind<?> kind = event.kind();
						if (kindMap.containsKey(kind)) {
							kindMap.get(kind).forEach((listener) -> 
								new Thread(() -> listener.handle((WatchEvent<Path>) event)).start());
						}
					}
					boolean reset = key.reset();
					if (!reset) {
						LOGGER.debug("Failed to reset WatchKey for Path: " + kindMap);
					}
				} catch (Exception e) {
					LOGGER.error("Exception in PathWatcher thread: ", e);
				}
			}
		});
		thread.setName(this.getClass().getSimpleName());
		thread.setDaemon(true);
		thread.start();
	}

	public void register(Path path, Kind<?> kind, Listener<WatchEvent<Path>> listener) throws IOException {
		register(path, Arrays.asList(kind), listener);
	}

	public void register(Path path, Collection<Kind<?>> kinds, Listener<WatchEvent<Path>> listener) throws IOException {
		WatchKey key = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
		for (Kind<?> kind : kinds) {
			if (!map.containsKey(key)) {
				map.put(key, new ConcurrentHashMap<>());
			} else {
				map.get(key);
			}
			Map<Kind<?>, Collection<Listener<WatchEvent<Path>>>> kindMap = map.get(key);

			if (!kindMap.containsKey(kind)) {
				kindMap.put(kind, new ConcurrentLinkedQueue<>());
			}
			Collection<Listener<WatchEvent<Path>>> listeners = kindMap.get(kind);
			listeners.add(listener);
		}
	}

}
