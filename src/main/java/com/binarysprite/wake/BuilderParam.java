package com.binarysprite.wake;

import java.io.File;

/**
 * 
 * @author Tabunoki
 *
 */
public class BuilderParam {

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
	public BuilderParam(File inputRoot, File outputRoot) {

		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.inputFile = inputRoot;
		this.outputFile = outputRoot;
	}

	/**
	 * コンストラクタです。
	 * @param inputRoot
	 * @param outputRoot
	 * @param inputFile
	 */
	public BuilderParam(File inputRoot, File outputRoot, File inputFile) {

		this.inputRoot = inputRoot;
		this.outputRoot = outputRoot;
		this.inputFile = inputFile;
		this.outputFile = new File(inputFile.getAbsolutePath()
				.replace(inputRoot.getAbsolutePath(), outputRoot.getAbsolutePath())
				.replace(".md", ".html")
				.replaceAll("@[^@|^.]*", ""));
	}

	@Override
	public String toString() {
		return "WebBuilderParam [inputRoot=" + inputRoot + ", outputRoot="
				+ outputRoot + ", inputFile=" + inputFile + ", outputFile="
				+ outputFile + "]";
	}
}
