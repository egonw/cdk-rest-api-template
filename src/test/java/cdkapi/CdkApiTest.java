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
    public void returnSmiles() {
        assertEquals("CCC", apiClient.getStructInfo("CCC").smiles);
    }

    @Test
    public void neutralizedCarboxylate() {
        assertEquals("CC(=O)O", apiClient.getStructInfo("CC(=O)[O-]").smiles);
    }

    @Test
    public void noChangeCarboxylicAcid() {
        assertEquals("CC(=O)O", apiClient.getStructInfo("CC(=O)O").smiles);
    }

    @Test
    public void neutralizedAminePlus() {
        assertEquals("CCN", apiClient.getStructInfo("CC[N+H3]").smiles);
    }

    @Test
    public void noChangeAmine() {
        assertEquals("CCN", apiClient.getStructInfo("CCN").smiles);
    }


}
