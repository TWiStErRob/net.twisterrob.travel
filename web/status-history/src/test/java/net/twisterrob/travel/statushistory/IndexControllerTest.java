package net.twisterrob.travel.statushistory;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;

import net.twisterrob.java.io.IOTools;
import net.twisterrob.travel.statushistory.controller.IndexController;

import static net.twisterrob.travel.statushistory.HtmlValidator.assertValidHtml;

// TODO use @MicronautTest in JUnit 5.
public class IndexControllerTest {

	private static EmbeddedServer server;
	private static HttpClient client;

	@BeforeClass
	public static void setupServer() {
		server = ApplicationContext
				.run(EmbeddedServer.class);
		client = server
				.getApplicationContext()
				.createBean(HttpClient.class, server.getURL());
	}

	@AfterClass
	public static void stopServer() {
		if (server != null) {
			server.stop();
		}
		if (client != null) {
			client.stop();
		}
	}

	@Test
	public void testIndex() {
		HttpRequest<?> request = HttpRequest.GET("/");

		String body = client.toBlocking().retrieve(request);

		assertNotNull(body);
		assertTrue(body.contains("Better London Travel"));
		assertValidHtml(body);
	}

	@Test
	public void testFavicon() throws IOException {
		HttpRequest<?> request = HttpRequest.GET("/favicon.ico");

		byte[] body = client.toBlocking().retrieve(request, byte[].class);

		assertNotNull(body);
		byte[] expected = IOTools.readBytes(IndexController.class.getResourceAsStream("/public/favicon.ico"));
		assertArrayEquals(expected, body);
	}
}
