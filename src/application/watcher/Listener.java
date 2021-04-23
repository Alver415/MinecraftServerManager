package application.watcher;

public interface Listener<T> {
	public void handle(T event);
}