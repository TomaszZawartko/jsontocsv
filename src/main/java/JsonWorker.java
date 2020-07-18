import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

class JsonWorker {

    private final JsonNode root;
    private final Map<String, String> allSimpleAttributes = new LinkedHashMap<>();
    private final Map<String, ArrayNode> allNestedElements = new LinkedHashMap<>();
    private final List<String> jsonPathsToDelete = new LinkedList<>();
    private final Set<String> headers = new LinkedHashSet<>();

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

    public Map<String, ArrayNode> findAllNestedElements() {
        findAllNestedElements(root);
        return allNestedElements;
    }

    public String prepareJsonBeforeParsing(List<String> nodesToIgnore, String startNodeName) {
        JSONArray jsonStringForPreparingToParsing = JsonPath.parse(root.toString()).read("." + startNodeName + "[*]");
        DocumentContext jsonContext = JsonPath.parse(jsonStringForPreparingToParsing);
        String jsonString = jsonContext.jsonString();
        jsonString = "{" + startNodeName + ":" + jsonString + "}";
        List<String> jsonPathsToRemove = this.createListOfJsonPathsToDelete(nodesToIgnore);
        jsonContext = JsonPath.parse(jsonString);

        for (String jsonPathToDelete : jsonPathsToRemove) {
            jsonContext.delete(jsonPathToDelete);
        }
        return jsonContext.jsonString();
    }

    public Set<String> createHeaders(){
        createHeaders(root,"");
        return headers;
    }

    private void findAllSimpleAttributes(JsonNode node) {
        ObjectNode object = (ObjectNode) node;
        object.fields()
                .forEachRemaining(
                        entry -> {
                            if (!entry.getValue().isArray()) {
                                allSimpleAttributes.put(entry.getKey(), entry.getValue().asText());
                            }
                        });
    }

    private void findAllNestedElements(JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object
                    .fields()
                    .forEachRemaining(
                            entry -> {
                                if (entry.getValue().isArray()) {
                                    allNestedElements.put(entry.getKey(), (ArrayNode) entry.getValue());
                                }
                            });
        }
    }



    private List<String> createListOfJsonPathsToDelete(List<String> nodesToIgnore){
        createJsonPathsToDelete(root, nodesToIgnore,"");
        List<String> editedNodeJsonPathToDelete = new LinkedList<>();
        jsonPathsToDelete.forEach(jsonPathToDelete -> {
                    String editedJsonPathToDelete = jsonPathToDelete.replaceFirst(Pattern.quote("[*]"), "");
                    editedNodeJsonPathToDelete.add(editedJsonPathToDelete);
                }
        );
        return editedNodeJsonPathToDelete;
    }

    private void createJsonPathsToDelete(JsonNode node, List<String> nodeNameToDelete, String path) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                JsonNode n = entry.getValue();
                if (n.isArray()) {
                    String jsonPath = path + "[*]." + entry.getKey();
                    if (nodeNameToDelete.contains(entry.getKey())) {
                        jsonPathsToDelete.add(jsonPath);
                    }
                    createJsonPathsToDelete(n, nodeNameToDelete, jsonPath);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createJsonPathsToDelete(item, nodeNameToDelete, path));
        }
    }

    private void createHeaders(JsonNode node, String parentName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                JsonNode n = entry.getValue();
                if (!n.isArray()) {
                    headers.add(parentName+ "."+entry.getKey());
                }
                createHeaders(n,entry.getKey());
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createHeaders(item, parentName));
        }
    }
}