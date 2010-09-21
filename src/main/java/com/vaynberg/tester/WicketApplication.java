package com.vaynberg.tester;

import org.apache.wicket.mock.MockSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;
import org.apache.wicket.util.ValueProvider;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see com.vaynberg.tester.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	/**
	 * Constructor
	 */
	public WicketApplication() {
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	public String getConfigurationType() {
		return DEPLOYMENT;
	}

	@Override
	protected void init() {
		super.init();
		setSessionStoreProvider(ValueProvider.<ISessionStore>of(new MockSessionStore()));
	}

}
