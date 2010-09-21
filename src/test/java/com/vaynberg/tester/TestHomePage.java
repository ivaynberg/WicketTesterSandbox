package com.vaynberg.tester;

import junit.framework.TestCase;
import org.apache.wicket.util.tester.WicketTester;

import com.vaynberg.tester.HomePage;
import com.vaynberg.tester.WicketApplication;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase {
	private WicketTester tester;

	private Timer timer = new Timer("overall");

	@Override
	public void setUp() {
		timer.reset();
		tester = new WicketTester(new WicketApplication());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		timer.stop();
		System.out.println(timer);
	}

	public void testRenderMyPage() {
		// start and render the test page
		tester.startPage(HomePage.class);

		// assert rendered page class
		tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		tester.assertLabel("message",
				"If you see this message wicket is properly configured and running");
		
		tester.executeAjaxEvent("link", "onclick");
	}
}
