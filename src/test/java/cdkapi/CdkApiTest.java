package cdkapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({
        "classpath:/web-app-context.xml",
        "classpath:/test-context.xml"})
public class CdkApiTest {
    @Autowired
    ApiClient apiClient;

    @Test
    public void returnsMwFromSmiles() {
        assertEquals(44.09573372097032, apiClient.getStructInfo("CCC").mw, 0);
    }

}
