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
        assertEquals("CCN", apiClient.getStructInfo("CC[NH3+]").smiles);
    }

    @Test
    public void noChangeAmine() {
        assertEquals("CCN", apiClient.getStructInfo("CCN").smiles);
    }

    @Test
    public void testCarboxylicAcids() {
        assertEquals("OC(=O)C(=O)O", apiClient.getStructInfo("[O-]C(=O)C(=O)[O-]").smiles);
    }

    @Test
    public void testAminoAcid() {
        assertEquals("NC(C)C(=O)O", apiClient.getStructInfo("[NH3+]C(C)C(=O)[O-]").smiles);
    }

}
