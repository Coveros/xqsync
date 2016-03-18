package com.marklogic.ps;

import java.util.HashMap;
import java.util.Map;

public class XccProperties {

   public Map<String, String> props = new HashMap<String, String>();

    public XccProperties(Map<String, String> map) {
        this.props = map;
    }

    public void removeSystemProps() {

        this.props.remove("last-modified");


    }
}
