package com.binarysprite.wake.builder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pegdown.PegDownProcessor;

import com.binarysprite.wake.Builder;
import com.binarysprite.wake.BuilderParam;

/**
 * 
 * @author Tabunoki
 *
 */
public class ContentFileBuilder implements Builder {

	/**
	 * 
	 */
	private final Map<String, String> template;

	/**
	 * 
	 * @param template
	 */
	public ContentFileBuilder(Map<String, String> template) {
		super();
		this.template = template;
	}

	@Override
	public void build(BuilderParam param) {

		/*
		 * 読み込み
		 */
		String contentString = read(param.inputFile);

		/*
		 * コンテンツファイル内の変数を退避
		 */
		final Map<String, String> variables = new HashMap<String, String>();
		final Pattern pattern = Pattern.compile("^@(.*)=(.*)$", Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(contentString);

		while (matcher.find()) {
			variables.put(matcher.group(1).trim(), matcher.group(2).trim());
			contentString = contentString.replaceAll(matcher.group(0), "");
		}

		/*
		 * Markdown 変換と、バインド
		 */
		PegDownProcessor processor = new PegDownProcessor();

		String[] fileNameElement = param.inputFile.getName().split("[@|.]");
		if (fileNameElement.length == 3) {
			/*
			 * テンプレート指定がある場合は markdown してからバインドする
			 */
			String templateString = template.get(fileNameElement[1]);

			if (templateString == null || templateString.isEmpty()) {
				System.err.println("template file (" + fileNameElement[1] + ") is not exists.");
			} else {

				try {
					contentString = template.get(fileNameElement[1])
							.replaceAll("<wake:body>.?</wake:body>", processor.markdownToHtml(contentString));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * 変数をバインド
			 */
			for (String valuable : variables.keySet()) {
				contentString = contentString.replaceAll("\\{\\{" + valuable + "\\}\\}", variables.get(valuable));
			}

		} else {
			/*
			 * テンプレート指定がない場合は markdown のみ
			 */
			contentString = processor.markdownToHtml(contentString);
		}

		/*
		 * 書き込み
		 */
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(param.outputFile)), "UTF-8");

			writer.write(contentString);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

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
