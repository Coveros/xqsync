package com.marklogic.ps;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

public class XccPropMapConverter implements Converter {


    public boolean canConvert(Class clazz) {
        return clazz.equals(XccProperties.class);
    }

    public void marshal(Object value,
                        HierarchicalStreamWriter writer,
                        MarshallingContext context) {

        XccProperties props = (XccProperties) value;

        for (String p : props.props.keySet()) {
            writer.startNode(p);
            writer.setValue(props.props.get(p));
            writer.endNode();
        }
    }


    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {
        Map<String, String> map = new HashMap<String, String>();

        String topNodeName = reader.getNodeName();

        if (!topNodeName.endsWith(":properties") && !topNodeName.contains("properties")) {

            return null;
        }


        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String nodeName = reader.getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }

            String value = reader.getValue().trim();

            reader.moveUp();
            map.put(nodeName, value);

            reader.moveUp();
        }

        XccProperties xccProp = new XccProperties(map);

        return xccProp;
    }


}
