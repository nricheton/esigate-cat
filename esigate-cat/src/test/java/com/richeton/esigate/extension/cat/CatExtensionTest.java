package com.richeton.esigate.extension.cat;


import static org.esigate.test.TestUtils.createHttpResponse;
import static org.esigate.test.TestUtils.createMockDriver;
import static org.esigate.test.TestUtils.createRequest;
import static org.esigate.test.TestUtils.driverProxy;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.extension.Esi;
import org.esigate.test.conn.SequenceResponse;
import org.esigate.test.driver.AbstractDriverTestCase;
import org.junit.Assert;
import org.junit.Test; 

public class CatExtensionTest extends AbstractDriverTestCase {

	/**
	 * Test for the custom error page extension.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * 
	 * @throws Exception
	 */
	@Test
	public void testErrorPage() throws IOException, URISyntaxException {
		// Configuration with our custom extension
		Properties properties = new Properties();
		properties.put(Parameters.REMOTE_URL_BASE, "http://localhost.mydomain.fr/");
		properties.put(Parameters.EXTENSIONS, Esi.class.getName() + "," + CatExtension.class.getName());

		// Build driver and request.
		Driver driver = createMockDriver(properties,
		// Setup error response.
				new SequenceResponse().response(createHttpResponse().status(500).reason("Failed").build()));

		try {
			driverProxy(driver,
			// Request
					createRequest("http://test.mydomain.fr/foobar/").build());
			fail("HttpErrorPage expected");
		} catch (HttpErrorPage errorPage) {
			HttpResponse response = errorPage.getHttpResponse();
			Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("https://http.cat/500"));
		}
	}

}
