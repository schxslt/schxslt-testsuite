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

package name.dmaus.schxslt.testsuite.impl;

import java.nio.file.Files;
import java.nio.file.Path;

import org.w3c.dom.Document;

import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.transform.TransformerFactory;

import javax.xml.transform.stream.StreamSource;

import name.dmaus.schxslt.Result;
import name.dmaus.schxslt.Schematron;

import name.dmaus.schxslt.testsuite.ValidationException;
import name.dmaus.schxslt.testsuite.Validation;

import java.util.Set;
import java.util.HashSet;

import java.util.logging.Logger;

class SchXsltJavaValidation implements Validation
{
    final Set<String> features = new HashSet<String>();
    final Logger log = Logger.getLogger(getClass().getName());
    final TransformerFactory transformerFactory;

    Path schema;
    Path document;
    String phase;
    Result result;

    SchXsltJavaValidation (final TransformerFactory transformerFactory)
    {
        this.transformerFactory = transformerFactory;
        features.add("svrl");
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
        return result.getValidationReport();
    }

    public boolean isValid ()
    {
        return result.isValid();
    }

    public void execute () throws ValidationException
    {
        Schematron schematron = null;
        Document validationStylesheet = null;
        try {
            StreamSource schSource = new StreamSource(Files.newInputStream(schema));
            StreamSource docSource = new StreamSource(Files.newInputStream(document));

            schSource.setSystemId(schema.toString());
            docSource.setSystemId(document.toString());

            schematron = Schematron.newInstance(schSource, phase).withTransformerFactory(transformerFactory);

            validationStylesheet = schematron.getValidationStylesheet();

            result = schematron.validate(docSource);

        } catch (Exception e) {
            log.severe("Validation terminated by exception: " + e.getMessage());
            if (validationStylesheet != null) {
                DOMImplementationLS domImplementation = (DOMImplementationLS)validationStylesheet.getImplementation();
                LSSerializer serializer = domImplementation.createLSSerializer();
                log.info(serializer.writeToString(validationStylesheet));
            }
            throw new ValidationException(e);
        }
    }
}
