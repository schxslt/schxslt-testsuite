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
 * Testsuite factory.
 */
public final class TestsuiteFactory
{
    private static final String NAMESPACE_URI = "https://doi.org/10.5281/zenodo.5679629#";

    private final TestcaseFactory testcaseFactory = new TestcaseFactory();
    private final DocumentBuilderFactory documentBuilderFactory;
    private final Validator validator;

    public TestsuiteFactory (final Validator validator)
    {
        this.validator = validator;
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(getClass().getResource("/testsuite.xsd"));

            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setSchema(schema);
        } catch (SAXException e) {
            throw new RuntimeException("Unable to load XML Schema for testcase specifications", e);
        }
    }
    
    public Testsuite newInstance (final Path file)
    {
        try {
            DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();
            Document document = parser.parse(Files.newInputStream(file));

            String queryBinding = document.getDocumentElement().getAttribute("queryBinding");
            String title = getElementsByTagNameNS(document, NAMESPACE_URI, "title").get(0).getTextContent();

            List<Testcase> testcases = new ArrayList<Testcase>();
            for (Element element : getElementsByTagNameNS(document, NAMESPACE_URI, "testcase")) {
                Path filename = file.resolveSibling(element.getAttribute("href"));
                testcases.add(testcaseFactory.newInstance(filename));
            }

            return new Testsuite(title, testcases, validator, queryBinding);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to create instance of XML parser", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read testsuite specification", e);
        } catch (SAXException e) {
            throw new RuntimeException("Invalid testsuite specification", e);
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
