package com.binarysprite.wake;

import java.io.File;

/**
 * 
 * @author Tabunoki
 *
 */
public class WebBuilderParam {

	/**
	 * 入力ディレクトリのルートです。
	 */
	public final File inputRoot;

	/**
	 * 出力ディレクトリのルートです。
	 */
	public final File outputRoot;

	/**
	 * 入力ファイルです。
	 */
	public final File inputFile;

	/**
	 * 出力ファイルです。
	 */
	public final File outputFile;

	/**
	 * コンストラクタです。
	 * @param inputRoot
	 * @param outputRoot
	 * @param inputFile
	 */
	public WebBuilderParam(File inputRoot, File outputRoot, File inputFile) {
		super();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.inputFile = inputFile;
		this.outputFile = new File(inputFile.getAbsolutePath()
				.replace(inputRoot.getAbsolutePath(), outputRoot.getAbsolutePath())
				.replace(".md", ".html")
				.replaceAll("@[^\\.]+", ""));
	}

	/**
	 * コンストラクタです。
	 * @param param
	 * @param inputFile
	 */
	public WebBuilderParam(WebBuilderParam param, File inputFile) {
		this(param.inputRoot, param.outputRoot, inputFile);
	}

	/**
	 * コンストラクタです。
	 * @param inputRoot
	 * @param outputRoot
	 */
	public WebBuilderParam(File inputRoot, File outputRoot) {
		this(inputRoot, outputRoot, inputRoot);
	}

	@Override
	public String toString() {
		return "WebBuilderParam [inputRoot=" + inputRoot + ", outputRoot="
				+ outputRoot + ", inputFile=" + inputFile + ", outputFile="
				+ outputFile + "]";
	}
}
