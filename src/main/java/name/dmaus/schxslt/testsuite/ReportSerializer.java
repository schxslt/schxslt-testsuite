/*
 * Copyright (C) 2020 by David Maus <dmaus@dmaus.name>
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

import javax.xml.parsers.DocumentBuilder;

import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class ReportSerializer
{
    final static String NS = "tag:dmaus@dmaus.name,2020:Schematron:Testsuite:Report";

    final DocumentBuilder documentBuilder;
    final SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public ReportSerializer (final DocumentBuilder documentBuilder)
    {
        this.documentBuilder = documentBuilder;
    }

    public Document serialize (final Report report)
    {
        Document document = documentBuilder.newDocument();
        Element root = document.createElementNS(NS, "report");
        root.setAttribute("timestamp", dates.format(report.getTimestamp()));
        root.setAttribute("success", Integer.toString(report.countSuccess()));
        root.setAttribute("failure", Integer.toString(report.countFailure()));
        root.setAttribute("skipped", Integer.toString(report.countSkipped()));

        for (ValidationResult result : report.getValidationResults()) {
            appendValidationResult(root, result);
        }

        document.appendChild(root);
        return document;
    }

    void appendValidationResult (final Element parent, final ValidationResult result)
    {
        Testcase testcase = result.getTestcase();
        Document document = parent.getOwnerDocument();

        Element element = document.createElementNS(NS, "testcase");
        element.setAttribute("status", result.getStatus().toString().toLowerCase());
        element.setAttribute("id", testcase.getId());

        element
            .appendChild(document.createElementNS(NS, "label"))
            .appendChild(document.createTextNode(testcase.getLabel()));

        Element primary = document.createElementNS(NS, "document");
        if (testcase.getDocument() != null) {
            primary.setAttribute("href", testcase.getDocument().toUri().toString());
            element.appendChild(primary);
        }

        Element schema = document.createElementNS(NS, "schema");
        if (testcase.getDocument() != null) {
            schema.setAttribute("href", testcase.getSchema().toUri().toString());
            element.appendChild(schema);
        }

        Element report = document.createElementNS(NS, "report");
        if (testcase.getReport() != null) {
            report.setAttribute("href", testcase.getReport().toUri().toString());
            element.appendChild(report);
        }

        if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
            Element message = document.createElementNS(NS, "message");
            message.appendChild(document.createTextNode(result.getErrorMessage()));
            element.appendChild(message);
        }

        parent.appendChild(element);
    }
}
