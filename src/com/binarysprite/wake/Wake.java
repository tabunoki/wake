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
	 * true の場合、一覧表示モードで動作します。
	 */
	@Option(name = "-l", usage = "dispray list")
	boolean isList;

	/**
	 * インプットディレクトリです。
	 */
	@Option(name="-i", usage="input directory", metaVar="INPUT", required=true)
    private File input = new File("./");
	
	/**
	 * アウトプットディレクトリです。
	 */
	@Option(name="-o", usage="output directory", metaVar="OUTPUT", required=true)
    private File output = new File("./");
	
	/**
	 * サーバーの起動フラグです。
	 */
	@Option(name="-s", usage="start localhost server")
	private boolean startServer = false;
	
	/**
	 * ブラウザの起動フラグです。
	 */
	@Option(name="-c", usage="start browser")
	private boolean startClient = false;
	
	/**
	 * 表示確認用サーバーのポート番号です。
	 */
	@Option(name="-p", usage="server port", metaVar="PORT")
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
	 */
	public Wake() {
		/*
		 * do nothing.
		 */
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

		parser.setUsageWidth(80);

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
		 * 実処理
		 */
		if (isList) {
			WebBuilderMode.LIST.handle(WebBuilder.DIRECTORY, new WebBuilderParam(input, output));
		}
		WebBuilderMode.BUILD.handle(WebBuilder.DIRECTORY, new WebBuilderParam(input, output));
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				WebBuilderMode.BUILD.handle(WebBuilder.DIRECTORY, new WebBuilderParam(input, output));
			}
		}, 1, 2, TimeUnit.SECONDS);
		
		if (startServer) {
			this.startServer();
		}
		if (startClient) {
			this.startClient();
		}
	}
	
	/**
	 * 
	 */
	public void startServer() {
		
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
							
							byte[] messageBody = new byte[(int)file.length()];
							
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
	public void startClient() {
		
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
