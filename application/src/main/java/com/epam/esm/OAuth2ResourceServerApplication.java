package com.epam.esm;

import com.epam.esm.exceptions.Translator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@EnableWebMvc
@SpringBootApplication(scanBasePackages = "com.epam.esm")
public class OAuth2ResourceServerApplication {

	@PersistenceContext
	public EntityManager em;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx =
				SpringApplication.run(OAuth2ResourceServerApplication.class, args);

		DispatcherServlet dispatcherServlet = (DispatcherServlet) ctx.getBean("dispatcherServlet");
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
	}

	/**
	 * Set the list of resource files here
	 *
	 * @return resourceBundleMessageSource
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		rs.setBasenames("messages");
		rs.setDefaultEncoding("UTF-8");
		rs.setUseCodeAsDefaultMessage(true);
		return rs;
	}

	@Bean
	public Translator translator() {
		return new Translator(messageSource());
	}

	@Bean
	public EntityManager entityManager() {
		return em;
	}
}
