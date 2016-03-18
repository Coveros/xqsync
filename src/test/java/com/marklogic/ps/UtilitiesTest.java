package com.marklogic.ps;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class UtilitiesTest {


    @Test
    public void weCanJoinStringList() throws Exception {

        List<String> items = Arrays.asList("One", "Two", "Three");
        String rv = Utilities.join(items, "|");

        assertThat(rv).isEqualTo("One|Two|Three");

    }

    @Test
    public void weCanJoinStringArray() throws Exception {
        
        String[] items = new String[] {"One", "Two", "Three"};
        String rv = Utilities.join(items, "|");

        assertThat(rv).isEqualTo("One|Two|Three");
    }

    @Test
    public void weCanEsacpeXMLSpecialCharacters() {

        String badXmlString = "<&>";
        String rv = Utilities.escapeXml(badXmlString);

        assertThat(rv).isEqualTo("&lt;&amp;&gt;");

    }

    @Test
    public void escapeXMLShouldReturnEmptyStringWhenGivenNull() {

        String rv = Utilities.escapeXml(null);

        assertThat(rv).isEqualTo("");

    }
}

