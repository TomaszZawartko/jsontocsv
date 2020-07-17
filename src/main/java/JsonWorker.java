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

    private JsonNode root;
    private final Map<String, ArrayNode> allNestedElements = new LinkedHashMap<>();
    private final Map<String, String> allSimpleAttributes = new LinkedHashMap<>();
    private final List<String> jsonPathsToDelete = new LinkedList<>();
    private final Set<HeaderContainer> headersContainer = new LinkedHashSet<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Set<String> normalHeaders = new LinkedHashSet<>();

    JsonWorker(JsonNode node) {
        this.root = Objects.requireNonNull(node);
    }

    JsonWorker(String content) throws JsonProcessingException {
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

    public String prepareJsonBeforeParsing(List<String> nodesToIgnore, String startNodeName) throws JsonProcessingException {

        //znajdujemy wezel do parsowania
        JSONArray jsonStringForPreparingToParsing = JsonPath.parse(root.toString()).read("." + startNodeName + "[*]");
        DocumentContext jsonContext = JsonPath.parse(jsonStringForPreparingToParsing);
        String jsonString = jsonContext.jsonString();
        jsonString = "{" + startNodeName + ":" + jsonString + "}";
        List<String> jsonPathsToRemove = this.createListOfJsonPathsToDelete(nodesToIgnore);
        jsonContext = JsonPath.parse(jsonString);

        //usuwamy wezly
        for (String jsonPathToDelete : jsonPathsToRemove) {
            jsonContext.delete(jsonPathToDelete);
        }
        this.root = mapper.readTree(jsonContext.jsonString());
        //zmieniamy nazwy atrybutow na root.group1. ... .groupN.attributName
        //Set<HeaderContainer> headersContainer = this.createHeaders(/*startNodeName*/"",/*"." + startNodeName + "[*]"*/"");
        Set<HeaderContainer> headersContainer = this.createHeaderContainerForEachSimpleAttribute();
        for (HeaderContainer header : headersContainer) {
            jsonContext.renameKey(header.jsonPath, header.oldAttributeName, header.newAttributeName);
        }
        return jsonContext.jsonString();
    }

    public Set<String> createNormalHeaders(){
        createNormalHeaders(root,"");
        Set<String> editedHeaders = new LinkedHashSet<>();
        normalHeaders.forEach(header -> editedHeaders.add(header.substring(1)));
        return editedHeaders;
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

    private Set<HeaderContainer> createHeaderContainerForEachSimpleAttribute(){
        createHeaderContainerForEachSimpleAttribute(root,"","","");
        headersContainer.forEach(headerContainer -> headerContainer.newAttributeName = headerContainer.newAttributeName.substring(1));
        return headersContainer;
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

    private void createHeaderContainerForEachSimpleAttribute(JsonNode node, String jsonPath, String newName, String oldName) {
        if (node.isObject()) {
            ObjectNode object = (ObjectNode) node;
            object
                    .fields()
                    .forEachRemaining(
                            entry -> {
                                String val="";
                                if(entry.getValue().isArray()){
                                    val = "."+entry.getKey();
                                }
                                createHeaderContainerForEachSimpleAttribute(entry.getValue(), jsonPath + val, newName + "." + entry.getKey(), entry.getKey());
                            });
        } else if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            AtomicInteger counter = new AtomicInteger();
            array
                    .elements()
                    .forEachRemaining(
                            item -> {
                                createHeaderContainerForEachSimpleAttribute(item, jsonPath + "[" + counter.getAndIncrement()+"]", newName, oldName);
                            });
        } else {
            headersContainer.add(new HeaderContainer(jsonPath,oldName,newName));
        }
    }

    private void createNormalHeaders(JsonNode node, String parentName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                String nodeName = parentName + "." + entry.getKey();
                JsonNode n = entry.getValue();
                if (!n.isArray()) {
                    normalHeaders.add(nodeName);
                } else {
                    createNormalHeaders(n, nodeName);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createNormalHeaders(item, parentName));
        }
    }
}