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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

class JsonWorker {

    private final JsonNode root;
    private final Map<String, String> allSimpleAttributes = new LinkedHashMap<>();
    private final Map<String, /*ArrayNode*/JsonNode> allNestedElements = new LinkedHashMap<>();
    private final List<String> jsonPathsToDelete = new LinkedList<>();
    private final Set<String> headers = new LinkedHashSet<>();

    JsonWorker(JsonNode node) {
        this.root = Objects.requireNonNull(node);
    }

    JsonWorker(String content) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.root = mapper.readTree(content);
    }

    public Set<String> createHeaders(final String startParsingNodeName) {
        createHeaders(root, "", startParsingNodeName);
        return headers;
    }

    public Set<String> process(final String startParsingNodeName) {
        process(root, startParsingNodeName, startParsingNodeName);
        return headers;
    }

    public Map<String, String> findAllSimpleAttributes() {
        findAllSimpleAttributes(root);
        return allSimpleAttributes;
    }

    public Map<String, /*ArrayNode*/JsonNode> findAllNestedElements() {
        findAllNestedElements(root);
        return allNestedElements;
    }

    public String prepareJsonBeforeParsing(final List<String> nodesToIgnore, final String startNodeName) {
        JSONArray jsonElementsForPreparingToParsing = JsonPath.parse(root.toString()).read("." + startNodeName + "[*]");
        DocumentContext foundedJsonContext = JsonPath.parse(jsonElementsForPreparingToParsing);
        String foundedJsonForPreparingToParsing = foundedJsonContext.jsonString();
        foundedJsonForPreparingToParsing = "{" + startNodeName + ":" + foundedJsonForPreparingToParsing + "}";
        List<String> jsonPathsToRemove = this.createListOfJsonPathsToDelete(nodesToIgnore);
        foundedJsonContext = JsonPath.parse(foundedJsonForPreparingToParsing);

        for (String jsonPathToDelete : jsonPathsToRemove) {
            //foundedJsonContext.delete(jsonPathToDelete);
        }
        return foundedJsonContext.jsonString();
    }

    private void findAllSimpleAttributes(final JsonNode node) {
        ObjectNode object = (ObjectNode) node;
        object.fields()
                .forEachRemaining(
                        field -> {
                            /*if(field.getValue().isObject()){
                                findAllSimpleAttributes(field.getValue());
                            }
                            else if (!field.getValue().isArray()) {
                                allSimpleAttributes.put(field.getKey(), field.getValue().asText());
                            }*/
                            if(field.getValue().isValueNode()){
                                allSimpleAttributes.put(field.getKey(), field.getValue().asText());
                            }else {
                                //findAllSimpleAttributes(field.getValue());
                            }
                        });
    }

    private void findAllNestedElements(final JsonNode node) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object.fields()
                    .forEachRemaining(
                            field -> {
                                /*if(field.getValue().isObject()){
                                    findAllNestedElements(field.getValue());
                                }
                                else if (field.getValue().isArray()) {
                                    allNestedElements.put(field.getKey(), (ArrayNode) field.getValue());
                                }*/
                                if(!field.getValue().isValueNode()){
                                    allNestedElements.put(field.getKey(), /*(ArrayNode)*/ field.getValue());
                                }
                            });
        }
    }


    private List<String> createListOfJsonPathsToDelete(final List<String> nodesToIgnore) {
        createJsonPathsToDelete(root, nodesToIgnore, "");
        List<String> editedNodeJsonPathToDelete = new LinkedList<>();
        jsonPathsToDelete.forEach(jsonPathToDelete -> {
                    String editedJsonPathToDelete = jsonPathToDelete.replaceFirst(Pattern.quote("[*]"), "");
                    editedNodeJsonPathToDelete.add(editedJsonPathToDelete);
                }
        );
        return editedNodeJsonPathToDelete;
    }

    private void createJsonPathsToDelete(final JsonNode node, final List<String> nodeNameToDelete, final String path) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(field -> {
                JsonNode fieldValue = field.getValue();
                if (fieldValue.isArray()) {
                    String jsonPath = path + "[*]." + field.getKey();
                    if (nodeNameToDelete.contains(field.getKey())) {
                        jsonPathsToDelete.add(jsonPath);
                    }
                    createJsonPathsToDelete(fieldValue, nodeNameToDelete, jsonPath);
                }else if(fieldValue.isObject()){
                    createJsonPathsToDelete(fieldValue, nodeNameToDelete,path);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createJsonPathsToDelete(item, nodeNameToDelete, path));
        }
    }

    private void createHeaders(final JsonNode node, final String parentName, final String startParsingNodeName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(field -> {
                JsonNode fieldValue = field.getValue();
                if(fieldValue.isObject()){
                    createHeaders(fieldValue,parentName,startParsingNodeName);
                }
                else if (!fieldValue.isArray()) {
                    if (!parentName.equals(startParsingNodeName)) {
                        headers.add(parentName + "." + field.getKey());
                    } else {
                        headers.add(field.getKey());
                    }
                }
                createHeaders(fieldValue, field.getKey(), startParsingNodeName);
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createHeaders(item, parentName, startParsingNodeName));
        }
    }

    private void process(JsonNode node, String parentName, String startParsingNodeName) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object
                    .fields()
                    .forEachRemaining(
                            entry -> {
                                if(entry.getValue().isValueNode()){
                                    String header = parentName.equals(startParsingNodeName) ? entry.getKey() : parentName + "." + entry.getKey();
                                    headers.add(/*parentName + "." + entry.getKey()*/header);
                                }else{
                                    process(entry.getValue(), entry.getKey(), startParsingNodeName);
                                }
                            });
        } else if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            array
                    .elements()
                    .forEachRemaining(
                            item -> {
                                process(item, parentName, startParsingNodeName);
                            });
        }
    }
}