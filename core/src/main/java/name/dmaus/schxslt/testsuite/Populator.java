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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Populates a testcase to filesystem.
 */
public final class Populator
{
    private static final String QUERYBINDING_NAME = "queryBinding";

    private final XPathFactory xpathFactory = XPathFactory.newInstance();
    private final Serializer serializer = new Serializer();
    private final Path targetDirectory;

    public Populator (final Path targetDirectory)
    {
        this.targetDirectory = targetDirectory;
    }

    PopulatedTestcase populate (final Testcase testcase, final String queryBinding)
    {
        try {
            Path directory = Files.createTempDirectory(targetDirectory, null);

            Element schemaElement = selectSchema(testcase.getSchemas(), queryBinding);
            Path schema = Files.createTempFile(directory, null, null);
            serialize(schema, schemaElement, false);

            Path document = null;
            for (Element element : testcase.getDocuments()) {
                Path filename = directory.resolve(element.getAttribute("filename"));
                serialize(filename, element, true);
                if (document == null) {
                    document = filename;
                }
            }

            List<Assertion> assertions = compileAssertions(testcase.getAssertions());

            return new PopulatedTestcase(testcase.getTitle(), testcase.getExpectedValidationResultStatus(), schema, document, assertions);
        } catch (IOException | XPathExpressionException e) {
            throw new RuntimeException("Unable to populate testcase to filesystem", e);
        }
    }

    private List<Assertion> compileAssertions (final List<Element> elements) throws XPathExpressionException
    {
        List<Assertion> assertions = new ArrayList<Assertion>();
        for (Element element : elements) {
            XPath compiler = xpathFactory.newXPath();
            compiler.setNamespaceContext(new Namespaces(element));
            XPathExpression expression = compiler.compile(element.getAttribute("assert"));
            assertions.add(new Assertion(expression));
        }
        return assertions;
    }

    private void serialize (final Path filename, final Element element, final boolean isWrapper) throws IOException
    {
        Path parent = filename.getParent();
        if (parent != null) {
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        }

        DocumentFragment document = element.getOwnerDocument().createDocumentFragment();
        if (isWrapper) {
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() != Node.TEXT_NODE) {
                    document.appendChild(node.cloneNode(true));
                }
            }
        } else {
            document.appendChild(element.cloneNode(true));
        }
        serializer.serialize(document, filename);
    }

    private Element selectSchema (final List<Element> schemas, final String queryBinding)
    {
        Element defaultSchema = null;
        for (Element schema : schemas) {
            String schemaQueryBinding = schema.getAttribute(QUERYBINDING_NAME);
            if ("".equals(schemaQueryBinding)) {
                defaultSchema = schema;
            }
            if (schemaQueryBinding.equals(queryBinding)) {
                return schema;
            }
        }
        defaultSchema = (Element)defaultSchema.cloneNode(true);
        defaultSchema.setAttribute(QUERYBINDING_NAME, queryBinding);
        return defaultSchema;
    }
}
