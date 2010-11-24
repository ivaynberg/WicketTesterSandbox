package com.vaynberg.tester;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterConfig;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class HtmlUnitTest {
	private static List<Timer> timers;

	private Server server;
	private Context js;
	private Scriptable scope;

	private WicketTester tester;

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

	ServletTester st;

	@Before
	public void createServletTester() throws Exception {
		Timer timer=timer("servlet-tester-startup");
		st = new ServletTester();
		st.setContextPath("/");
		st.addServlet(ConfiguredServlet.class, "/*");
		st.addServlet(DefaultServlet.class, "/");
		st.start();
		timer.stop();
	}

	@After
	public void destroyServletTester() throws Exception {
		Timer timer=timer("servlet-tester-shutdown");
		st.stop();
		timer.stop();
	}

	public static class ConfiguredServlet extends WicketServlet {
		
		@Override
		protected WicketFilter newWicketFilter() {
			return new WicketFilter() {

				@Override
				protected String getFilterPathFromConfig(
						FilterConfig filterConfig) {
					return "";
				}

				@Override
				protected IWebApplicationFactory getApplicationFactory() {
					return new IWebApplicationFactory() {

						public WebApplication createApplication(
								WicketFilter filter) {
							return new WicketApplication();
						}

						public void destroy() {

						}

					};
				}
			};
		}
	}

	private class LocalConnection implements WebConnection {

		public WebResponse getResponse(WebRequest wrequest) throws IOException {
			HttpTester request = new HttpTester();
			request.setURI(wrequest.getUrl().toString()
					.substring("http://localhost".length()));
			request.setMethod(wrequest.getHttpMethod().name());
			request.setVersion("HTTP/1.1");
			request.setHeader("Host", "localhost");

			try {
				String buffer = request.generate();
				buffer = st.getResponses(buffer);
				HttpTester response = new HttpTester();
				response.parse(buffer);

				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				Enumeration names=response.getHeaderNames();
				while (names.hasMoreElements()) {
					String name=(String) names.nextElement();
					headers.add(new NameValuePair(name, response.getHeader(name)));
				}
				String content=response.getContent();
				
				WebResponseData data = new WebResponseData(
						(content!=null)?content.getBytes():null, response.getStatus(),
						response.getReason(), headers);
				WebResponse wr = new WebResponse(data, wrequest, 100);
				return wr;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	public void testThis() throws Exception {
		Timer timer = timer("first-request");

		Timer tmp=timer("first-page-render");
		WebClient client = new WebClient();
		client.setRedirectEnabled(true);
		client.setWebConnection(new LocalConnection());

		client.setAjaxController(new NicelyResynchronizingAjaxController());
		Page rawpage = client
				.getPage("http://localhost/wicket/bookmarkable/com.vaynberg.tester.HomePage");
		HtmlPage page = (HtmlPage) rawpage;
		HtmlAnchor anchor = (HtmlAnchor) page.getElementById("link");
		HtmlSpan span = (HtmlSpan) page.getElementById("counter");
		assertEquals("0", span.getTextContent().trim());
		tmp.stop();
		
		tmp=timer("first-click");
		page = anchor.click();
		client.waitForBackgroundJavaScript(10000);
		span = (HtmlSpan) page.getElementById("counter");
		assertEquals("1", span.getTextContent().trim());
		tmp.stop();
		
		tmp=timer("second-click");
		page = anchor.click();
		client.waitForBackgroundJavaScript(10000);
		span = (HtmlSpan) page.getElementById("counter");
		assertEquals("2", span.getTextContent().trim());
		tmp.stop();
	
		client.closeAllWindows();
		
		timer.stop();
		
	
	}

}
