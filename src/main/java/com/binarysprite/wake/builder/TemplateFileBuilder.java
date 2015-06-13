package com.binarysprite.wake.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.binarysprite.wake.Builder;
import com.binarysprite.wake.BuilderParam;

/**
 * 
 * @author Tabunoki
 *
 */
public class TemplateFileBuilder implements Builder {

	/**
	 * 
	 */
	private final Map<String, String> template;

	/**
	 * 
	 * @param template
	 */
	public TemplateFileBuilder(Map<String, String> template) {
		super();
		this.template = template;
	}

	@Override
	public void build(BuilderParam param) {

		/*
		 * 読み込み
		 */
		String tempalteString = read(param.inputFile);

		/*
		 * バインド
		 */
		String[] fileNameElement = param.outputFile.getName().split("[@|.]");
		if (fileNameElement.length == 4) {

			tempalteString = template.get(fileNameElement[1]).replace("<wake:body>", tempalteString);

		}

		template.put(fileNameElement[0], tempalteString);
	}

	/**
	 * 指定のファイルを読み込み、文字列として返します。
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	protected String read(File inputFile) {

		final StringBuilder builder = new StringBuilder();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));

			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return builder.toString();
	}

}
