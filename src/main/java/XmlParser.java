import org.json.XML;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class XmlParser {

    public List<Map<String, String>> parse(String xml, String startParsingNodeName, List<String> nodesToIgnore) throws IOException {
        String json = XML.toJSONObject(xml).toString();
        return new JsonParser().parse(json,startParsingNodeName, nodesToIgnore);
    }

}
