import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

;


public class JsonParserRenamedAttrTest {

    @Test
    void shouldDeleteNestedElements(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input5.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations", Arrays.asList("furnishings", "addresses"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(5);
        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(6);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(0).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(1).size()).isEqualTo(6);
        assertThat(newres.get(1).get("id")).isEqualTo("1318504");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(1).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(1).get("customers.name")).isEqualTo("");

        assertThat(newres.get(2).size()).isEqualTo(6);
        assertThat(newres.get(2).get("id")).isEqualTo("1318504");
        assertThat(newres.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(2).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(3).size()).isEqualTo(6);
        assertThat(newres.get(3).get("id")).isEqualTo("1318504");
        assertThat(newres.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(6);
        assertThat(newres.get(4).get("id")).isEqualTo("1318501");
        assertThat(newres.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(4).get("rooms.id")).isEqualTo("");
        assertThat(newres.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(newres.get(4).get("customers.id")).isEqualTo("");
        assertThat(newres.get(4).get("customers.name")).isEqualTo("");

    }

    @Test
    void shouldDeleteNestedElement(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input4.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations", Arrays.asList("furnishings"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(5);
        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(6);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(0).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(1).size()).isEqualTo(6);
        assertThat(newres.get(1).get("id")).isEqualTo("1318504");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(1).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(1).get("customers.name")).isEqualTo("");

        assertThat(newres.get(2).size()).isEqualTo(6);
        assertThat(newres.get(2).get("id")).isEqualTo("1318504");
        assertThat(newres.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(2).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(3).size()).isEqualTo(6);
        assertThat(newres.get(3).get("id")).isEqualTo("1318504");
        assertThat(newres.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(6);
        assertThat(newres.get(4).get("id")).isEqualTo("1318501");
        assertThat(newres.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(4).get("rooms.id")).isEqualTo("");
        assertThat(newres.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(newres.get(4).get("customers.id")).isEqualTo("");
        assertThat(newres.get(4).get("customers.name")).isEqualTo("");

    }

    @Test
    void checkEmptyJson(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input0.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(0);
    }

    @Test
    void checkSimpleJson(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input1.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(2);
        assertThat(newres.get(0).size()).isEqualTo(2);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");

        assertThat(newres.get(1).size()).isEqualTo(2);
        assertThat(newres.get(1).get("id")).isEqualTo("1318501");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2021-05-07 07:47:05");

    }

    @Test
    void checkJsonWithOneNestedLevel(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input2.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(4);
        assertThat(newres.get(0).size()).isEqualTo(4);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2"); //TODO

        assertThat(newres.get(1).size()).isEqualTo(4);
        assertThat(newres.get(1).get("id")).isEqualTo("1318504");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(1).get("rooms.floor")).isEqualTo("3");

        assertThat(newres.get(2).size()).isEqualTo(4);
        assertThat(newres.get(2).get("id")).isEqualTo("1318501");
        assertThat(newres.get(2).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(2).get("rooms.id")).isEqualTo("5");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("25"); //TODO

        assertThat(newres.get(3).size()).isEqualTo(4);
        assertThat(newres.get(3).get("id")).isEqualTo("1318501");
        assertThat(newres.get(3).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(3).get("rooms.id")).isEqualTo("6");
        assertThat(newres.get(3).get("rooms.floor")).isEqualTo(""); //TODO

    }

    @Test
    void checkJsonWithPermutations(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input3.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(8);

        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(6);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(0).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(1).size()).isEqualTo(6);
        assertThat(newres.get(1).get("id")).isEqualTo("1318504");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(1).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(1).get("customers.name")).isEqualTo("");

        assertThat(newres.get(2).size()).isEqualTo(6);
        assertThat(newres.get(2).get("id")).isEqualTo("1318504");
        assertThat(newres.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(2).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(3).size()).isEqualTo(6);
        assertThat(newres.get(3).get("id")).isEqualTo("1318504");
        assertThat(newres.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(6);
        assertThat(newres.get(4).get("id")).isEqualTo("1318501");
        assertThat(newres.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(4).get("rooms.id")).isEqualTo("5");
        assertThat(newres.get(4).get("rooms.floor")).isEqualTo("25");
        assertThat(newres.get(4).get("customers.id")).isEqualTo("100");
        assertThat(newres.get(4).get("customers.name")).isEqualTo("");


        assertThat(newres.get(5).size()).isEqualTo(6);
        assertThat(newres.get(5).get("id")).isEqualTo("1318501");
        assertThat(newres.get(5).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(5).get("rooms.id")).isEqualTo("5");
        assertThat(newres.get(5).get("rooms.floor")).isEqualTo("25");
        assertThat(newres.get(5).get("customers.id")).isEqualTo("");
        assertThat(newres.get(5).get("customers.name")).isEqualTo("Janina");

        assertThat(newres.get(6).size()).isEqualTo(6);
        assertThat(newres.get(6).get("id")).isEqualTo("1318501");
        assertThat(newres.get(6).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(6).get("rooms.id")).isEqualTo("6");
        assertThat(newres.get(6).get("rooms.floor")).isEqualTo("");
        assertThat(newres.get(6).get("customers.id")).isEqualTo("100");
        assertThat(newres.get(6).get("customers.name")).isEqualTo("");

        assertThat(newres.get(7).size()).isEqualTo(6);
        assertThat(newres.get(7).get("id")).isEqualTo("1318501");
        assertThat(newres.get(7).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(7).get("rooms.id")).isEqualTo("6");
        assertThat(newres.get(7).get("rooms.floor")).isEqualTo("");
        assertThat(newres.get(7).get("customers.id")).isEqualTo("");
        assertThat(newres.get(7).get("customers.name")).isEqualTo("Janina"); //
    }

    @Test
    void checkJsonWithOneAndThreeNestedLevel(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("input4.json").getFile());
        String jsonContent = null;
        try {
            jsonContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = jsonParser.parse(jsonContent,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(5);
        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(9);
        assertThat(newres.get(0).get("id")).isEqualTo("1318504");
        assertThat(newres.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(0).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan");
        assertThat(newres.get(0).get("furnishings.beds")).isEqualTo("king");
        assertThat(newres.get(0).get("furnishings.pool")).isEqualTo("swimming");
        assertThat(newres.get(0).get("furnishings.alcohol")).isEqualTo("vodka");

        assertThat(newres.get(1).size()).isEqualTo(9);
        assertThat(newres.get(1).get("id")).isEqualTo("1318504");
        assertThat(newres.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(newres.get(1).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(1).get("customers.name")).isEqualTo("");
        assertThat(newres.get(1).get("furnishings.beds")).isEqualTo("king");
        assertThat(newres.get(1).get("furnishings.pool")).isEqualTo("swimming");
        assertThat(newres.get(1).get("furnishings.alcohol")).isEqualTo("vodka");

        assertThat(newres.get(2).size()).isEqualTo(9);
        assertThat(newres.get(2).get("id")).isEqualTo("1318504");
        assertThat(newres.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(2).get("customers.id")).isEqualTo("1");
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan");
        assertThat(newres.get(2).get("furnishings.beds")).isEqualTo("");
        assertThat(newres.get(2).get("furnishings.pool")).isEqualTo("");
        assertThat(newres.get(2).get("furnishings.alcohol")).isEqualTo("");

        assertThat(newres.get(3).size()).isEqualTo(9);
        assertThat(newres.get(3).get("id")).isEqualTo("1318504");
        assertThat(newres.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.id")).isEqualTo("3");
        assertThat(newres.get(3).get("customers.name")).isEqualTo("");
        assertThat(newres.get(3).get("furnishings.beds")).isEqualTo("");
        assertThat(newres.get(3).get("furnishings.pool")).isEqualTo("");
        assertThat(newres.get(3).get("furnishings.alcohol")).isEqualTo("");

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(9);
        assertThat(newres.get(4).get("id")).isEqualTo("1318501");
        assertThat(newres.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(4).get("rooms.id")).isEqualTo("");
        assertThat(newres.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(newres.get(4).get("customers.id")).isEqualTo("");
        assertThat(newres.get(4).get("customers.name")).isEqualTo("");
        assertThat(newres.get(4).get("furnishings.beds")).isEqualTo("");
        assertThat(newres.get(4).get("furnishings.pool")).isEqualTo("");
        assertThat(newres.get(4).get("furnishings.alcohol")).isEqualTo("");
    }
}
