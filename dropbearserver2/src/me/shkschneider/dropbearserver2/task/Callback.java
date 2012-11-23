package me.shkschneider.dropbearserver2.task;

public interface Callback<T> {

	public static final int TASK_CHECK = 0;
	public static final int TASK_INSTALL = 1;
	public static final int TASK_START = 2;
	public static final int TASK_STOP = 3;
	public static final int TASK_REMOVE = 4;

	public void onTaskComplete(int id, T result);
}
