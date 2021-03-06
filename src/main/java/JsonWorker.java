import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class JsonWorker {

    private final JsonNode root;
    private JsonNode startNode = null;
    private final Map<String, String> allSimpleAttributes = new LinkedHashMap<>();
    private final Map<String, JsonNode> allNestedElements = new LinkedHashMap<>();

    JsonWorker(JsonNode node) {
        this.root = Objects.requireNonNull(node);
    }

    JsonWorker(String content) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.root = mapper.readTree(content);
    }

    public Map<String, String> findAllSimpleAttributes() {
        findAllSimpleAttributes(root);
        return allSimpleAttributes;
    }

    public Map<String, JsonNode> findAllNestedElements() {
        findAllNestedElements(root);
        return allNestedElements;
    }

    public String findStartNode(final String startNodeName) {

        //JsonNode founded = findStartNode(root,startNodeName);

        JSONArray jsonElementsForPreparingToParsing = JsonPath.parse(root.toString()).read("." + startNodeName + "[*]");
        DocumentContext foundedJsonContext = JsonPath.parse(jsonElementsForPreparingToParsing);
        String foundedJsonForPreparingToParsing = foundedJsonContext.jsonString();
        foundedJsonForPreparingToParsing = "{" + startNodeName + ":" + foundedJsonForPreparingToParsing + "}";
        foundedJsonContext = JsonPath.parse(foundedJsonForPreparingToParsing);
        return foundedJsonContext.jsonString();
    }

    public JsonNode findStartJsonNode(final String startNodeName) {
        findStartNode(root,startNodeName);
        return startNode;
    }

    private void findStartNode(JsonNode node, String startParsingNode){
        AtomicReference<JsonNode> startNode = new AtomicReference<>();
        node.fields().forEachRemaining(entry -> {
            String name = entry.getKey();
            JsonNode value = entry.getValue();
            if(name.equals(startParsingNode)){
                this.startNode = value;
            } else {
                findStartNode(value, startParsingNode);
            }
        });
    }

    private void findAllSimpleAttributes(final JsonNode node) {
        ObjectNode object = (ObjectNode) node;
        object.fields()
                .forEachRemaining(
                        field -> {
                            if(field.getValue().isValueNode()){
                                allSimpleAttributes.put(field.getKey(), field.getValue().asText());
                            }
                        });
    }

    private void findAllNestedElements(final JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object.fields()
                    .forEachRemaining(
                            field -> {
                                if(!field.getValue().isValueNode()){
                                    allNestedElements.put(field.getKey(), field.getValue());
                                }
                            });
        }
    }
}