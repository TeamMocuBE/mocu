package com.example.mocu;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MocuApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocuApplication.class, args);
	}

	/*
	@Bean
	public ServletWebServerFactory serverFactory() {
		TomcatServletWebServerFactory tomcatServletWebServerFactory
				= new TomcatServletWebServerFactory();
		tomcatServletWebServerFactory.addAdditionalTomcatConnectors(createStandardConnector());

		return tomcatServletWebServerFactory;
	}

	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11NioProtocol");
		connector.setPort(80);
		return connector;
	}

	 */
}
