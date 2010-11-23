package com.vaynberg.tester;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class HtmlUnitTest {
	private static List<Timer> timers;

	private Server server;
	private Context js;
	private Scriptable scope;

	@BeforeClass
	public static void initTimers() {
		timers = new ArrayList<Timer>();
		timer("overall");
	}

	@AfterClass
	public static void printTimingInfo() {
		for (Timer timer : timers) {
			System.out.println(timer);
		}
	}

	private static Timer timer(String name) {
		Timer timer = new Timer(name);
		timers.add(timer);
		return timer;
	}

	@Before
	public void startServer() throws Exception {
		Timer timer = timer("server-startup");
		server = new Server();
		SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar("src/main/webapp");

	
		server.addHandler(bb);

		server.start();
		
		
		timer.stop();
	}

	@After
	public void stopServer() throws Exception {
		Timer timer = timer("server-shutdown");
		server.stop();
		server.join();
		timer.stop();
	}

	@Test
	public void testThis() throws Exception {
		Timer timer = timer("request1");
		
		WebClient client=new WebClient();
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		HtmlPage page=client.getPage("http://localhost:8080");

		HtmlAnchor anchor=(HtmlAnchor) page.getElementById("link");
		HtmlSpan span=(HtmlSpan) page.getElementById("counter");
		assertEquals("0", span.getTextContent().trim());
		page=anchor.click();
		client.waitForBackgroundJavaScript(10000);
		span=(HtmlSpan) page.getElementById("counter");
		assertEquals("1", span.getTextContent().trim());
		
		timer.stop();
	}

}
