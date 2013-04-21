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
	 */
	public WebBuilderParam(File inputRoot, File outputRoot) {
		this(inputRoot, outputRoot, inputRoot, outputRoot);
	}
	
	/**
	 * コンストラクタです。
	 * @param inputRoot
	 * @param outputRoot
	 * @param inputFile
	 * @param outputFile
	 */
	public WebBuilderParam(File inputRoot, File outputRoot, File inputFile,
			File outputFile) {
		super();
		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}

	@Override
	public String toString() {
		return "WebBuilderParam [inputRoot=" + inputRoot + ", outputRoot="
				+ outputRoot + ", inputFile=" + inputFile + ", outputFile="
				+ outputFile + "]";
	}
}
