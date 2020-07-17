import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.stream.Collectors;

public class JsonParser {

    private final List<Map<String, String>> result = new LinkedList<>();

    public List<Map<String, String>> parse(String json, List<ObjectNode> reservations, String startParsingNodeName, List<String> nodesToIgnore) throws IOException {
        JsonWorker worker = new JsonWorker(json);
        String jsonNodePreparedForParsing = worker.prepareJsonBeforeParsing(nodesToIgnore, startParsingNodeName);
        Set<String> headers = worker.createNormalHeaders();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonAfterPreparing = (ObjectNode)mapper.readTree(jsonNodePreparedForParsing);
        ArrayNode array = (ArrayNode)jsonAfterPreparing.get(startParsingNodeName);
        List<ObjectNode> firstLevel = new LinkedList<>();
        array.elements().forEachRemaining(element -> {
            firstLevel.add((ObjectNode)element);
        });
        processRoot(firstLevel);
        addBlankValuesToUnfilledAttributes(headers);
        return sortByHeaders(headers);
    }

    private void addBlankValuesToUnfilledAttributes(Set<String> headers){
        result.forEach(row -> {
            headers.forEach(header -> {
                row.putIfAbsent(header, "");
            });
        });
    }

    private List<Map<String, String>> sortByHeaders(Set<String> headers){
        List<Map<String, String>> sortedResult = new LinkedList<>();
        result.forEach(row -> {
            Map<String, String> sortedRow = new LinkedHashMap<>();
            headers.forEach(header -> {
                sortedRow.put(header, row.get(header));
            });
            sortedResult.add(sortedRow);
        });
        return sortedResult;
    }

    private void processRoot(List<ObjectNode> reservations) {
        for (ObjectNode reservation : reservations) {

            JsonWorker jsonWorker = new JsonWorker(reservation);
            Map<String, String> allSimpleAttributesFromReservation = jsonWorker.findAllSimpleAttributes();
            Map<String, ArrayNode> allNestedElementsFromReservation = jsonWorker.findAllNestedElements();
            if(allNestedElementsFromReservation.isEmpty()){
                result.add(allSimpleAttributesFromReservation);
                continue;
            }
            List<ObjectNode> nestedElements = mergeSimpleAttributesWithNestedElementsFromTheSameLevel(allSimpleAttributesFromReservation,allNestedElementsFromReservation);

            Map<JsonNode, List<ObjectNode>> nestedElementsGroupedByGroupName =
                    nestedElements
                            .stream()
                            .collect(Collectors.groupingBy(x -> x.findValue("group")));//grupujemy obiekty wzgledem przynaleznosci do wezla
            List<List<ObjectNode>> listOfGroupedNestedNodes = new LinkedList<>();
            for(Map.Entry<JsonNode, List<ObjectNode>> oneGroup : nestedElementsGroupedByGroupName.entrySet()){
                List<ObjectNode> groupValues = oneGroup.getValue();
                for(ObjectNode node : groupValues){
                    node.remove("group");
                }
                listOfGroupedNestedNodes.add(groupValues);
            }

            List<List<ObjectNode>> permutatedNestedGroups = permutations(listOfGroupedNestedNodes);
            List<ObjectNode> mergedPermutatedGroups = mergeNestedGroups(permutatedNestedGroups);
            processRoot(mergedPermutatedGroups);
        }
    }

    private List<ObjectNode> mergeSimpleAttributesWithNestedElementsFromTheSameLevel(Map<String, String> allSimpleAttributes, Map<String, ArrayNode> allNestedElements) {
        List<ObjectNode> mergedSimpleAttributesWithNestedElements = new LinkedList<>();
        for (Map.Entry<String, ArrayNode> entry : allNestedElements.entrySet()) {
            String nestedElementName = entry.getKey();
            ArrayNode nestedElementValues = entry.getValue();
            nestedElementValues.elements().forEachRemaining(item -> {
                if (item.isObject()) {
                    ObjectNode nestedElement = (ObjectNode) item;
                    for (Map.Entry<String, String> reservationSimpleAttributes : allSimpleAttributes.entrySet()) {
                        nestedElement.put(reservationSimpleAttributes.getKey(), reservationSimpleAttributes.getValue());
                    }
                    nestedElement.put("group", nestedElementName);
                    mergedSimpleAttributesWithNestedElements.add(nestedElement);
                }
            });
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
            permutationsImpl(collections, res, 0, new LinkedList<T>());
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


/*    private void generateAllPermutationsBetweenObjectNodes(List<List<ObjectNode>> listsOfObjectNodes, List<ObjectNode> result, int depth, ObjectNode current) {
        if (depth == listsOfObjectNodes.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < listsOfObjectNodes.get(depth).size(); i++) {
            generateAllPermutationsBetweenObjectNodes(listsOfObjectNodes, result, depth + 1, mergeTwoObjectNode(current, listsOfObjectNodes.get(depth).get(i)));
        }
    }*/

    static private ObjectNode mergeTwoObjectNode(ObjectNode objectNode1, ObjectNode objectNode2){
        ObjectNode mergedObjectNode = objectNode1.deepCopy();
        mergedObjectNode.setAll(objectNode2);
        return mergedObjectNode;
    }

/*    static private <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            permutationsImpl(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

    static private <T> void permutationsImpl(List<List<T>> ori, Collection<List<T>> res, int d, List<T> current) {
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
    }*/
}
