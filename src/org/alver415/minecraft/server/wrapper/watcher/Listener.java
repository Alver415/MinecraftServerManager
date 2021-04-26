package org.alver415.minecraft.server.wrapper.watcher;

public interface Listener<T> {
	public void handle(T event);
}