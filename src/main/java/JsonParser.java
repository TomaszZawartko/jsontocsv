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
        processRoot(firstLevel, startParsingNodeName);
        addBlankValuesToUnfilledAttributes(headers);
        return sortByHeaders(headers);
        //return result;
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
/*    private JsonNode prepareJsonBeforeParsing(String node, List<String> nodesToIgnore, String startNodeName) throws JsonProcessingException {
        String jsonStringForPreparingToParsing = JsonPath.parse(node).read("." + startNodeName + "[*]");
        //usuwamy zbedne wezly
        JsonWorker jsonWorker = new JsonWorker(jsonStringForPreparingToParsing);
        List<String> jsonPathsToRemove = jsonWorker.createListOfJsonPathsToDelete(nodesToIgnore);
        DocumentContext jsonContext = JsonPath.parse(node);
        for (String jsonPathToDelete : jsonPathsToRemove) {
            jsonContext.delete(jsonPathToDelete);
        }

        //zmieniamy nazwy atrybutow na root.group1. ... .groupN.attributName
        Set<HeaderContainer> headersContainer = jsonWorker.createHeaders();
        for (HeaderContainer header : headersContainer) {
            jsonContext.renameKey(header.jsonPath, header.oldAttributeName, header.newAttributeName);
        }

        //zwracamy wyczyszczonego jsona przygotowanego do procesowania
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonContext.toString());
    }*/


    private void processRoot(List<ObjectNode> reservations, String name) {
        //Znajdujemy atrybuty proste rezerwacji
        //List<Map<String, String>> reservationsSimpleAttributes = new LinkedList<>(); //Czemu to jest lista a nie po prostu mapa?????
        for (ObjectNode reservation : reservations) {
            JsonWorker flattener = new JsonWorker(reservation);
            Map<String, String> simpleAttributesForReservation = flattener.findAllSimpleAttributes(name);
            //reservationsSimpleAttributes.add(simpleAttributesForReservation);
            //Znajdujemy elementy zagniezdzone dla danej rezerwacji
            Map<String, ArrayNode> allNestedElementsFromReservation = flattener.findAllNestedElements();

            //Kazdy element zagniezdzony scalamy z atrybutami prostymi rezerwacji
            List<ObjectNode> nestedElements = new LinkedList<>();
            String nestedElementName = "";
            if(allNestedElementsFromReservation.isEmpty()){
                result.add(simpleAttributesForReservation);
                //break;
            }
            for (Map.Entry<String, ArrayNode> entry : allNestedElementsFromReservation.entrySet()) {
                nestedElementName = entry.getKey();
                ArrayNode nestedElementValues = entry.getValue();
                String finalNestedElementName = nestedElementName;
                String finalNestedElementName1 = nestedElementName;
                nestedElementValues.elements().forEachRemaining(item -> {
                    if (item.isObject()) {
                        ObjectNode nestedElement = (ObjectNode) item;
                        for (Map.Entry<String, String> reservationSimpleAttributes : simpleAttributesForReservation.entrySet()) {
                            nestedElement.put(/*name + "." + finalNestedElementName + "." + */reservationSimpleAttributes.getKey(), reservationSimpleAttributes.getValue());
                        }
                        nestedElement.put("group", finalNestedElementName1);
                        nestedElements.add(nestedElement);
                    }
                });
                System.out.println("OK");
            }

            //permutujemy kazdy element zagniezdzony ("kazdy z kazdym")
            List<ObjectNode> permutatedNestedElements = new LinkedList<>();
            if(nestedElements.isEmpty()){
                continue;
            }
            Map<JsonNode, List<ObjectNode>> nestedElementsGroupedByGroupName = nestedElements.stream().collect(Collectors.groupingBy(x -> x.findValue("group")));//grupujemy obiekty wzgledem przynaleznosci do wezla
            List<List<ObjectNode>> listOfGroupedNestedNodes = new LinkedList<>();
            for(Map.Entry<JsonNode, List<ObjectNode>> oneGroup : nestedElementsGroupedByGroupName.entrySet()){
                String groupName = oneGroup.getKey().asText();
                List<ObjectNode> groupValues = oneGroup.getValue();
                for(ObjectNode node : groupValues){
                    node.remove("group");
                }
                listOfGroupedNestedNodes.add(groupValues);
            }
            List<ObjectNode> permutatedGroups = new LinkedList<>();
            generateAllPermutationsBetweenObjectNodes(listOfGroupedNestedNodes, permutatedGroups, 0, new ObjectNode(new JsonNodeFactory(false)));
            //permutatedGroups = permutations(listOfGroupedNestedNodes);
            //koniec permutacji
            processRoot(/*nestedElements*/permutatedGroups,nestedElementName);
        }
    }

    private void generateAllPermutationsBetweenObjectNodes(List<List<ObjectNode>> listsOfObjectNodes, List<ObjectNode> result, int depth, ObjectNode current) {
        if (depth == listsOfObjectNodes.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < listsOfObjectNodes.get(depth).size(); i++) {
            generateAllPermutationsBetweenObjectNodes(listsOfObjectNodes, result, depth + 1, mergeTwoObjectNode(current, listsOfObjectNodes.get(depth).get(i)));
        }
    }

    private ObjectNode mergeTwoObjectNode(ObjectNode objectNode1, ObjectNode objectNode2){
        ObjectNode mergedObjectNode = objectNode1.deepCopy();
        mergedObjectNode.setAll(objectNode2);
        return mergedObjectNode;
    }

    private <T> Collection<List<T>> permutations(List<Collection<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            Collection<List<T>> res = new LinkedList<>();
            permutationsImpl(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

  //  /** Recursive implementation for {@link #permutations(List, Collection)} */
    private <T> void permutationsImpl(List<Collection<T>> ori, Collection<List<T>> res, int d, List<T> current) {
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
}
