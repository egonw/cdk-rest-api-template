package cdkapi;


import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Transform;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smirks.Smirks;
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

    private static final SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    private static final SmilesGenerator generator = SmilesGenerator.isomeric();

    private static final Transform neutralAcid = Smirks.compile("[O-:1]>>[H][O+0:1]");
    private static final Transform neutralAmine = Smirks.compile("[N+H3:1]>>[N+0H2:1]");

    @GetMapping("/smirks/neutralize/{struct}")
    Map<String, Object> neutralize(@PathVariable(name = "struct") String struct) throws InvalidSmilesException {
        LOG.info("Neutralizing {}", struct);
        IAtomContainer cdkStruct = parser.parseSmiles(struct);

        Iterable<IAtomContainer> iterable = neutralAcid.apply(cdkStruct, Transform.Mode.Exclusive);
        for (IAtomContainer neutral : iterable) {
            cdkStruct = neutral;
            struct = generator.createSMILES(neutral);
            LOG.info("  --> {}", struct);
        }
        iterable = neutralAmine.apply(cdkStruct, Transform.Mode.Exclusive);
        for (IAtomContainer neutral : iterable) {
            struct = generator.createSMILES(neutral);
            LOG.info("  --> {}", struct);
        }
        return Map.of("smiles", struct);
    }
}
