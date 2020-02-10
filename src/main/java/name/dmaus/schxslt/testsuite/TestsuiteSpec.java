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

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;

class TestsuiteSpec
{
    static final String NS = "tag:dmaus@dmaus.name,2019:Schematron:Testsuite";

    final Document document;

    TestsuiteSpec (final Document document)
    {
        this.document = document;
    }

    String[] getTestcaseLocations ()
    {
        List<String> testcases = new ArrayList<String>();

        NodeList nodes = document.getElementsByTagNameNS(NS, "testcase");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element testcase = (Element)nodes.item(i);
            testcases.add(testcase.getAttribute("href"));
        }

        return testcases.toArray(new String[testcases.size()]);
    }

    String getBaseURI ()
    {
        return document.getDocumentURI();
    }

    String getLabel ()
    {
        String label = document.getElementsByTagNameNS(NS, "label").item(0).getTextContent();
        return label;
    }

    String getQueryBinding ()
    {
        return document.getDocumentElement().getAttribute("queryBinding");
    }
}
