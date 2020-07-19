
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;
import org.json.XML;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class JsonMain {

    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();/*
        File jsonInput = new File(classLoader.getResource("input3.json").getFile());
        String jsonContent = FileUtils.readFileToString(jsonInput);
        JsonParser jsonParser = new JsonParser();
        long start = System.currentTimeMillis();
        List<Map<String, String>> newres = jsonParser.parse(jsonContent,"reservations",Collections.emptyList());
        long end = System.currentTimeMillis() - start;
        System.out.println(end/1000L);*/

        File xmlInput = new File(classLoader.getResource("xml4.xml").getFile());
        String xmlContent = FileUtils.readFileToString(xmlInput);

/*        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new SimpleModule().addDeserializer(
                JsonNode.class,
                new DuplicateToArrayJsonNodeDeserializer()
        ));
        JsonNode node = new XmlMapper().readTree(xmlContent);

        JSONObject jObject = XML.toJSONObject(xmlContent);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Object json = mapper.readValue(jObject.toString(), Object.class);
        String output = mapper.writeValueAsString(json);
        System.out.println(output);

        String s = XML.toJSONObject(xmlContent).toString();
        List<Map<String, String>> newres = new JsonParser().parse(s,"reservations",Collections.emptyList());
        JSONObject xmlJSONObj = XML.toJSONObject(xmlContent);
        String jsonPrettyPrintString = xmlJSONObj.toString(4);

        String ss = U.xmlToJson(xmlContent);*/

        JsonNode node = new XmlMapper().readTree(xmlContent);
        String jObject = XML.toJSONObject(xmlContent).toString();
        List<Map<String, String>> newres = new JsonParser().parse(jObject,"reservations",Collections.emptyList());

        System.out.println("");
    }

    static void generatePermutations(List<List<String>> lists, List<String> result, int depth, String current) {
        if (depth == lists.size()) {
            result.add(current);
            return;
        }

        for (int i = 0; i < lists.get(depth).size(); i++) {
            generatePermutations(lists, result, depth + 1, current + lists.get(depth).get(i));
        }
    }

    static private <T> List<List<T>> permutations(List<List<T>> collections) {
        if (collections == null || collections.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<List<T>> res = new LinkedList<>();
            permutationsImpl(collections, res, 0, new LinkedList<T>());
            return res;
        }
    }

    //  /** Recursive implementation for {@link #permutations(List, Collection)} */
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
    }

    static private <T> T redu(List<T> list, BinaryOperator<T> f){
        return list.stream().reduce(f).orElse(null);
    }
}
