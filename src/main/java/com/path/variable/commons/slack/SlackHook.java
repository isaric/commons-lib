package com.path.variable.commons.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.path.variable.commons.slack.exceptions.MessagingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Collections.singletonMap;

public class SlackHook {
	
	private final ObjectMapper mapper;

	private final String url;

	private final HttpClient client;

	public SlackHook(String url) {
		this.url = url;
		this.mapper = new ObjectMapper();
		this.client = HttpClientBuilder.create().build();
	}

	public void sendPlainText(String text) {
		var payload = singletonMap("text", (Object) text);
		sendPayload(payload);
	}

	public void sendMessage(SlackMessage message) {
		sendPayload(message);
	}

	private void sendPayload(Object payload) {
		HttpPost post = new HttpPost();
		try {
			post.setURI(new URI(url));
			var entity = new StringEntity(mapper.writeValueAsString(payload));
			post.setEntity(entity);
			post.setHeader("content-type", "application/json");
			client.execute(post);
		} catch (URISyntaxException | IOException ex) {
			throw new MessagingException("Slack message could not be sent", ex);
		}
	}

	public String getUrl() {
		return url;
	}
}
