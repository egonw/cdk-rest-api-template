package cdkapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Creates MockMVC object to test the endpoints.
 */
public class MockMvcFactoryBean implements FactoryBean<MockMvc> {
    @Autowired private WebApplicationContext applicationContext;
    @Autowired private ObjectMapper objectMapper;

    MockMvcFactoryBean() {
        RestAssuredMockMvc.config = RestAssuredMockMvc.config()
                .objectMapperConfig(new ObjectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Override
    public MockMvc getObject() {
        return MockMvcBuilders.webAppContextSetup(applicationContext).build();
    }

    @Override
    public Class<?> getObjectType() {
        return MockMvc.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}