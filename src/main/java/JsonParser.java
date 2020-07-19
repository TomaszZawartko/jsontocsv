import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonParser {

    private final List<Map<String, String>> result = new LinkedList<>();

    public List<Map<String, String>> parse(String json, String startParsingNodeName, List<String> nodesToIgnore) throws IOException {
        JsonWorker worker = new JsonWorker(json);
        String jsonPreparedToParsing = worker.findStartNode(startParsingNodeName);

        //ObjectMapper mapper = new ObjectMapper();
        //ObjectNode jsonAfterPreparing = (ObjectNode) mapper.readTree(jsonPreparedToParsing);
        JsonNode array = worker.findStartJsonNode(startParsingNodeName); //(ArrayNode) jsonAfterPreparing.get(startParsingNodeName);
        List<ObjectNode> firstLevel = new LinkedList<>();
        array.elements().forEachRemaining(element -> {
            if(element.isObject()){
                firstLevel.add((ObjectNode) element);
            } else if(element.isArray()){
                element.elements().forEachRemaining(arrayElement -> {
                    firstLevel.add((ObjectNode)arrayElement);
                });
            }

        });
        processRoot(firstLevel,nodesToIgnore);
        Set<String> headers = createHeadersFromResult();
        addBlankValuesToUnfilledAttributes(headers);
        return sortByHeaders(headers);
    }

    private Set<String> createHeadersFromResult(){
        Set<String> headers = new HashSet<>();
        for(Map<String,String> row : result){
            headers.addAll(row.keySet());
        }
        return headers;
    }

    private void addBlankValuesToUnfilledAttributes(Set<String> headers) {
        result.forEach(row -> headers.forEach(header -> row.putIfAbsent(header, "")));
    }

    private List<Map<String, String>> sortByHeaders(Set<String> headers) {
        List<Map<String, String>> sortedResult = new LinkedList<>();
        result.forEach(row -> {
            Map<String, String> sortedRow = new LinkedHashMap<>();
            headers.forEach(header -> sortedRow.put(header, row.get(header)));
            sortedResult.add(sortedRow);
        });
        return sortedResult;
    }

    private void processRoot(List<ObjectNode> root, List<String> nodesToIgnore) {
        for (ObjectNode rootElement : root) {
            JsonWorker jsonWorker = new JsonWorker(rootElement);
            Map<String, String> allSimpleAttributesFromRootElement = jsonWorker.findAllSimpleAttributes();
            Map<String, JsonNode> allNestedElementsFromRootElement = jsonWorker.findAllNestedElements();
            Map<String, JsonNode> allNestedElementsFromRootElementWithoutNodesToIgnore =
                    removeNodesToIgnore(allNestedElementsFromRootElement, nodesToIgnore);
            Map<String, JsonNode> allNestedElementsFromRootElementWithUniqueSimpleAttributesNames =
                    createUniqueAttributesNameForSimpleAttributesInNestedElements(allNestedElementsFromRootElementWithoutNodesToIgnore);
            if (allNestedElementsFromRootElementWithUniqueSimpleAttributesNames.isEmpty()) {
                result.add(allSimpleAttributesFromRootElement);
            } else {
                List<List<ObjectNode>> groupedNestedElementsMergedWithSimpleAttributes =
                        mergeSimpleAttributesWithNestedElementsFromTheSameLevel(
                                allSimpleAttributesFromRootElement,
                                allNestedElementsFromRootElementWithUniqueSimpleAttributesNames);
                List<List<ObjectNode>> permutedNestedGroups = permutations(groupedNestedElementsMergedWithSimpleAttributes);
                List<ObjectNode> mergedPermutedGroups = mergeNestedGroups(permutedNestedGroups);
                processRoot(mergedPermutedGroups, nodesToIgnore);
            }
        }
    }

    private Map<String, JsonNode> removeNodesToIgnore(Map<String, JsonNode> nestedElements, List<String> nodesToIgnore){
        Map<String, JsonNode> nestedElementsWithoutNodesToIgnore = new LinkedHashMap<>();
        for(Map.Entry<String, JsonNode> entry : nestedElements.entrySet()){
            if(!nodesToIgnore.contains(entry.getKey())){
                nestedElementsWithoutNodesToIgnore.put(entry.getKey(), entry.getValue());
            }
        }
        return nestedElementsWithoutNodesToIgnore;
    }

    private Map<String, JsonNode> createUniqueAttributesNameForSimpleAttributesInNestedElements(Map<String, JsonNode> nestedElements) {
        Map<String, JsonNode> renamedAttributesFromNestedElement = new LinkedHashMap<>();
        for (Map.Entry<String, JsonNode> nestedElement : nestedElements.entrySet()) {
            String nestedElementName = nestedElement.getKey();
            JsonNode arrayNodeAttributes = nestedElement.getValue();
            ArrayNode newAttributes = new ArrayNode(new JsonNodeFactory(false));
            arrayNodeAttributes.elements().forEachRemaining(attributes -> {
                if(attributes.isObject()) {
                    ObjectNode node = (ObjectNode) attributes;
                    ObjectNode newNode = new ObjectNode(new JsonNodeFactory(false));
                    node.fields().forEachRemaining(attr -> {
                        String attrName = attr.getKey();
                        if (attr.getValue().isValueNode()) {
                            String attrValue = attr.getValue().asText();
                            newNode.put(nestedElementName + "." + attrName, attrValue);
                        } else {
                            newNode.set(attr.getKey(), attr.getValue());
                        }
                    });
                    newAttributes.add(newNode);
                }else if (attributes.isArray()){
                    ArrayNode oldArray = (ArrayNode)attributes;
                    renameAttributesFromArrayNode(oldArray, newAttributes, nestedElementName);
                }
            });
            renamedAttributesFromNestedElement.put(nestedElementName, newAttributes);
        }
        return renamedAttributesFromNestedElement;
    }

    private void renameAttributesFromArrayNode(ArrayNode oldArray, ArrayNode newArray, String parentName){
        oldArray.elements().forEachRemaining(objectNode -> {
            ObjectNode on = (ObjectNode) objectNode;
            ObjectNode newNode = new ObjectNode(new JsonNodeFactory(false));
            on.fields().forEachRemaining(attr -> {
                JsonNode attribute = attr.getValue();
                if(attribute.isValueNode()){
                    newNode.put(parentName + "." + attr.getKey(),attribute.asText());
                }else{
                    newNode.set(attr.getKey(),attr.getValue());
                }
            });
            newArray.add(newNode);
        });
    }

    private List<List<ObjectNode>> mergeSimpleAttributesWithNestedElementsFromTheSameLevel(Map<String, String> allSimpleAttributes, Map<String, /*ArrayNode*/JsonNode> allNestedElements) {
        List<List<ObjectNode>> mergedSimpleAttributesWithNestedElements = new LinkedList<>();
        for (Map.Entry<String, JsonNode> entry : allNestedElements.entrySet()) {
            List<ObjectNode> mergedSimpleAttributesWithNestedElement = new LinkedList<>();
            JsonNode nestedElementValues = entry.getValue();
            nestedElementValues.elements().forEachRemaining(item -> {
                if (item.isObject()) {
                    ObjectNode nestedElement = (ObjectNode) item;
                    for (Map.Entry<String, String> reservationSimpleAttributes : allSimpleAttributes.entrySet()) {
                        nestedElement.put(reservationSimpleAttributes.getKey(), reservationSimpleAttributes.getValue());
                    }
                    mergedSimpleAttributesWithNestedElement.add(nestedElement);
                }
            });
            mergedSimpleAttributesWithNestedElements.add(mergedSimpleAttributesWithNestedElement);
        }
        return mergedSimpleAttributesWithNestedElements;
    }

    private List<ObjectNode> mergeNestedGroups(List<List<ObjectNode>> nestedGroups) {
        List<ObjectNode> mergedPermutatedGroups = new LinkedList<>();
        nestedGroups.forEach(elementsToBeMerged -> mergedPermutatedGroups
                .add(elementsToBeMerged
                        .stream()
                        .reduce(JsonParser::mergeTwoObjectNode)
                        .orElse(null)));
        return mergedPermutatedGroups;
    }

    private <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            permutationsImpl(collections, res, 0, new LinkedList<>());
            return res;
        }
    }

    private <T> void permutationsImpl(List<List<T>> ori, Collection<List<T>> res, int d, List<T> current) {
        // if depth equals number of original collections, final reached, add and return
        if (d == ori.size()) {
            res.add(current);
            return;
        }

        // iterate from current collection and copy 'current' element N times, one for each element
        Collection<T> currentCollection = ori.get(d);
        for (T element : currentCollection) {
            List<T> copy = new LinkedList<>(current);
            copy.add(element);
            permutationsImpl(ori, res, d + 1, copy);
        }
    }

    static private ObjectNode mergeTwoObjectNode(ObjectNode objectNode1, ObjectNode objectNode2) {
        ObjectNode mergedObjectNode = objectNode1.deepCopy();
        mergedObjectNode.setAll(objectNode2);
        return mergedObjectNode;
    }
}
