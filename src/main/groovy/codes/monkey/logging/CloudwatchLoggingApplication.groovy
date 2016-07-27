package codes.monkey.logging

import ch.qos.logback.access.tomcat.LogbackValve
import org.apache.catalina.Context
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

@SpringBootApplication
class CloudwatchLoggingApplication {

	@Bean
	EmbeddedServletContainerFactory servletContainer(TomcatContextCustomizer customizer) {
		def factory = new TomcatEmbeddedServletContainerFactory()
		factory.setTomcatContextCustomizers([customizer])
		factory
	}

	@Bean
	TomcatContextCustomizer contextCustomizer(){
		new TomcatContextCustomizer() {
			@Override
			void customize(Context context) {
				context.getPipeline().addValve(new LogbackValve())
			}
		}
	}

	static void main(String[] args) {
		SpringApplication.run CloudwatchLoggingApplication, args
	}
}
