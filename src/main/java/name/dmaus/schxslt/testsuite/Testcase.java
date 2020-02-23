/*
 * Copyright (C) 2019,2020 by David Maus <dmaus@dmaus.name>
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

import java.nio.file.Path;
import java.nio.file.Files;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.ArrayList;

import java.util.Set;
import java.util.HashSet;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import javax.xml.XMLConstants;

class Testcase
{
    final XMLSerializer serializer = new XMLSerializer();
    final TestcaseSpec spec;

    Path schema;
    Path document;
    String queryBinding;

    Path report;

    Testcase (final TestcaseSpec spec)
    {
        this.spec = spec;
    }

    String getId ()
    {
        return spec.getId();
    }

    Path getSchema ()
    {
        return schema;
    }

    Path getDocument ()
    {
        return document;
    }

    Path getReport ()
    {
        return report;
    }

    String getPhase ()
    {
        return spec.getPhase();
    }

    String getLabel ()
    {
        return spec.getLabel();
    }

    String getReference ()
    {
        return spec.getReference();
    }

    boolean isExpectValid ()
    {
        return spec.isExpectValid();
    }

    boolean isExpectError ()
    {
        return spec.isExpectError();
    }

    boolean isOptional ()
    {
        return spec.isOptional();
    }

    Set<String> getFeatures ()
    {
        Set<String> features = new HashSet<String>();
        String[] feats = spec.getFeatures();
        for (int i = 0; i < feats.length; i++) {
            if (!feats[i].isEmpty()) {
                features.add(feats[i]);
            }
        }
        return features;
    }

    Expectation[] getExpectations ()
    {
        List<Expectation> expect = new ArrayList<Expectation>();

        XPathFactory compilerFactory = XPathFactory.newInstance();
        Element[] elements = spec.getExpectations();
        for (int i = 0; i < elements.length; i++) {
            Namespaces nsContext = createNamespaceContext(elements[i]);
            XPath compiler = compilerFactory.newXPath();
            compiler.setNamespaceContext(nsContext);
            try {
                expect.add(new Expectation(compiler.compile(elements[i].getAttribute("test"))));
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
        }

        return expect.toArray(new Expectation[expect.size()]);
    }

    public void populate (final String queryBindingStr)
    {
        try {

            Path tempDirectory = Files.createTempDirectory("testsuite-ng.").toAbsolutePath();

            schema = Files.createTempFile(tempDirectory, "schema", ".sch");
            report = Files.createTempFile(tempDirectory, "report", ".xml");

            serializer.serialize(spec.getSchema(queryBindingStr), schema);

            document = serialize(tempDirectory, spec.getPrimaryDocument());

            NodeList documents = spec.getSecondaryDocuments();
            for (int i = 0; i < documents.getLength(); i++) {
                Element documentWrap = (Element)documents.item(i);
                serialize(tempDirectory, documentWrap);
            }

            queryBinding = queryBindingStr;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Path serialize (final Path directory, final Element documentWrap) throws IOException
    {
        String filename = documentWrap.getAttribute("filename");
        Path filepath = directory.resolve(filename).toAbsolutePath();

        if (!filepath.startsWith(directory)) {
            throw new RuntimeException("Cannot populate file outside of target directory");
        }

        if (filepath.getParent() != null) {
            Files.createDirectories(filepath.getParent());
        }

        NodeList childNodes = documentWrap.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            if (childNodes.item(j).getNodeType() == Element.ELEMENT_NODE) {

                Element documentElement = (Element)childNodes.item(j);

                Path documentFile = Files.createFile(filepath);

                serializer.serialize(documentElement, documentFile);

                break;
            }
        }
        return filepath;
    }

    Namespaces createNamespaceContext (final Node startNode)
    {
        Namespaces nsContext = new Namespaces();
        Node node = startNode;
        do {
            collectNamespaceDecls(nsContext, node);
            node = node.getParentNode();
        } while (node != null);
        return nsContext;
    }

    void collectNamespaceDecls (final Namespaces nsContext, final Node node)
    {
        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            if (XMLConstants.XMLNS_ATTRIBUTE.equals(attrs.item(i).getNamespaceURI())) {
                String prefix = attrs.item(i).getLocalName();
                if (!nsContext.isDeclaredPrefix(prefix)) {
                    nsContext.addNamespaceBinding(prefix, attrs.item(i).getTextContent());
                }
            }
        }
    }
}
