package com.binarysprite.wake;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * 
 * @author Tabunoki
 *
 */
public enum WebBuilder implements FileFilter {

	/**
	 * ディレクトリビルダーです。
	 */
	DIRECTORY {
		@Override
		public void list(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			/*
			 * ディレクトリに対する処理
			 */
			System.out.println("[D]: " + param.inputFile.getAbsolutePath());
			
			/*
			 * ディレクトリの子要素に対する処理
			 */
			handleChild(webBuilderMode, param);
		}

		@Override
		public void build(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			/*
			 * ディレクトリに対する処理
			 */
			if (param.outputFile.exists() == false) {
				param.outputFile.mkdirs();
			}
			
			/*
			 * ディレクトリの子要素に対する処理
			 */
			handleChild(webBuilderMode, param);
		}
		

		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

		@Override
		public File getOutputFile(WebBuilderParam param, File inputFile) {
			
			return new File(inputFile.getAbsolutePath()
					.replace(param.inputRoot.getAbsolutePath(), param.outputRoot.getAbsolutePath()));
		}
	},
	
	/**
	 * コンテンツファイルビルダーです。
	 */
	CONTENT_FILE {
		@Override
		public void list(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			System.out.println("[C]: " + param.inputFile.getAbsolutePath());
			
		}

		@Override
		public void build(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			/*
			 * 読み込み
			 */
			String contentString = null;
			try {
				contentString = read(param.inputFile);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			
			/*
			 * コンテンツファイル内の変数を退避
			 */
			final Map<String, String> variables = new HashMap<String, String>();
			final Pattern pattern = Pattern.compile("^(<wake:[a-z]+>)(.*?)</wake:[a-z]+>", Pattern.MULTILINE);
			final Matcher matcher = pattern.matcher(contentString);
			
			while (matcher.find()) {
				variables.put(matcher.group(1), matcher.group(2));
				contentString = contentString.replaceAll(matcher.group(0), "");
			}
			
			/*
			 * Markdown 変換と、バインド
			 */
			MarkdownProcessor processor = new MarkdownProcessor();
			
			String[] fileNameElement = param.inputFile.getName().split("[@|.]");
			if (fileNameElement.length == 3) {
				/*
				 * テンプレート指定がある場合は markdown してからバインドする
				 */
				contentString = TEMPALTE_MAP.get(fileNameElement[1]).replace("<wake:body>", processor.markdown(contentString));
				
				/*
				 * 変数をバインド
				 */
				for (String tag : variables.keySet()) {
					contentString = contentString.replaceAll(tag, variables.get(tag));
				}
				
			} else {
				/*
				 * テンプレート指定がない場合は markdown のみ
				 */
				contentString = processor.markdown(contentString);
			}
			
			/*
			 * 書き込み
			 */
			try {
				write(param.outputFile, contentString);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".md");
		}

		@Override
		public File getOutputFile(WebBuilderParam param, File inputFile) {
			
			return new File(inputFile.getAbsolutePath()
					.replace(param.inputRoot.getAbsolutePath(), param.outputRoot.getAbsolutePath())
					.replace(".md", ".html")
					.replaceAll("@[^\\.]+", ""));
		}
	},
	
	/**
	 * テンプレートファイルビルダーです。
	 */
	TEMPLATE_FILE {
		@Override
		public void list(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			System.out.println("[T]: " + param.inputFile.getAbsolutePath());
			
		}

		@Override
		public void build(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			/*
			 * 読み込み
			 */
			String tempalteString = null;
			try {
				tempalteString = read(param.inputFile);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			
			/*
			 * バインド
			 */
			String[] fileNameElement = param.outputFile.getName().split("[@|.]");
			if (fileNameElement.length == 4) {
				
				tempalteString = TEMPALTE_MAP.get(fileNameElement[1]).replace("<wake:body>", tempalteString);
				
			}
			
			TEMPALTE_MAP.put(fileNameElement[0], tempalteString);
		}

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".tmpl.html");
		}

		@Override
		public File getOutputFile(WebBuilderParam param, File inputFile) {
			
			return new File(inputFile.getAbsolutePath()
					.replace(param.inputRoot.getAbsolutePath(), param.outputRoot.getAbsolutePath()));
		}
	},
	
