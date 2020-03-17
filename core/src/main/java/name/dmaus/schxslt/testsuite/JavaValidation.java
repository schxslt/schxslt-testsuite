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
import java.nio.file.Paths;
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

public final class JavaValidation implements Validation
{
    static final String NSSVRL = "http://purl.oclc.org/dsdl/svrl";

    final List<String> compilerSteps;
    final TransformerFactory transformerFactory;
    final Set<String> features = new HashSet<String>();

    final ErrorListener errorListener = new ErrorListener () {
            public void error (final TransformerException e) {};
            public void warning (final TransformerException e) {};
            public void fatalError (final TransformerException e) throws TransformerException { throw new TransformerException(e); };
        };

    Path schema;
    Path document;
    String phase;
    Document report;

    public JavaValidation (final TransformerFactory transformerFactory, final String[] features, final List<String> compilerSteps)
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

            Transformer t = compileSchematron();

            t.transform(source, result);

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
        for (String step : compilerSteps) {
            final Path stylesheet = Paths.get(step);
            final Transformer transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(stylesheet), stylesheet.toString()));
            final DOMResult result = new DOMResult();
            transformer.setErrorListener(errorListener);
            if (phase != null) {
                transformer.setParameter("phase", phase);
            }
            transformer.transform(source, result);
            source = new DOMSource(result.getNode(), result.getSystemId());
        }
        return transformerFactory.newTransformer(source);
    }
}
