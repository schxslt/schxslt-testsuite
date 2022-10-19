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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.value.UntypedAtomicValue;

/**
 * Schematron validation with Saxon.
 */
final class SaxonXsltValidator implements Validator
{
    private static final String SVRL_NAMESPACE_URI = "http://purl.oclc.org/dsdl/svrl";

    private final Processor processor;
    private final SaxonTemplateProvider templates;
    private final Map<QName, XdmValue> options;
    private final DocumentBuilder documentBuilder;

    SaxonXsltValidator (final Processor processor, final SaxonTemplateProvider templates, final Map<String, String> options) throws ParserConfigurationException
    {
        this.processor = processor;
        this.templates = templates;
        this.options = new HashMap<QName, XdmValue>();
        for (Map.Entry<String, String> option : options.entrySet()) {
            this.options.put(new QName(option.getKey()), XdmValue.makeValue(new UntypedAtomicValue(option.getValue())));
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    private ValidationResult checkValidationResult (final Document report)
    {
        ValidationResult.Status status = null;
        if (report.getElementsByTagNameNS(SVRL_NAMESPACE_URI, "failed-assert").getLength() == 0
            && report.getElementsByTagNameNS(SVRL_NAMESPACE_URI, "successful-report").getLength() == 0) {
            status = ValidationResult.Status.VALID;
        } else {
            status = ValidationResult.Status.INVALID;
        }
        return new ValidationResult(status, report);
    }


    public ValidationResult validate (final Path document, final Path schema)
    {
        try {
            Xslt30Transformer transpiler = compile(schema);
            Source source = new StreamSource(Files.newInputStream(document), document.toUri().toString());
            Document result = documentBuilder.newDocument();
            transpiler.applyTemplates(source, new DOMDestination(result));
            return checkValidationResult(result);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load document", e);
            // CHECKSTYLE:OFF
        } catch (Exception e) {
            // CHECKSTYLE:ON
            System.err.println(e.getMessage());
            return new ValidationResult(ValidationResult.Status.ERROR);
        }
    }


    private Xslt30Transformer compile (final Path schema) throws SaxonApiException
    {
        try {
            XsltCompiler compiler = processor.newXsltCompiler();
            Source source = new StreamSource(Files.newInputStream(schema), schema.toString());
            for (XsltExecutable template : templates.getTemplates(compiler)) {
                Xslt30Transformer transformer = template.load30();
                XdmDestination result = new XdmDestination();
                result.setDestinationBaseURI(schema.toUri());
                transformer.setStylesheetParameters(options);
                transformer.transform(source, result);
                source = result.getXdmNode().asSource();
            }
            return compiler.compile(source).load30();
        } catch (IOException e) {
            throw new RuntimeException("Unable to compile stylesheet", e);
        }
    }
}
