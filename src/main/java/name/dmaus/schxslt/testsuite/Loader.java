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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.w3c.dom.Document;

class Loader
{
    final ErrorHandler errors = new ErrorHandler () {
            public void fatalError (final SAXParseException e) { throw new RuntimeException(e); }

            public void error (final SAXParseException e) { throw new RuntimeException(e); }

            public void warning (final SAXParseException e) { throw new RuntimeException(e); }
        };

    final DocumentBuilderFactory testsuiteDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilderFactory testcaseDocumentBuilderFactory = DocumentBuilderFactory.newInstance();

    Loader ()
    {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            Schema testsuiteSchema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream("/testsuite.xsd")));
            testsuiteDocumentBuilderFactory.setXIncludeAware(true);
            testsuiteDocumentBuilderFactory.setNamespaceAware(true);
            testsuiteDocumentBuilderFactory.setSchema(testsuiteSchema);

            Schema testcaseSchema = schemaFactory.newSchema(new StreamSource(getClass().getResourceAsStream("/testcase.xsd")));
            testcaseDocumentBuilderFactory.setXIncludeAware(true);
            testcaseDocumentBuilderFactory.setNamespaceAware(true);
            testcaseDocumentBuilderFactory.setSchema(testcaseSchema);

        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    Testcase loadTestcase (final Path input)
    {
        Document document = loadDocument(testcaseDocumentBuilderFactory, input);
        TestcaseSpec spec = new TestcaseSpec(document);
        return new Testcase(spec);
    }

    Testsuite loadTestsuite (final Path input)
    {
        Document document = loadDocument(testsuiteDocumentBuilderFactory, input);
        TestsuiteSpec spec = new TestsuiteSpec(document);
        return new Testsuite(spec);
    }

    Document loadDocument (final DocumentBuilderFactory factory, final Path input)
    {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(errors);
            return builder.parse(Files.newInputStream(input), input.toString());
        } catch (ParserConfigurationException| SAXException | IOException e) {
            throw new RuntimeException(e);
        }

    }

}
