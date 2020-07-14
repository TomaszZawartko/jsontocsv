import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonMain {
    static Set<String> headers = new LinkedHashSet<>();
    static Set<HeaderContainer> headersContainer = new LinkedHashSet<>();
    static Set<String> nodeJsonPathToDelete = new LinkedHashSet<>();
    static List<Map<String, String>> result = new LinkedList<>();

    public static void main(String[] args) throws IOException {

        //String jsonContent = FileUtils.readFileToString(new File("C:\\Users\\Tomek\\Desktop\\test\\json\\dwaEleZagn\\dwaEleZagn.json"));
        String jsonContent = FileUtils.readFileToString(new File("C:\\Users\\Tomek\\Desktop\\test\\json\\oneLvl\\oneLvl.json"));

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.EMPTY_LIST);


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = (ObjectNode) mapper.readTree(new File("C:\\Users\\Tomek\\Desktop\\test\\json\\dwaEleZagn\\dwaEleZagn.json"));
        //String pretty = node.toString();
        //createHeaders(node,"");
        String nodeString = node.toString();
        createHeadersContainer(node, "", "");
        String content = FileUtils.readFileToString(new File("C:\\Users\\Tomek\\Desktop\\test\\json\\dwaEleZagn\\dwaEleZagn.json"));
        DocumentContext jsonContext = JsonPath.parse(content);
        JSONArray dc = JsonPath.parse(content).read(".reservations[*]");
        String str = dc.toJSONString();
        JsonNode n = mapper.readTree(str);
        List<String> nodesToIgnore = new LinkedList<>();
        nodesToIgnore.add("customers");
        nodesToIgnore.add("rooms");
        //for (String nodeToIgnore : nodesToIgnore) {
            createJsonPathsToDelete(node, nodesToIgnore, "");
        //}

        Set<String> editedNodeJsonPathToDelete = new LinkedHashSet<>();
        nodeJsonPathToDelete.forEach(jsonPathToDelete -> {
                    String editedJsonPathToDelte = jsonPathToDelete.replaceFirst(Pattern.quote("[*]"), "");
                    editedNodeJsonPathToDelete.add(editedJsonPathToDelte);
                }
        );
        for (String jsonPathToDelete : editedNodeJsonPathToDelete) {
            jsonContext.delete(jsonPathToDelete);
        }

        headersContainer.forEach(header -> header.jsonPath = header.jsonPath + "[*]");
        headersContainer.forEach(header -> header.jsonPath = header.jsonPath.replaceFirst(Pattern.quote("[*]"), ""));

        for (HeaderContainer header : headersContainer) {
            jsonContext.renameKey(header.jsonPath, header.oldAttributeName, header.newAttributeName);
        }

        String s = jsonContext.jsonString();
        JsonNode nodeFromJsonPath = mapper.readTree(s);
        List<ObjectNode> reservations = new LinkedList<>();
        nodeFromJsonPath.fields().forEachRemaining(entry -> {
            ArrayNode arrayNode = (ArrayNode) entry.getValue();
            arrayNode.elements().forEachRemaining(item -> {
                reservations.add((ObjectNode) item);
            });
        });
        JsonWorker worker = new JsonWorker(nodeFromJsonPath);
        JsonParser parser = new JsonParser();
       // List<Map<String, String>> afterParsing = parser.parse(reservations,"reservations", new ArrayList<>());

        //jsonContext.delete("$.reservations[*].customers");

        //jsonContext.renameKey(".reservations[*]", "id", "reservations.id");
        //jsonContext.renameKey(".reservations[*].rooms[*]", "id", "reservations.rooms.id");
        //jsonContext.renameKey(".reservations[*].customers[*]", "cust_id", "reservations.rooms.cust_id");

        List<String> paths = Arrays.asList("$.reservations[*].customers[*].childrens", "$.reservations[*].rooms");
        for (String p : paths) {
            jsonContext.delete(p);
        }

        Map<String, JSONArray> json = jsonContext.json();
        //jsonContext.renameKey("$.reservations[*]","id","reservations.id");

        System.out.println(jsonContext.jsonString());


        List<ObjectNode> allElementsFromFirstLevel = new LinkedList<>();
        node.fields().forEachRemaining(entry -> {
            ArrayNode arrayNode = (ArrayNode) entry.getValue();
            arrayNode.elements().forEachRemaining(item -> {
                allElementsFromFirstLevel.add((ObjectNode) item);
            });
        });

      //  JsonParser parser = new JsonParser();
        //List<Map<String, String>> result = parser.parse(allElementsFromFirstLevel, "reservations", new ArrayList<>());

        //processRoot(allElementsFromFirstLevel, "reservations");
        System.out.println("OK");

        List<String> a = Arrays.asList("a", "b");
        List<String> b = Arrays.asList("c", "d");
        List<String> e = Arrays.asList("e", "f", "g");
        List<List<String>> c = Arrays.asList(a, b, e);
        List<List<String>> d = permutations(c);
        List<String> flat =
                d.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        System.out.println("OK");

    }

    private static <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            permutationsImpl(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

    private static <T> void permutationsImpl(List<List<T>> ori, List<List<T>> res, int d, List<T> current) {
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

    static void createJsonPathsToDelete(JsonNode node, List<String> nodeNameToDelete, String path) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                JsonNode n = entry.getValue();
                if (n.isArray()) {
                    String jsonPath = path + "[*]." + entry.getKey();
                    if (/*entry.getKey().equals(nodeNameToDelete)*/nodeNameToDelete.contains(entry.getKey())) {
                        nodeJsonPathToDelete.add(jsonPath);
                    }
                    createJsonPathsToDelete(n, nodeNameToDelete, jsonPath);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createJsonPathsToDelete(item, nodeNameToDelete, path));
        }
    }

    static void createHeadersContainer(JsonNode node, String parentName, String path) {
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

    static void createHeaders(JsonNode node, String parentName) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fields().forEachRemaining(entry -> {
                String nodeName = parentName + "." + entry.getKey();
                JsonNode n = entry.getValue();
                if (!n.isArray()) {
                    headers.add(nodeName);
                } else {
                    createHeaders(n, nodeName);
                }
            });
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            arrayNode.elements().forEachRemaining(item -> createHeaders(item, parentName));
        }
    }


/*    static void createHeaders(Map<String, JSONArray> node, String parentName) {
        for(Map.Entry<String, JSONArray> nestedNode : node.entrySet()){
            String name = nestedNode.getKey();
            JSONArray array = nestedNode.getValue();
            headers.add(parentName + "." +name);
            array.forEach(x -> {
                createHeaders((Map<String, JSONArray>)x,parentName + "." +name);
            });
        }
    }*/
}
