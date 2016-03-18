package com.marklogic.ps.xccproperties;

import com.marklogic.ps.XccPropMapConverter;
import com.marklogic.ps.XccProperties;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class XccPropertiesMarshaller {

    private XStream marshaller;

    public  XccPropertiesMarshaller () {
        QNameMap qmap = new QNameMap();
        qmap.setDefaultNamespace("http://marklogic.com/xdmp/property");
        qmap.setDefaultPrefix("prop");
        StaxDriver staxDriver = new StaxDriver(qmap);

        marshaller = new XStream(staxDriver);

        marshaller.alias("properties", XccProperties.class);
        marshaller.registerConverter(new XccPropMapConverter());

    }

    public  XccProperties unmarshall(String props) {
        return (XccProperties) marshaller.fromXML(props);
    }

    public  String marshall(XccProperties propVal) {
        return marshaller.toXML(propVal);
    }
}
