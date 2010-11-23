package com.vaynberg.tester;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends WebPage {
	private int counter = 0;

	public HomePage(final PageParameters parameters) {

		// Add the simplest type of label
		final Component label;
		add(label = new Label("message", new PropertyModel(this, "counter"))
				.setOutputMarkupId(true).setMarkupId("counter"));

		AjaxLink link = new AjaxLink("link") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				counter++;
				target.addComponent(label);
				System.out.println("CLICKED!");
			}
			
			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator() {
			return new IAjaxCallDecorator() {
				
				public CharSequence decorateScript(Component component, CharSequence script) {
					return "print('hello');"+script;
				}
				
				public CharSequence decorateOnSuccessScript(Component component,
						CharSequence script) {
					return script;
				}
				
				public CharSequence decorateOnFailureScript(Component component,
						CharSequence script) {
					return script;
				}
			};
			}
		};
		link.setOutputMarkupId(true);
		link.setMarkupId("link");
		add(link);

	}

}
