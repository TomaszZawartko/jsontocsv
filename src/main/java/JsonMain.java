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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonMain {
    static Set<String> headers = new LinkedHashSet<>();
    static Set<HeaderContainer> headersContainer = new LinkedHashSet<>();
    static Set<String> nodeJsonPathToDelete = new LinkedHashSet<>();
    static List<Map<String, String>> result = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input6.json").getFile());
        String jsonContent = FileUtils.readFileToString(input);
        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());

        List<String> a = Arrays.asList("a", "b");
        List<String> b = Arrays.asList("c", "d");
        List<String> e = Arrays.asList("e", "f", "g");
        List<List<String>> c = Arrays.asList(a, b, e);
        List<List<String>> d = permutations(c);

        List<String> res = new LinkedList<>();
        d.stream().forEach(list -> {
            res.add(list.stream().reduce((s1,s2) -> s1+s2).orElse(""));
        });

        List<String> flat =
                d.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        System.out.println("OK");
        List<String> result = new LinkedList<>();
        generatePermutations(c, result, 0, "");
        System.out.println("OK");

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

