package com.vaynberg.tester;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.util.file.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

public class RhinoTest {
	private static List<Timer> timers;

	private Server server;
	private Context js;
	private Scriptable scope;

//	@BeforeClass
	public static void initTimers() {
		timers = new ArrayList<Timer>();
		timer("overall");
	}

//	@AfterClass
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

//	@Before
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

//	@After
	public void stopServer() throws Exception {
		Timer timer = timer("server-shutdown");
		server.stop();
		server.join();
		timer.stop();
	}

//	@Before
	public void startJavascript() throws FileNotFoundException, IOException {
		Timer timer = timer("javascript-startup");

		final File envjs = getEnvJsDistDir();
		if (!envjs.exists()) {
			throw new IllegalStateException("cannot find envjs base dir: "
					+ envjs);
		}

		final File rhino = new File(envjs, "env.rhino.js");

		Global global = new Global();
		js = ContextFactory.getGlobal().enterContext();
		global.init(js);
		js.setOptimizationLevel(-1);
		js.setLanguageVersion(Context.VERSION_1_5);
		scope = js.initStandardObjects(global);
		js.evaluateReader(scope, new FileReader(rhino),
				rhino.getAbsolutePath(), 1, null);

		timer.stop();
	}

	private File getEnvJsDistDir() {
		return new File("c:/dev/env-js/git/dist");
	}

//	@After
	public void stopJavascript() {
		Timer timer = timer("javascript-shutdown");
		Context.exit();
		timer.stop();
	}

//	@Test
	public void testThis() throws Exception {
		Timer timer = timer("request1");
		final File exec = new File("src/main/webapp/exec.js");
		js.evaluateReader(scope, new FileReader(exec), exec.getAbsolutePath(),
				1, null);

		// js.evaluateString(
		// scope,
		// "window.location='http://localhost:8080/wicket/bookmarkable/com.mycompany.HomePage';",
		// "<get page code>", 1, null);
		//
		// js.evaluateString(scope, "print(document.innerHTML);",
		// "<get page code>", 1, null);
		//
		// js.evaluateString(
		// scope,
		// "var wcall=wicketAjaxGet('../page;jsessionid=11ojowxhd6wsr1g9812cf8yk1p?0-1.IBehaviorListener.0-link',function() { }.bind(this),function() { }.bind(this), function() {return Wicket.$('link') != null;}.bind(this));",
		// "<ajax code>", 1, null);
		// js.evaluateString(scope,
		// "print(document.getElementById('link').innerHTML);",
		// "<click code>", 1, null);

		timer.stop();
	}

}