	/**
	 * リソースファイルビルダーです。
	 */
	RESOURCE_FILE {
		@Override
		public void list(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			System.out.println("[R]: " + param.inputFile.getAbsolutePath());
			
		}

		@Override
		public void build(WebBuilderMode webBuilderMode, WebBuilderParam param) {
			
			FileChannel srcChannel = null;
			FileChannel destChannel = null;
			try {
				srcChannel = new FileInputStream(param.inputFile).getChannel();
				destChannel = new FileOutputStream(param.outputFile).getChannel();
				
				srcChannel.transferTo(0, srcChannel.size(), destChannel);
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} finally {
				if (srcChannel != null) {
					try {
						srcChannel.close();
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
				if (destChannel != null) {
					try {
						destChannel.close();
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}
				
			}
		}

		@Override
		public boolean accept(File pathname) {
			return !CONTENT_FILE.accept(pathname) && !TEMPLATE_FILE.accept(pathname) && !DIRECTORY.accept(pathname);
		}

		@Override
		public File getOutputFile(WebBuilderParam param, File inputFile) {
			
			return new File(inputFile.getAbsolutePath()
					.replace(param.inputRoot.getAbsolutePath(), param.outputRoot.getAbsolutePath()));
		}
	};
	
	/**
	 * テンプレートマップです。
	 * テンプレート名をキーにし、テンプレートの内容を値として持ちます。
	 */
	private static final Map<String, String> TEMPALTE_MAP = new HashMap<String, String>();
	
	/**
	 * 一覧を表示するモードの実行メソッドです。
	 * @param webBuilderMode
	 * @param param
	 */
	public abstract void list(WebBuilderMode webBuilderMode, WebBuilderParam param);
	
	/**
	 * HTMLファイルをビルドするモードの実行メソッドです。
	 * @param webBuilderMode
	 * @param param
	 */
	public abstract void build(WebBuilderMode webBuilderMode, WebBuilderParam param);
	
	/**
	 * 
	 * @param param
	 * @param inputFile
	 * @return
	 */
	public abstract File getOutputFile(WebBuilderParam param, File inputFile);

	/**
	 * 子ファイルをハンドルします。
	 * 処理の順番は以下のとおりです。
	 * <ol>
	 * <ul>テンプレートファイル<ul>
	 * <ul>コンテンツファイル<ul>
	 * <ul>リソースファイル<ul>
	 * <ul>ディレクトリ<ul>
	 * </ol>
	 * @param webBuilderMode
	 * @param file
	 */
	protected void handleChild(WebBuilderMode webBuilderMode, WebBuilderParam param) {
		
		for (File file : param.inputFile.listFiles(TEMPLATE_FILE)) {
			webBuilderMode.handle(TEMPLATE_FILE, new WebBuilderParam(
					param.inputRoot, param.outputRoot, file, TEMPLATE_FILE.getOutputFile(param, file)));
		}
		
		for (File file : param.inputFile.listFiles(CONTENT_FILE)) {
			webBuilderMode.handle(CONTENT_FILE, new WebBuilderParam(
					param.inputRoot, param.outputRoot, file, CONTENT_FILE.getOutputFile(param, file)));
		}
		
		for (File file : param.inputFile.listFiles(RESOURCE_FILE)) {
			webBuilderMode.handle(RESOURCE_FILE, new WebBuilderParam(
					param.inputRoot, param.outputRoot, file, RESOURCE_FILE.getOutputFile(param, file)));
		}
		
		for (File file : param.inputFile.listFiles(DIRECTORY)) {
			webBuilderMode.handle(DIRECTORY, new WebBuilderParam(
					param.inputRoot, param.outputRoot, file, DIRECTORY.getOutputFile(param, file)));
		}
	}

	/**
	 * 指定のファイルを読み込み、文字列として返します。
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	protected String read(File inputFile) throws IOException {
		
		final StringBuilder builder = new StringBuilder();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), /*"JISAutoDetect"*/ "UTF-8"));
			
			String line = null;
			while((line = reader.readLine()) != null){
				builder.append(line);
				builder.append("\n");
			}
			
			
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * 指定の文字列を、指定のファイルに書き込みます。
	 * @param outputFile
	 * @param string
	 * @throws IOException
	 */
	protected void write(File outputFile, String string) throws IOException {
		
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)), "UTF-8");
			
			writer.write(string);
			
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
