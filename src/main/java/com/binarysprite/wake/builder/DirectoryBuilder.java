package com.binarysprite.wake.builder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.binarysprite.wake.Builder;
import com.binarysprite.wake.BuilderParam;

/**
 * 
 * @author Tabunoki
 *
 */
public class DirectoryBuilder implements Builder {

	/**
	 * 
	 */
	private final Map<String, String> template;

	/**
	 * 
	 */
	public DirectoryBuilder() {
		this(new HashMap<String, String>());
	}

	/**
	 * 
	 * @param template
	 */
	public DirectoryBuilder(Map<String, String> template) {
		super();
		this.template = template;
	}

	@Override
	public void build(BuilderParam param) {

		if (param.outputFile.exists() == false) {
			param.outputFile.mkdirs();
		}

		for (File child : param.inputFile.listFiles()) {

			BuilderParam childParam = new BuilderParam(param.inputRoot, param.outputRoot, child);

			final boolean directory = child.isDirectory();
			final boolean file = child.isFile();
			final boolean content = child.getName().endsWith(".md");
			final boolean template = child.getName().endsWith(".tmpl.html");

			if (directory) {
				/*
				 * Directory
				 */
				new DirectoryBuilder(this.template).build(childParam);

			} else if (file && content) {
				/*
				 * Content File
				 */
				new ContentFileBuilder(this.template).build(childParam);

			} else if (file && template) {
				/*
				 * Template File
				 */
				new TemplateFileBuilder(this.template).build(childParam);

			} else {
				/*
				 * Resource File
				 */
				new ResourceFileBuilder().build(childParam);

			}
		}
	}
}
