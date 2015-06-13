package com.binarysprite.wake;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.binarysprite.wake.builder.DirectoryBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Wake（ウェイク）のメインクラスです。
 * 
 * @author Tabunoki
 * 
 */
public class Wake {

	/**
	 * true の場合、デバックモードで動作します。
	 */
	@Option(name = "-d", usage = "debug options")
	boolean isDebug;

	/**
	 * 入出力ファイルのルートです。
	 */
	@Option(name = "-r", usage = "root directory", metaVar = "ROOT", required = true)
	private File root;

	/**
	 * ソースディレクトリです。
	 */
	@Option(name = "-s", usage = "source directory", metaVar = "SOURCE")
	private String sourceDirectory = "source";

	/**
	 * パブリックディレクトリです。
	 */
	@Option(name = "-p", usage = "public directory", metaVar = "PUBLIC")
	private String publicDirectory = "public";

	/**
	 * サーバーの起動フラグです。
	 */
	@Option(name = "--server", usage = "start localhost server")
	private boolean startServer = false;

	/**
	 * ブラウザの起動フラグです。
	 */
	@Option(name = "--browser", usage = "start browser")
	private boolean startClient = false;

	/**
	 * 表示確認用サーバーのポート番号です。
	 */
	@Option(name = "--port", usage = "server port", metaVar = "PORT")
	private int port = 8080;

	/**
	 * メインメソッドです。
	 * @param args
	 */
	public static void main(String[] args) {
		new Wake().doMain(args);
	}

	/**
	 * 
	 * @param args
	 */
	public void doMain(String[] args) {

		/*
		 * 引数解析
		 */
		CmdLineParser parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);

			if (args.length == 0) {
				System.out.println("No argument is given.");
				parser.printUsage(System.out);
				System.out.println();
			}

		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.println("java -jar wake.jar [options...] arguments...");
			parser.printUsage(System.err);
			System.err.println();

			return;
		}
		
		/*
		 * ファイルインスタンスを作成
		 */
		File input = new File(root, sourceDirectory);
		File output = new File(root, publicDirectory);

		/*
		 * チェック処理
		 */
		if (input.exists() == false) {
			System.err.println("input directory is not exists. (" + input.getAbsolutePath() + ")");
			return;
		}

		/*
		 * 実処理
		 */
		final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		service.scheduleWithFixedDelay(new Watcher(input, output, new DirectoryBuilder()), 0, 1, TimeUnit.SECONDS);

		/*
		 * シャットダウン処理
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				System.out.println("Starting shutdown...");
				System.out.flush();

				service.shutdown();

				System.out.println("Done.");
				System.out.flush();
			}
		});

		if (startServer) {
			startServer(output);
		}

		if (startClient) {
			startClient(port);
		}
	}

	/**
	 * 
	 */
	public static void startServer(File output) {

		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

			server.createContext("/", new HttpHandler() {

				@Override
				public void handle(HttpExchange exchange) throws IOException {

					File file = new File(output.getAbsoluteFile(), exchange.getRequestURI().getPath());

					if (file.isDirectory()) {
						file = new File(file, "index.html");
					}

					if (file.isFile()) {
						exchange.sendResponseHeaders(200, 0);

						FileInputStream in = null;
						OutputStream out = null;
						try {
							in = new FileInputStream(file);
							out = exchange.getResponseBody();

							byte[] messageBody = new byte[(int) file.length()];

							in.read(messageBody);
							out.write(messageBody);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (in != null) {
								in.close();
							}
							if (out != null) {
								out.close();
							}
						}

					} else {
						exchange.sendResponseHeaders(404, 0);
					}
				}
			});
			server.start();

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public static void startClient(int port) {

		Desktop desktop = Desktop.getDesktop();

		try {
			desktop.browse(new URI("http://localhost:" + port + "/"));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
