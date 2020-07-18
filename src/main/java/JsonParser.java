import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonParser {

    private final List<Map<String, String>> result = new LinkedList<>();

    public List<Map<String, String>> parse(String json, String startParsingNodeName, List<String> nodesToIgnore) throws IOException {
        JsonWorker worker = new JsonWorker(json);
        String jsonPreparedToParsing = worker.prepareJsonBeforeParsing(nodesToIgnore, startParsingNodeName);

        JsonWorker workerAfterPrepared = new JsonWorker(jsonPreparedToParsing);
        Set<String> headers = workerAfterPrepared.createHeaders(startParsingNodeName);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonAfterPreparing = (ObjectNode)mapper.readTree(jsonPreparedToParsing);
        ArrayNode array = (ArrayNode)jsonAfterPreparing.get(startParsingNodeName);
        List<ObjectNode> firstLevel = new LinkedList<>();
        array.elements().forEachRemaining(element -> firstLevel.add((ObjectNode)element));
        processRoot(firstLevel);
        addBlankValuesToUnfilledAttributes(headers);
        return sortByHeaders(headers);
    }

    private void addBlankValuesToUnfilledAttributes(Set<String> headers){
        result.forEach(row -> headers.forEach(header -> row.putIfAbsent(header, "")));
    }

    private List<Map<String, String>> sortByHeaders(Set<String> headers){
        List<Map<String, String>> sortedResult = new LinkedList<>();
        result.forEach(row -> {
            Map<String, String> sortedRow = new LinkedHashMap<>();
            headers.forEach(header -> sortedRow.put(header, row.get(header)));
            sortedResult.add(sortedRow);
        });
        return sortedResult;
    }

    private void processRoot(List<ObjectNode> reservations) {
        for (ObjectNode reservation : reservations) {
            JsonWorker jsonWorker = new JsonWorker(reservation);
            Map<String, String> allSimpleAttributesFromReservation = jsonWorker.findAllSimpleAttributes();
            Map<String, ArrayNode> allNestedElementsFromReservation = jsonWorker.findAllNestedElements();
            Map<String, ArrayNode> allRenamedNestedElementsFromReservation = createUniqueAttributesNameForSimpleAttributesInNestedElements(allNestedElementsFromReservation);
            if(allRenamedNestedElementsFromReservation.isEmpty()){
                result.add(allSimpleAttributesFromReservation);
            }else {
                List<List<ObjectNode>> nestedElementsMergedWithSimpleAttributesGrouped =
                        mergeSimpleAttributesWithNestedElementsFromTheSameLevel(allSimpleAttributesFromReservation, allRenamedNestedElementsFromReservation);
                List<List<ObjectNode>> permutedNestedGroups = permutations(nestedElementsMergedWithSimpleAttributesGrouped);
                List<ObjectNode> mergedPermutedGroups = mergeNestedGroups(permutedNestedGroups);
                processRoot(mergedPermutedGroups);
            }
        }
    }

    private Map<String, ArrayNode> createUniqueAttributesNameForSimpleAttributesInNestedElements(Map<String, ArrayNode> nestedElements) {
        Map<String, ArrayNode> renamedAttributesFromNestedElement = new LinkedHashMap<>();
        for(Map.Entry<String, ArrayNode> nestedElement : nestedElements.entrySet()){
            String nestedElementName = nestedElement.getKey();
            ArrayNode arrayNodeAttributes = nestedElement.getValue();
            ArrayNode newAttributes = new ArrayNode(new JsonNodeFactory(false));
            arrayNodeAttributes.elements().forEachRemaining(attributes -> {
                ObjectNode node = (ObjectNode)attributes;
                ObjectNode newNode = new ObjectNode(new JsonNodeFactory(false));
                node.fields().forEachRemaining(attr -> {
                    String attrName = attr.getKey();
                    if(attr.getValue().isArray()){
                        newNode.set(attrName, attr.getValue());
                    }else {
                        String attrValue = attr.getValue().asText();
                        newNode.put(nestedElementName + "." + attrName, attrValue);
                    }
                });
                newAttributes.add(newNode);
            });
            renamedAttributesFromNestedElement.put(nestedElementName,newAttributes);
        }
        return renamedAttributesFromNestedElement;
    }

    private List<List<ObjectNode>> mergeSimpleAttributesWithNestedElementsFromTheSameLevel(Map<String, String> allSimpleAttributes, Map<String, ArrayNode> allNestedElements) {
        List<List<ObjectNode>> mergedSimpleAttributesWithNestedElements = new LinkedList<>();
        for (Map.Entry<String, ArrayNode> entry : allNestedElements.entrySet()) {
            List<ObjectNode> mergedSimpleAttributesWithNestedElement = new LinkedList<>();
            ArrayNode nestedElementValues = entry.getValue();
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

    private List<ObjectNode> mergeNestedGroups(List<List<ObjectNode>> nestedGroups){
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

    static private ObjectNode mergeTwoObjectNode(ObjectNode objectNode1, ObjectNode objectNode2){
        ObjectNode mergedObjectNode = objectNode1.deepCopy();
        mergedObjectNode.setAll(objectNode2);
        return mergedObjectNode;
    }
}
