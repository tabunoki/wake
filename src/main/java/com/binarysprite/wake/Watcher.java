package com.binarysprite.wake;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tabunoki
 *
 */
public class Watcher implements Runnable {

	/**
	 * 
	 */
	private final File inputRoot;

	/**
	 * 
	 */
	private final File outputRoot;

	/**
	 * 
	 */
	private final Builder builder;

	/**
	 * 
	 */
	private Map<String, Long> map = new HashMap<>();

	/**
	 * 
	 */
	private Map<String, Long> previousMap = new HashMap<>();

	/**
	 * 
	 * @param inputRoot
	 * @param outputRoot
	 * @param builder
	 */
	public Watcher(File inputRoot, File outputRoot, Builder builder) {
		super();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.builder = builder;
	}

	@Override
	public void run() {

		if (!isUpToDate()) {

			/*
			 * 
			 */

			/*
			 * 
			 */
			builder.build(new BuilderParam(inputRoot, outputRoot));
		}

	}

	/**
	 * 
	 * @return
	 */
	public boolean isUpToDate() {

		boolean upToDate = true;

		map = new HashMap<String, Long>();

		watch(inputRoot);

		for (String path : map.keySet()) {

			if (previousMap.get(path) == null) {

				/*
				 * 新規
				 */
				upToDate = false;

				System.out.println("C: " + path);

			} else if (map.get(path) > previousMap.get(path)) {

				/*
				 * 更新
				 */
				upToDate = false;

				System.out.println("M: " + path);
			}
		}

		for (String path : previousMap.keySet()) {

			if (map.get(path) == null) {

				/*
				 * 削除
				 */
				upToDate = false;

				System.out.println("D: " + path);

			}
		}

		previousMap = map;

		return upToDate;
	}

	/**
	 * 
	 * @param current
	 */
	private void watch(File current) {

		map.put(current.getAbsolutePath(), current.lastModified());

		if (current.isDirectory()) {
			for (File child : current.listFiles()) {
				watch(child);
			}
		}
	}

}
