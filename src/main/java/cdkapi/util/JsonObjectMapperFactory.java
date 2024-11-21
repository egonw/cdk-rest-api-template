package cdkapi.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

public class JsonObjectMapperFactory {
    public static final ObjectMapper OBJECT_MAPPER = create();

    public static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(ANY)
                .withGetterVisibility(NONE)
                .withSetterVisibility(NONE)
                .withCreatorVisibility(NONE)
                .withIsGetterVisibility(NONE));
        mapper.registerModule(new SimpleModule().addDeserializer(String.class, new TrimStringDeserializer(String.class)));
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    public static ObjectNode json() {
        return JsonObjectMapperFactory.OBJECT_MAPPER.createObjectNode();
    }

    private static class TrimStringDeserializer extends StdScalarDeserializer<String> {
        public TrimStringDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext cxt) throws IOException {
            String s = p.getValueAsString();
            return s == null ? null : s.trim();
        }
    }
}
