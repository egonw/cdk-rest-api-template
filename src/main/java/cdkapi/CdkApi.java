package cdkapi;


import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.openscience.cdk.tools.manipulator.AtomContainerManipulator.getMass;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/api")
class CdkApi {
    private static final Logger LOG = LoggerFactory.getLogger(CdkApi.class);

    @GetMapping("/mw/{struct}")
    Map<String, Object> mw(@PathVariable(name = "struct") String struct) throws InvalidSmilesException {
        LOG.info("Calculating {} props", struct);
        IAtomContainer cdkStruct = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(struct);
        return Map.of("mw", getMass(cdkStruct, AtomContainerManipulator.MolWeight));
    }
}
