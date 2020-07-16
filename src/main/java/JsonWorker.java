import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
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

    private JsonNode root;
    private final Map<String, ArrayNode> allNestedElements = new LinkedHashMap<>();
    private final Map<String, String> allSimpleAttributes = new LinkedHashMap<>();
    private final List<String> jsonPathsToDelete = new LinkedList<>();
    private final Set<HeaderContainer> headersContainer = new LinkedHashSet<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Set<String> simpleHeaders = new LinkedHashSet<>();

    JsonWorker(JsonNode node) {
        this.root = Objects.requireNonNull(node);
    }

    JsonWorker(String content) throws JsonProcessingException {
        this.root = mapper.readTree(content);
    }

    public Map<String, String> findAllSimpleAttributes(String parentName) {
        findAllSimpleAttributes(root, parentName);
        return allSimpleAttributes;
    }

    public Map<String, ArrayNode> findAllNestedElements() {
        findAllNestedElements(root);
        return allNestedElements;
    }

    public List<String> createListOfJsonPathsToDelete(List<String> nodesToIgnore){
        createJsonPathsToDelete(root, nodesToIgnore,"");
        List<String> editedNodeJsonPathToDelete = new LinkedList<>();
        jsonPathsToDelete.forEach(jsonPathToDelete -> {
                    String editedJsonPathToDelete = jsonPathToDelete.replaceFirst(Pattern.quote("[*]"), "");
                    editedNodeJsonPathToDelete.add(editedJsonPathToDelete);
                }
        );
        return editedNodeJsonPathToDelete;
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
        Set<HeaderContainer> headersContainer = this.createHeaders(/*startNodeName*/"",/*"." + startNodeName + "[*]"*/"");
        for (HeaderContainer header : headersContainer) {
            try {
                //jsonContext.renameKey(header.jsonPath, header.oldAttributeName, header.newAttributeName);
            } catch(PathNotFoundException ex){
                //jsonContext.add(header.jsonPath, header.newAttributeName);
            }
        }
        return jsonContext.jsonString();
    }

    public Set<HeaderContainer> createHeaders(String parentName, String path){
        createHeadersContainer(root,parentName,path);
        headersContainer.forEach(header -> header.jsonPath = header.jsonPath + "[*]");
        headersContainer.forEach(header -> header.jsonPath = header.jsonPath.replaceFirst(Pattern.quote("[*]"), ""));
        return headersContainer;
    }

    public Set<String> createSimpleHeaders(){
        createSimpleHeaders(root,"");
        return simpleHeaders;
    }

    private void findAllSimpleAttributes(JsonNode node, String nestedElementName) {
        ObjectNode object = (ObjectNode) node;
        object.fields()
                .forEachRemaining(
                        entry -> {
                            if (!entry.getValue().isArray()) {
                                String attributeName = entry.getKey();
                                if (attributeName.contains(".")) {
                                    allSimpleAttributes.put(attributeName, entry.getValue().asText());
                                } else {
                                    allSimpleAttributes.put(nestedElementName + "." + entry.getKey(), entry.getValue().asText());
                                }
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

    private void createHeadersContainer(JsonNode node, String parentName, String path) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                String oldName = entry.getKey();
                String newName = parentName + "." + entry.getKey();
                HeaderContainer headerContainer = new HeaderContainer(path, oldName, newName);
                JsonNode n = entry.getValue();
                if (!n.isArray()) {
                    headersContainer.add(headerContainer);
                } else {
                    String jsonPath = path + "[*]." + entry.getKey();
                    createHeadersContainer(n, newName, jsonPath);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createHeadersContainer(item, parentName, path));
        }
    }

    private void createSimpleHeaders(JsonNode node, String parentName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                String nodeName = parentName + "." + entry.getKey();
                JsonNode n = entry.getValue();
                if (!n.isArray()) {
                    simpleHeaders.add(nodeName);
                } else {
                    createSimpleHeaders(n, entry.getKey());
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createSimpleHeaders(item, parentName));
        }
    }
}