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

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.ArrayList;

class TestcaseSpec
{

    static final String NS = "tag:dmaus@dmaus.name,2019:Schematron:Testsuite";
    static final String NS_SCHEMATRON = "http://purl.oclc.org/dsdl/schematron";

    static final String NAME_QUERYBINDING = "queryBinding";
    static final String NAME_SECONDARY = "secondary";
    static final String NAME_PRIMARY = "primary";
    static final String NAME_SCHEMAS = "schemas";
    static final String NAME_SCHEMA = "schema";
    static final String NAME_EXPECTATION = "expectation";
    static final String NAME_EXPECT = "expect";

    final Document document;

    TestcaseSpec (final Document document)
    {
        this.document = document;
    }

    boolean isExpectValid ()
    {
        String value = document.getDocumentElement().getAttribute(NAME_EXPECT);
        if ("valid".equals(value)) {
            return true;
        }
        return false;
    }

    boolean isExpectError ()
    {
        String value = document.getDocumentElement().getAttribute(NAME_EXPECT);
        return "error".equals(value);
    }

    boolean isOptional ()
    {
        if (document.getDocumentElement().hasAttribute("optional")) {
            String value = document.getDocumentElement().getAttribute("optional");
            return Boolean.parseBoolean(value);
        }
        return false;
    }

    Element getPrimaryDocument ()
    {
        return (Element)document.getElementsByTagNameNS(NS, NAME_PRIMARY).item(0);
    }

    NodeList getSecondaryDocuments ()
    {
        return document.getElementsByTagNameNS(NS, NAME_SECONDARY);
    }

    String getPhase ()
    {
        Element schema = (Element)document.getElementsByTagNameNS(NS, NAME_SCHEMAS).item(0);
        return schema.getAttribute("phase");
    }

    String getId ()
    {
        return document.getDocumentElement().getAttribute("id");
    }

    String getLabel ()
    {
        String label = document.getElementsByTagNameNS(NS, "label").item(0).getTextContent();
        return label;
    }

    String getReference ()
    {
        Element reference = (Element)document.getElementsByTagNameNS(NS, "reference").item(0);
        if (reference == null) {
            return null;
        }
        return reference.getTextContent();
    }

    String[] getFeatures ()
    {
        return document.getDocumentElement().getAttribute("features").split("[ \t]");
    }

    Element[] getExpectations ()
    {
        List<Element> expect = new ArrayList<Element>();

        NodeList nodes = document.getElementsByTagNameNS(NS, NAME_EXPECTATION);
        for (int i = 0; i < nodes.getLength(); i++) {
            expect.add((Element)nodes.item(i));
        }

        return expect.toArray(new Element[expect.size()]);
    }

    Element getSchema (final String queryBinding) throws ValidationException
    {
        Element unspecificSchema = null;
        NodeList nodes = document.getElementsByTagNameNS(NS_SCHEMATRON, NAME_SCHEMA);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element schema = (Element)nodes.item(i);
            String schemaQueryBinding = schema.getAttribute(NAME_QUERYBINDING);
            if (schemaQueryBinding.equals(queryBinding)) {
                return schema;
            }
            if (schemaQueryBinding.isEmpty()) {
                unspecificSchema = schema;
            }
        }

        if (unspecificSchema == null) {
            throw new ValidationException("Unable to obtain schema for query binding " + queryBinding);
        }

        unspecificSchema = (Element)unspecificSchema.cloneNode(true);
        unspecificSchema.setAttribute(NAME_QUERYBINDING, queryBinding);
        return unspecificSchema;
    }
}
