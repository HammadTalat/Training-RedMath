package com.example.newsmanagementsystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyBindingTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PropertyTestConfiguration.class)
            .withPropertyValues(
                    "app.news.title=Simple Test News",
                    "app.news.page-size=5",
                    "app.sample-message=Hello from the test property");

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert") // Assertions execute inside the context callback.
    void loadsConfigurationPropertiesAndValueAnnotation() {
        contextRunner.run(context -> {
            NewsProperties configurationProperties = context.getBean(NewsProperties.class);
            PropertyExamplesController controller = context.getBean(PropertyExamplesController.class);

            assertThat(configurationProperties.title()).isEqualTo("Simple Test News");
            assertThat(configurationProperties.pageSize()).isEqualTo(5);

            Map<String, Object> response = controller.showPropertyExamples().getBody();
            assertThat(response).isNotNull();
            assertThat(response.get("properties"))
                    .isEqualTo(new NewsProperties("Simple Test News", 5));
            assertThat(response.get("message")).isEqualTo("Hello from the test property");
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(NewsProperties.class)
    @Import(PropertyExamplesController.class)
    static class PropertyTestConfiguration {
    }
}
