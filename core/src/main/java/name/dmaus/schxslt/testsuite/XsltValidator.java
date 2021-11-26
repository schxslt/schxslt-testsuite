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
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * Generic XSLT-based Schematron validation.
 */
final class XsltValidator implements Validator
{
    private static final String SVRL_NAMESPACE_URI = "http://purl.oclc.org/dsdl/svrl";

    private final TransformerFactory transformerFactory;
    private final TemplateProvider templates;
    private final Map<String, String> options;

    XsltValidator (final TransformerFactory transformerFactory, final TemplateProvider templates, final Map<String, String> options)
    {
        this.transformerFactory = transformerFactory;
        this.templates = templates;
        this.options = options;
    }

    public ValidationResult validate (Path document, Path schema)
    {
        try {
            Transformer transpiler = compile(schema);
            Source source = new StreamSource(Files.newInputStream(document));
            DOMResult result = new DOMResult();

            transpiler.transform(source, result);

            return checkValidationResult((Document)result.getNode());

        } catch (IOException e) {
            throw new RuntimeException("Unable to load document", e);
        } catch (TransformerException e) {
            return new ValidationResult(ValidationResult.Status.ERROR);
        }
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

    private Transformer compile (final Path schema)
    {
        try {
            Source source = new StreamSource(Files.newInputStream(schema), schema.toString());
            for (Templates template : templates.getTemplates(transformerFactory)) {
                Transformer transformer = template.newTransformer();
                configureTransformer(transformer, options);
                DOMResult result = new DOMResult();
                transformer.transform(source, result);
                source = new DOMSource(result.getNode(), result.getSystemId());
            }
            return transformerFactory.newTransformer(source);
        } catch (TransformerException | IOException e) {
            throw new RuntimeException("Unable to compile schema to XSLT", e);
        }
    }

    private void configureTransformer (final Transformer transformer, final Map<String, String> parameters)
    {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }
    }

}
