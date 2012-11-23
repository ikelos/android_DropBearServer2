package me.shkschneider.dropbearserver2.task;

public interface Callback<T> {

	public void onTaskComplete(T result);
}
