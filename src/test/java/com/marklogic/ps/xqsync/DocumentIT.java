/**
 * Copyright (c) 2007-2010 Mark Logic Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The use of the Apache License does not indicate that this project is
 * affiliated with the Apache Software Foundation.
 */
package com.marklogic.ps.xqsync;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.marklogic.ps.Connection;
import com.marklogic.ps.Session;
import com.marklogic.ps.SimpleLogger;
import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCapability;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.ContentPermission;

/**
 * @author Michael Blakeley, MarkLogic Corporation
 * 
 */
public class DocumentIT {

    @Test
    public void testEscaping() throws Exception {
        String testString = "http://foo.com/bar baz/";
        String expected = testString;
        Configuration config = new Configuration();
        Properties properties = new Properties();
        properties.setProperty(Configuration.INPUT_PATH_KEY, "/dev/null");
        properties.setProperty(Configuration.OUTPUT_PATH_KEY, "/dev/null");
        config.setLogger(SimpleLogger.getSimpleLogger());
        config.setProperties(properties);
        config.configure();
        FilePathReader reader = new FilePathReader(config);
        FilePathWriter writer = new FilePathWriter(config);
        XQSyncDocument doc = new XQSyncDocument(new String[] { testString },
                reader, writer, config);
        testString = doc.getOutputUri(0);
        assertEquals(testString, expected);
        // testString = doc.getOutputUri(true);
        // assertEquals(testString, expected);
    }

    @Test
    @Ignore("requires a local MarkLogic server")
    public void testPermissions() throws Exception {
        Properties props = new Properties();
        props.setProperty(SimpleLogger.LOG_LEVEL, "INFO");
        props.setProperty(SimpleLogger.LOG_HANDLER, "CONSOLE");
        URI uri = new URI("xcc://q:q@localhost:9000/");
        props.setProperty(Configuration.INPUT_CONNECTION_STRING_KEY,
                uri.toString());
        props.setProperty(Configuration.OUTPUT_PATH_KEY, "/dev/null");
        Connection c = new Connection(uri);
        Session sess = (Session) c.newSession();
        String documentUri = this.getClass().getName() + "/testPermissions.xml";

        String documentString = "<!-- this is a test -->\n"
                + "<test id=\"foo\"/>\n";
        ContentCreateOptions createOptions = ContentCreateOptions
                .newXmlInstance();

        // write the test document
        Content content = ContentFactory.newContent(documentUri,
                documentString, createOptions);
        sess.insertContent(content);

        // set the permissions
        AdhocQuery req = sess.newAdhocQuery(Session.XQUERY_VERSION_1_0_ML
                + "declare variable $URI as xs:string external;\n"
                + "let $perms := xdmp:permission('admin', 'read')\n"
                + "return xdmp:document-set-permissions($URI, $perms)\n");
        req.setNewStringVariable("URI", documentUri);
        sess.submitRequest(req);

        // retrieve the test document
        Configuration config = new Configuration();
        config.setLogger(SimpleLogger.getSimpleLogger());
        config.setProperties(props);
        config.configure();
        SessionReader reader = new SessionReader(config);
        FilePathWriter writer = new FilePathWriter(config);
        XQSyncDocument doc = new XQSyncDocument(new String[] { documentUri },
                reader, writer, config);
        doc.read();
        String retrievedXml = new String(doc.getContent(0));

        // test the round-trip of the XML
        assertEquals(documentString, retrievedXml);

        // test the permissions
        ContentPermission[] permissions = doc.getMetadata(0).getPermissions();
        SimpleLogger logger = config.getLogger();
        logger.fine("found permissions " + permissions.length);
        for (int i = 0; i < permissions.length; i++) {
            logger.finer("permission[" + i + "] = " + permissions[i]);
        }
        assertEquals(1, permissions.length);
        assertEquals(permissions[0].getCapability(), ContentCapability.READ);
        assertEquals(permissions[0].getRole(), "admin");

        // on success, delete the test document
        // (otherwise we might inspect it)
        req = sess.newAdhocQuery(Session.XQUERY_VERSION_1_0_ML
                + "declare variable $URI as xs:string external;\n"
                + "xdmp:document-delete($URI)\n");
        req.setNewStringVariable("URI", documentUri);
        sess.submitRequest(req);

        sess.close();
    }
}
