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

public class XmlParserTest {

    @Test
    void checkEmptyXml(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml0.xml").getFile());
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

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void checkSimpleXml(){
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
    void checkXmlWithOneNestedLevel(){
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
        assertThat(result.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(0).get("rooms.floor")).isEqualTo("2");

        assertThat(result.get(1).size()).isEqualTo(4);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(1).get("rooms.floor")).isEqualTo("3");

        assertThat(result.get(2).size()).isEqualTo(4);
        assertThat(result.get(2).get("id")).isEqualTo("1318501");
        assertThat(result.get(2).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(2).get("rooms.id")).isEqualTo("5");
        assertThat(result.get(2).get("rooms.floor")).isEqualTo("25");

        assertThat(result.get(3).size()).isEqualTo(4);
        assertThat(result.get(3).get("id")).isEqualTo("1318501");
        assertThat(result.get(3).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(3).get("rooms.id")).isEqualTo("6");
        assertThat(result.get(3).get("rooms.floor")).isEqualTo("");
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
        assertThat(result.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customers.id")).isEqualTo("1");
        assertThat(result.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(1).size()).isEqualTo(6);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customers.id")).isEqualTo("3");
        assertThat(result.get(1).get("customers.name")).isEqualTo("");

        assertThat(result.get(2).size()).isEqualTo(6);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customers.id")).isEqualTo("1");
        assertThat(result.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(3).size()).isEqualTo(6);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customers.id")).isEqualTo("3");
        assertThat(result.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(6);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("rooms.id")).isEqualTo("5");
        assertThat(result.get(4).get("rooms.floor")).isEqualTo("25");
        assertThat(result.get(4).get("customers.id")).isEqualTo("100");
        assertThat(result.get(4).get("customers.name")).isEqualTo("");


        assertThat(result.get(5).size()).isEqualTo(6);
        assertThat(result.get(5).get("id")).isEqualTo("1318501");
        assertThat(result.get(5).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(5).get("rooms.id")).isEqualTo("5");
        assertThat(result.get(5).get("rooms.floor")).isEqualTo("25");
        assertThat(result.get(5).get("customers.id")).isEqualTo("");
        assertThat(result.get(5).get("customers.name")).isEqualTo("Janina");

        assertThat(result.get(6).size()).isEqualTo(6);
        assertThat(result.get(6).get("id")).isEqualTo("1318501");
        assertThat(result.get(6).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(6).get("rooms.id")).isEqualTo("6");
        assertThat(result.get(6).get("rooms.floor")).isEqualTo("");
        assertThat(result.get(6).get("customers.id")).isEqualTo("100");
        assertThat(result.get(6).get("customers.name")).isEqualTo("");

        assertThat(result.get(7).size()).isEqualTo(6);
        assertThat(result.get(7).get("id")).isEqualTo("1318501");
        assertThat(result.get(7).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(7).get("rooms.id")).isEqualTo("6");
        assertThat(result.get(7).get("rooms.floor")).isEqualTo("");
        assertThat(result.get(7).get("customers.id")).isEqualTo("");
        assertThat(result.get(7).get("customers.name")).isEqualTo("Janina"); //
    }

    @Test
    void checkXmlWithOneAndThreeNestedLevel(){
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
        assertThat(result.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customers.id")).isEqualTo("1");
        assertThat(result.get(0).get("customers.name")).isEqualTo("Jan");
        assertThat(result.get(0).get("furnishings.beds")).isEqualTo("king");
        assertThat(result.get(0).get("furnishings.pool")).isEqualTo("swimming");
        assertThat(result.get(0).get("furnishings.alcohol")).isEqualTo("vodka");

        assertThat(result.get(1).size()).isEqualTo(9);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customers.id")).isEqualTo("3");
        assertThat(result.get(1).get("customers.name")).isEqualTo("");
        assertThat(result.get(1).get("furnishings.beds")).isEqualTo("king");
        assertThat(result.get(1).get("furnishings.pool")).isEqualTo("swimming");
        assertThat(result.get(1).get("furnishings.alcohol")).isEqualTo("vodka");

        assertThat(result.get(2).size()).isEqualTo(9);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customers.id")).isEqualTo("1");
        assertThat(result.get(2).get("customers.name")).isEqualTo("Jan");
        assertThat(result.get(2).get("furnishings.beds")).isEqualTo("");
        assertThat(result.get(2).get("furnishings.pool")).isEqualTo("");
        assertThat(result.get(2).get("furnishings.alcohol")).isEqualTo("");

        assertThat(result.get(3).size()).isEqualTo(9);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customers.id")).isEqualTo("3");
        assertThat(result.get(3).get("customers.name")).isEqualTo("");
        assertThat(result.get(3).get("furnishings.beds")).isEqualTo("");
        assertThat(result.get(3).get("furnishings.pool")).isEqualTo("");
        assertThat(result.get(3).get("furnishings.alcohol")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(9);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("rooms.id")).isEqualTo("");
        assertThat(result.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(result.get(4).get("customers.id")).isEqualTo("");
        assertThat(result.get(4).get("customers.name")).isEqualTo("");
        assertThat(result.get(4).get("furnishings.beds")).isEqualTo("");
        assertThat(result.get(4).get("furnishings.pool")).isEqualTo("");
        assertThat(result.get(4).get("furnishings.alcohol")).isEqualTo("");
    }

    @Test
    void shouldDeleteNestedElement(){
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
            result = xmlParser.parse(xmlContent,"reservations", Arrays.asList("furnishings"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(5);
        //first reservation
        assertThat(result.get(0).size()).isEqualTo(6);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customers.id")).isEqualTo("1");
        assertThat(result.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(1).size()).isEqualTo(6);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customers.id")).isEqualTo("3");
        assertThat(result.get(1).get("customers.name")).isEqualTo("");

        assertThat(result.get(2).size()).isEqualTo(6);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customers.id")).isEqualTo("1");
        assertThat(result.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(3).size()).isEqualTo(6);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customers.id")).isEqualTo("3");
        assertThat(result.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(6);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("rooms.id")).isEqualTo("");
        assertThat(result.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(result.get(4).get("customers.id")).isEqualTo("");
        assertThat(result.get(4).get("customers.name")).isEqualTo("");

    }

    @Test
    void shouldDeleteNestedElements(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml5.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser xmlParser = new XmlParser();
        List<Map<String, String>> result = new LinkedList<>();
        try {
            result = xmlParser.parse(xmlContent,"reservations", Arrays.asList("furnishings", "addresses"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(result.size()).isEqualTo(5);
        //first reservation
        assertThat(result.get(0).size()).isEqualTo(6);
        assertThat(result.get(0).get("id")).isEqualTo("1318504");
        assertThat(result.get(0).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(0).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(0).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(0).get("customers.id")).isEqualTo("1");
        assertThat(result.get(0).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(1).size()).isEqualTo(6);
        assertThat(result.get(1).get("id")).isEqualTo("1318504");
        assertThat(result.get(1).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(1).get("rooms.id")).isEqualTo("28902");
        assertThat(result.get(1).get("rooms.floor")).isEqualTo("2");
        assertThat(result.get(1).get("customers.id")).isEqualTo("3");
        assertThat(result.get(1).get("customers.name")).isEqualTo("");

        assertThat(result.get(2).size()).isEqualTo(6);
        assertThat(result.get(2).get("id")).isEqualTo("1318504");
        assertThat(result.get(2).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(2).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(2).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(2).get("customers.id")).isEqualTo("1");
        assertThat(result.get(2).get("customers.name")).isEqualTo("Jan");

        assertThat(result.get(3).size()).isEqualTo(6);
        assertThat(result.get(3).get("id")).isEqualTo("1318504");
        assertThat(result.get(3).get("add_date")).isEqualTo("2020-12-10 12:48:09");
        assertThat(result.get(3).get("rooms.id")).isEqualTo("28903");
        assertThat(result.get(3).get("rooms.floor")).isEqualTo("3");
        assertThat(result.get(3).get("customers.id")).isEqualTo("3");
        assertThat(result.get(3).get("customers.name")).isEqualTo("");

        //second reservation
        assertThat(result.get(4).size()).isEqualTo(6);
        assertThat(result.get(4).get("id")).isEqualTo("1318501");
        assertThat(result.get(4).get("add_date")).isEqualTo("2021-05-07 07:47:05");
        assertThat(result.get(4).get("rooms.id")).isEqualTo("");
        assertThat(result.get(4).get("rooms.floor")).isEqualTo("");
        assertThat(result.get(4).get("customers.id")).isEqualTo("");
        assertThat(result.get(4).get("customers.name")).isEqualTo("");

    }

    @Test
    void shouldFindStartParsingNode(){
        ClassLoader classLoader = new JsonMain().getClass().getClassLoader();

        File input = new File(classLoader.getResource("xml6.xml").getFile());
        String xmlContent = null;
        try {
            xmlContent = FileUtils.readFileToString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        XmlParser XmlParser = new XmlParser();
        List<Map<String, String>> newres = new LinkedList<>();
        try {
            newres = XmlParser.parse(xmlContent,"reservations", Collections.emptyList());
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
}
