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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.text.SimpleDateFormat;

class ReportBuilder
{
    static final String NS = "tag:dmaus@dmaus.name,2019:Schematron:Testsuite";

    final DocumentBuilder documentBuilder;
    final List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ssZ");

    String label;
    String queryBinding;
    String productLabel;

    ReportBuilder ()
    {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    Document build ()
    {
        Document document = documentBuilder.newDocument();

        Element report = (Element)document.appendChild(document.createElementNS(NS, "report"));

        addAttribute(report, "queryBinding", queryBinding);
        addAttribute(report, "timestamp", simpleDateFormat.format(new Date()));
        addTerminalElement(report, "label", label);
        addTerminalElement(report, "product", productLabel);

        for (int i = 0; i < validationResults.size(); i++) {
            ValidationResult validationResult = validationResults.get(i);
            Element testcase = addNonTerminalElement(report, "testcase");

            addAttribute(testcase, "id", validationResult.getTestcase().getId());
            addAttribute(testcase, "phase", validationResult.getTestcase().getPhase());
            if (validationResult.getTestcase().isExpectError()) {
                addAttribute(testcase, "expect", "error");
            } else if (validationResult.getTestcase().isExpectValid()) {
                addAttribute(testcase, "expect", "valid");
            } else {
                addAttribute(testcase, "expect", "invalid");
            }
            if (validationResult.getTestcase().isOptional()) {
                addAttribute(testcase, "optional", "true");
            } else {
                addAttribute(testcase, "optional", "false");
            }
            addAttribute(testcase, "status", validationResult.getStatus().name().toLowerCase());
            addTerminalElement(testcase, "label", validationResult.getTestcase().getLabel());
            addTerminalElement(testcase, "reference", validationResult.getTestcase().getReference());
            if (validationResult.getErrorMessage() != null) {
                addTerminalElement(testcase, "message", validationResult.getErrorMessage());
            }
            addAttribute(addTerminalElement(testcase, "document", null), "href", validationResult.getTestcase().getDocument().toString());
            addAttribute(addTerminalElement(testcase, "schema", null), "href", validationResult.getTestcase().getSchema().toString());
            if (validationResult.getReport() != null) {
                addAttribute(addTerminalElement(testcase, "report", null), "href", validationResult.getTestcase().getReport().toString());
            }
        }

        return document;
    }

    void setLabel (final String labelStr)
    {
        label = labelStr;
    }

    void setQueryBinding (final String queryBindingStr)
    {
        queryBinding = queryBindingStr;
    }

    void addValidationResult (final ValidationResult validationResult)
    {
        validationResults.add(validationResult);
    }

    void setProductLabel (final String labelStr)
    {
        productLabel = labelStr;
    }

    Element addNonTerminalElement (final Element parent, final String name)
    {
        Element child = parent.getOwnerDocument().createElementNS(NS, name);
        return (Element)parent.appendChild(child);
    }

    Element addTerminalElement (final Element parent, final String name, final String content)
    {
        Element child = parent.getOwnerDocument().createElementNS(NS, name);
        child.setTextContent(content);
        return (Element)parent.appendChild(child);
    }

    void addAttribute (final Element parent, final String name, final String content)
    {
        parent.setAttribute(name, content);
    }
}
