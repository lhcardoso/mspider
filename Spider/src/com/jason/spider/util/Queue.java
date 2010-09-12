package com.jason.spider.util;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {

	private static LinkedList<String> queue = new LinkedList<String>();

	static ReentrantLock lock = new ReentrantLock();

	public static  void add(String t) {
		if (queue.contains(t)) {
			return;
		}
		queue.addLast(t);
		
	}

	public synchronized static String get() {

		//lock.lock();
		String url = queue.removeFirst();
		//lock.unlock();
		return url;
	}

	public static boolean isEmpty() {
		return queue.isEmpty();
	}

}
