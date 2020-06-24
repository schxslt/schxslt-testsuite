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

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;

import javax.xml.transform.Source;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamSource;

/**
 * Java-based implementations.
 *
 */
public final class JavaValidation implements Validation
{
    private static final String NSSVRL = "http://purl.oclc.org/dsdl/svrl";

    private final List<Path> compilerSteps;
    private final TransformerFactory transformerFactory;
    private final Set<String> features = new HashSet<String>();

    private final ErrorListener errorListener = new ErrorListener () {
            public void error (final TransformerException exception)
            {
            }

            public void warning (final TransformerException exception)
            {
            }

            public void fatalError (final TransformerException exception) throws TransformerException
            {
                throw new TransformerException(exception);
            }
        };

    private Path schema;
    private Path document;
    private String phase;
    private Document report;

    public JavaValidation (final TransformerFactory transformerFactory, final String[] features, final List<Path> compilerSteps)
    {
        this.transformerFactory = transformerFactory;
        this.compilerSteps = compilerSteps;
        for (int i = 0; i < features.length; i++) {
            this.features.add(features[i]);
        }
    }

    public void setSchema (final Path path)
    {
        schema = path;
    }

    public void setDocument (final Path path)
    {
        document = path;
    }

    public void setPhase (final String string)
    {
        phase = string;
    }

    public Set<String> getFeatures ()
    {
        return features;
    }

    public Document getReport ()
    {
        return report;
    }

    public boolean isAvailable ()
    {
        return true;
    }

    public boolean isValid ()
    {
        NodeList asserts = report.getElementsByTagNameNS(NSSVRL, "failed-assert");
        NodeList reports = report.getElementsByTagNameNS(NSSVRL, "successful-report");
        if (reports.getLength() + asserts.getLength() > 0) {
            return false;
        }
        return true;
    }

    public void execute () throws ValidationException
    {
        try {

            StreamSource source = new StreamSource(Files.newInputStream(document));
            source.setSystemId(document.toString());

            DOMResult result = new DOMResult();

            Transformer transformer = compileSchematron();

            transformer.transform(source, result);

            report = (Document)result.getNode();

        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    /*
     * Compile schema to validating stylesheet.
     *
     * @return Transformer
     */
    Transformer compileSchematron () throws IOException, TransformerException
    {
        Source source = new StreamSource(Files.newInputStream(schema), schema.toString());
        for (Path step : compilerSteps) {
            final Transformer transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(step), step.toString()));
            final DOMResult result = new DOMResult();
            transformer.setErrorListener(errorListener);
            if (phase != null && !phase.isEmpty()) {
                transformer.setParameter("phase", phase);
            }
            transformer.transform(source, result);
            source = new DOMSource(result.getNode(), result.getSystemId());
        }
        return transformerFactory.newTransformer(source);
    }
}
