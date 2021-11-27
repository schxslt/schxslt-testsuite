/*
   * Copyright (C) 2021 by David Maus <dmaus@dmaus.name>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package name.dmaus.schxslt.testsuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Testcase factory.
 */
public final class TestcaseFactory
{
    private static final String NAMESPACE_URI = "https://doi.org/10.5281/zenodo.5679629#";

    private final DocumentBuilderFactory documentBuilderFactory;

    public TestcaseFactory ()
    {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getClass().getResource("/testcase.xsd"));

            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setSchema(schema);
        } catch (SAXException e) {
            throw new RuntimeException("Unable to load XML Schema for testcase specifications", e);
        }
    }

    public Testcase newInstance (final Path file)
    {
        try {
            DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();
            Document document = parser.parse(Files.newInputStream(file));

            List<Element> schemas = getElementsByTagNameNS(document, "http://purl.oclc.org/dsdl/schematron", "schema");
            List<Element> documents = getElementsByTagNameNS(document, NAMESPACE_URI, "document");
            List<Element> assertions = getElementsByTagNameNS(document, NAMESPACE_URI, "assertion");
            String title = getElementsByTagNameNS(document, NAMESPACE_URI, "title").get(0).getTextContent();
            String expect = document.getDocumentElement().getAttribute("expect").toUpperCase(Locale.ROOT);

            return new Testcase(title, ValidationResult.Status.valueOf(expect), schemas, documents, assertions);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to create instance of XML parser", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read testcase specification", e);
        } catch (SAXException e) {
            throw new RuntimeException("Invalid testcase specification", e);
        }
    }

    private List<Element> getElementsByTagNameNS (final Document document, final String namespaceUri, final String name)
    {
        List<Element> elements = new ArrayList<Element>();
        NodeList nodes = document.getElementsByTagNameNS(namespaceUri, name);
        for (int i = 0; i < nodes.getLength(); i++) {
            elements.add((Element)nodes.item(i));
        }
        return elements;
    }

}
