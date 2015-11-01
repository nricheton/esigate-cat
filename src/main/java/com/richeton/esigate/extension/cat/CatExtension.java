package com.richeton.esigate.extension.cat;

import java.util.Properties;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.Extension;
import org.esigate.test.http.HttpResponseBuilder;

public class CatExtension implements Extension, IEventListener {

	public boolean event(EventDefinition id, Event event) {

		ProxyEvent proxyEvent = (ProxyEvent) event;

		// In case of error
		if (proxyEvent.getErrorPage() != null) {

			// Build the new error page content
			StringBuilder sb = new StringBuilder();

			sb.append("<html><head><title>HTTP error</title></head>");
			sb.append("<body style=\"background-color:x	 #000; text-align: center; padding-top: 50px;\">");
			sb.append("<img src=\"https://http.cat/"
					+ proxyEvent.getErrorPage().getHttpResponse().getStatusLine().getStatusCode()
					+ ".jpg\"/></body></html>");

			// replace
			proxyEvent
					.setErrorPage(
							new HttpErrorPage(new HttpResponseBuilder()
									.status(proxyEvent.getErrorPage().getHttpResponse().getStatusLine().getStatusCode())
									.reason(proxyEvent.getErrorPage().getHttpResponse().getStatusLine()
											.getReasonPhrase())
					.entity(new StringEntity(sb.toString(), ContentType.TEXT_HTML)).build()));

		}
		return true;
	}

	public void init(Driver driver, Properties properties) {
		driver.getEventManager().register(EventManager.EVENT_PROXY_POST, this);
	}

}
