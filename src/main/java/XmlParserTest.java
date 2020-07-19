import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlParserTest {

    @Test
    void checkSimpleJson(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml1.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser xmlParser = new XmlParser();
        List<Map<String, String>> result = new LinkedList<>();
        try {
            result = xmlParser.parse(xmlContent,"reservations", Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).size()).isEqualTo(2);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");

        assertThat(result.get(1).size()).isEqualTo(2);
        assertThat(result.get(1).get("id")).isEqualTo("1318501");
        assertThat(result.get(1).get("add_date")).isEqualTo("2021-05-07 07:47:05");

    }

    @Test
    void checkJsonWithOneNestedLevel(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml2.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser xmlParser = new XmlParser();
        List<Map<String, String>> result = new LinkedList<>();
        try {
            result = xmlParser.parse(xmlContent,"reservations",Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).size()).isEqualTo(4);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(0).get("room.id")).isEqualTo("28902");
        assertThat(result.get(0).get("room.floor")).isEqualTo("2");

        assertThat(result.get(1).size()).isEqualTo(4);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("room.id")).isEqualTo("28903");
        assertThat(result.get(1).get("room.floor")).isEqualTo("3");

        assertThat(result.get(2).size()).isEqualTo(4);
        assertThat(result.get(2).get("id")).isEqualTo("1318501");
        assertThat(result.get(2).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(2).get("room.id")).isEqualTo("5");
        assertThat(result.get(2).get("room.floor")).isEqualTo("25");

        assertThat(result.get(3).size()).isEqualTo(4);
        assertThat(result.get(3).get("id")).isEqualTo("1318501");
        assertThat(result.get(3).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(3).get("room.id")).isEqualTo("6");
        assertThat(result.get(3).get("room.floor")).isEqualTo("");
    }

    @Test
    void checkJsonWithPermutations(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml3.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser xmlParser = new XmlParser();
        List<Map<String, String>> result = new LinkedList<>();
        try {
            result = xmlParser.parse(xmlContent,"reservations", Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(8);

        //first reservation
        assertThat(result.get(0).size()).isEqualTo(6);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(0).get("room.id")).isEqualTo("28902");
        assertThat(result.get(0).get("room.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customer.id")).isEqualTo("1");
        assertThat(result.get(0).get("customer.name")).isEqualTo("Jan");

        assertThat(result.get(1).size()).isEqualTo(6);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("room.id")).isEqualTo("28902");
        assertThat(result.get(1).get("room.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customer.id")).isEqualTo("3");
        assertThat(result.get(1).get("customer.name")).isEqualTo("");

        assertThat(result.get(2).size()).isEqualTo(6);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("room.id")).isEqualTo("28903");
        assertThat(result.get(2).get("room.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customer.id")).isEqualTo("1");
        assertThat(result.get(2).get("customer.name")).isEqualTo("Jan");

        assertThat(result.get(3).size()).isEqualTo(6);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("room.id")).isEqualTo("28903");
        assertThat(result.get(3).get("room.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customer.id")).isEqualTo("3");
        assertThat(result.get(3).get("customer.name")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(6);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("room.id")).isEqualTo("5");
        assertThat(result.get(4).get("room.floor")).isEqualTo("25");
        assertThat(result.get(4).get("customer.id")).isEqualTo("100");
        assertThat(result.get(4).get("customer.name")).isEqualTo("");


        assertThat(result.get(5).size()).isEqualTo(6);
        assertThat(result.get(5).get("id")).isEqualTo("1318501");
        assertThat(result.get(5).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(5).get("room.id")).isEqualTo("5");
        assertThat(result.get(5).get("room.floor")).isEqualTo("25");
        assertThat(result.get(5).get("customer.id")).isEqualTo("");
        assertThat(result.get(5).get("customer.name")).isEqualTo("Janina");

        assertThat(result.get(6).size()).isEqualTo(6);
        assertThat(result.get(6).get("id")).isEqualTo("1318501");
        assertThat(result.get(6).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(6).get("room.id")).isEqualTo("6");
        assertThat(result.get(6).get("room.floor")).isEqualTo("");
        assertThat(result.get(6).get("customer.id")).isEqualTo("100");
        assertThat(result.get(6).get("customer.name")).isEqualTo("");

        assertThat(result.get(7).size()).isEqualTo(6);
        assertThat(result.get(7).get("id")).isEqualTo("1318501");
        assertThat(result.get(7).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(7).get("room.id")).isEqualTo("6");
        assertThat(result.get(7).get("room.floor")).isEqualTo("");
        assertThat(result.get(7).get("customer.id")).isEqualTo("");
        assertThat(result.get(7).get("customer.name")).isEqualTo("Janina"); //
    }

    @Test
    void checkJsonWithOneAndThreeNestedLevel(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml4.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser xmlParser = new XmlParser();
        List<Map<String, String>> result = new LinkedList<>();
        try {
            result = xmlParser.parse(xmlContent,"reservations",Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(5);
        //first reservation
        assertThat(result.get(0).size()).isEqualTo(9);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(0).get("room.id")).isEqualTo("28902");
        assertThat(result.get(0).get("room.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customer.id")).isEqualTo("1");
        assertThat(result.get(0).get("customer.name")).isEqualTo("Jan");
        assertThat(result.get(0).get("furnishing.beds")).isEqualTo("king");
        assertThat(result.get(0).get("furnishing.pool")).isEqualTo("swimming");
        assertThat(result.get(0).get("furnishing.alcohol")).isEqualTo("vodka");

        assertThat(result.get(1).size()).isEqualTo(9);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("room.id")).isEqualTo("28902");
        assertThat(result.get(1).get("room.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customer.id")).isEqualTo("3");
        assertThat(result.get(1).get("customer.name")).isEqualTo("");
        assertThat(result.get(1).get("furnishing.beds")).isEqualTo("king");
        assertThat(result.get(1).get("furnishing.pool")).isEqualTo("swimming");
        assertThat(result.get(1).get("furnishing.alcohol")).isEqualTo("vodka");

        assertThat(result.get(2).size()).isEqualTo(9);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("room.id")).isEqualTo("28903");
        assertThat(result.get(2).get("room.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customer.id")).isEqualTo("1");
        assertThat(result.get(2).get("customer.name")).isEqualTo("Jan");
        assertThat(result.get(2).get("furnishing.beds")).isEqualTo("");
        assertThat(result.get(2).get("furnishing.pool")).isEqualTo("");
        assertThat(result.get(2).get("furnishing.alcohol")).isEqualTo("");

        assertThat(result.get(3).size()).isEqualTo(9);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("room.id")).isEqualTo("28903");
        assertThat(result.get(3).get("room.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customer.id")).isEqualTo("3");
        assertThat(result.get(3).get("customer.name")).isEqualTo("");
        assertThat(result.get(3).get("furnishing.beds")).isEqualTo("");
        assertThat(result.get(3).get("furnishing.pool")).isEqualTo("");
        assertThat(result.get(3).get("furnishing.alcohol")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(9);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("room.id")).isEqualTo("");
        assertThat(result.get(4).get("room.floor")).isEqualTo("");
        assertThat(result.get(4).get("customer.id")).isEqualTo("");
        assertThat(result.get(4).get("customer.name")).isEqualTo("");
        assertThat(result.get(4).get("furnishing.beds")).isEqualTo("");
        assertThat(result.get(4).get("furnishing.pool")).isEqualTo("");
        assertThat(result.get(4).get("furnishing.alcohol")).isEqualTo("");
    }
}
