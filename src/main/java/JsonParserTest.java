import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class JsonParserTest {

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
            newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(2);
        assertThat(newres.get(0).size()).isEqualTo(2);
        assertThat(newres.get(0).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(0).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");

        assertThat(newres.get(1).size()).isEqualTo(2);
        assertThat(newres.get(1).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(1).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");

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
            newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(4);
        assertThat(newres.get(0).size()).isEqualTo(4);
        assertThat(newres.get(0).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(0).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get(".reservations.rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("rooms.floor")).isEqualTo("2"); //TODO

        assertThat(newres.get(1).size()).isEqualTo(4);
        assertThat(newres.get(1).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(1).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get(".reservations.rooms.id")).isEqualTo("28903");
        assertThat(newres.get(1).get(".reservations.rooms.floor")).isEqualTo("3");

        assertThat(newres.get(2).size()).isEqualTo(4);
        assertThat(newres.get(2).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(2).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(2).get(".reservations.rooms.id")).isEqualTo("5");
        assertThat(newres.get(2).get("rooms.floor")).isEqualTo("25"); //TODO

        assertThat(newres.get(3).size()).isEqualTo(3);
        assertThat(newres.get(3).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(3).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(3).get(".reservations.rooms.id")).isEqualTo("6");
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
            newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(8);

        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(6);
        assertThat(newres.get(0).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(0).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get(".reservations.rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get("customers.floor")).isEqualTo("2"); //TODO
        assertThat(newres.get(0).get("customers.id")).isEqualTo("1"); //TODO
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan"); //TODO

        assertThat(newres.get(1).size()).isEqualTo(5);
        assertThat(newres.get(1).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(1).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get(".reservations.rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get("customers.floor")).isEqualTo("2"); //TODO
        assertThat(newres.get(1).get(".reservations.customers.id")).isEqualTo("3");

        assertThat(newres.get(2).size()).isEqualTo(6);
        assertThat(newres.get(2).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(2).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get(".reservations.rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get(".reservations.rooms.floor")).isEqualTo("3"); //TODO
        assertThat(newres.get(2).get("customers.id")).isEqualTo("1"); //TODO
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan"); //TODO

        assertThat(newres.get(3).size()).isEqualTo(5);
        assertThat(newres.get(3).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(3).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get(".reservations.rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get(".reservations.rooms.floor")).isEqualTo("3"); //TODO
        assertThat(newres.get(3).get(".reservations.customers.id")).isEqualTo("3"); //TODO

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(5);
        assertThat(newres.get(4).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(4).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(4).get(".reservations.rooms.id")).isEqualTo("5");
        assertThat(newres.get(4).get("customers.floor")).isEqualTo("25"); //TODO
        assertThat(newres.get(4).get("customers.id")).isEqualTo("100"); //TODO

        assertThat(newres.get(5).size()).isEqualTo(5);
        assertThat(newres.get(5).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(5).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(5).get(".reservations.rooms.id")).isEqualTo("5");
        assertThat(newres.get(5).get("customers.floor")).isEqualTo("25"); //TODO
        assertThat(newres.get(5).get("customers.name")).isEqualTo("Janina"); //TODO

        assertThat(newres.get(6).size()).isEqualTo(4);
        assertThat(newres.get(6).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(6).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(6).get(".reservations.rooms.id")).isEqualTo("6");
        assertThat(newres.get(6).get("customers.id")).isEqualTo("100"); //

        assertThat(newres.get(7).size()).isEqualTo(4);
        assertThat(newres.get(7).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(7).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(newres.get(7).get(".reservations.rooms.id")).isEqualTo("6");
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
            newres = jsonParser.parse(jsonContent,null,"reservations",/*Arrays.asList("childrens")*/Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(newres.size()).isEqualTo(5);
        //first reservation
        assertThat(newres.get(0).size()).isEqualTo(9);
        assertThat(newres.get(0).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(0).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(0).get(".reservations.rooms.id")).isEqualTo("28902");
        assertThat(newres.get(0).get(".reservations.rooms.floor")).isEqualTo("2");
        assertThat(newres.get(0).get(".reservations.customers.id")).isEqualTo("1");
        assertThat(newres.get(0).get("customers.name")).isEqualTo("Jan"); //TODO
        assertThat(newres.get(0).get(".reservations.rooms.furnishings.beds")).isEqualTo("king");
        assertThat(newres.get(0).get(".reservations.rooms.furnishings.pool")).isEqualTo("swimming");
        assertThat(newres.get(0).get(".reservations.rooms.furnishings.alcohol")).isEqualTo("vodka");

        assertThat(newres.get(1).size()).isEqualTo(8);
        assertThat(newres.get(1).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(1).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(1).get(".reservations.rooms.id")).isEqualTo("28902");
        assertThat(newres.get(1).get(".reservations.rooms.floor")).isEqualTo("2");
        assertThat(newres.get(1).get(".reservations.customers.id")).isEqualTo("3");
        assertThat(newres.get(1).get(".reservations.rooms.furnishings.beds")).isEqualTo("king");
        assertThat(newres.get(1).get(".reservations.rooms.furnishings.pool")).isEqualTo("swimming");
        assertThat(newres.get(1).get(".reservations.rooms.furnishings.alcohol")).isEqualTo("vodka");

        assertThat(newres.get(2).size()).isEqualTo(6);
        assertThat(newres.get(2).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(2).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(2).get(".reservations.rooms.id")).isEqualTo("28903");
        assertThat(newres.get(2).get(".reservations.rooms.floor")).isEqualTo("3");
        assertThat(newres.get(2).get(".reservations.customers.id")).isEqualTo("1");
        assertThat(newres.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(newres.get(3).size()).isEqualTo(5);
        assertThat(newres.get(3).get(".reservations.id")).isEqualTo("1318504");
        assertThat(newres.get(3).get(".reservations.add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(newres.get(3).get(".reservations.rooms.id")).isEqualTo("28903");
        assertThat(newres.get(3).get(".reservations.rooms.floor")).isEqualTo("3");
        assertThat(newres.get(3).get(".reservations.customers.id")).isEqualTo("3");

        //second reservation
        assertThat(newres.get(4).size()).isEqualTo(2);
        assertThat(newres.get(4).get(".reservations.id")).isEqualTo("1318501");
        assertThat(newres.get(4).get(".reservations.add_date")).isEqualTo("2021-05-07 07:47:05");
    }
}