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

import javax.xml.transform.TransformerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.logging.Logger;

import name.dmaus.schxslt.testsuite.ValidationFactory;
import name.dmaus.schxslt.testsuite.XMLSerializer;
import name.dmaus.schxslt.testsuite.Testsuite;
import name.dmaus.schxslt.testsuite.Driver;

import org.w3c.dom.Document;

public final class SchXslt
{
    static final Logger log = Logger.getLogger(SchXslt.class.getName());

    final ValidationFactory validationFactory;
    final Testsuite testsuite;
    final Path output;

    SchXslt (final ValidationFactory validationFactory, final String testsuite, final String output)
    {
        this.validationFactory = validationFactory;
        this.testsuite = Testsuite.newInstance(Paths.get(testsuite));
        this.output = Paths.get(output);
    }

    public static void main (final String[] args)
    {
        SchXsltConfiguration config = new SchXsltConfiguration();
        config.parse(args);

        ValidationFactory validationFactory = createValidationFactory(config.getProcessor(), config.getQueryBinding());
        String testsuite = config.getTestsuite();
        String output = config.getOutput();

        SchXslt application = new SchXslt(validationFactory, testsuite, output);
        application.run();
    }

    static ValidationFactory createValidationFactory (final String processor, final String queryBinding)
    {
        ValidationFactory validationFactory = null;
        switch (processor) {
        case "saxon-he":
            validationFactory = new SchXsltJavaValidationFactory(createTransformerFactory("net.sf.saxon.TransformerFactoryImpl"), queryBinding, "Saxon HE");
            break;
        case "saxon-pe":
            validationFactory = new SchXsltJavaValidationFactory(createTransformerFactory("com.saxonica.config.ProfessionalTransformerFactory"), queryBinding, "Saxon PE");
            break;
        case "saxon-ee":
            validationFactory = new SchXsltJavaValidationFactory(createTransformerFactory("com.saxonica.config.EnterpriseTransformerFactory"), queryBinding, "Saxon EE");
            break;
        case "xalan":
            validationFactory = new SchXsltJavaValidationFactory(createTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl"), queryBinding, "Xalan");
            break;
        default:
            String message = "Unsupported processor: " + processor;
            log.severe(message);
            throw new RuntimeException(message);
        }
        return validationFactory;
    }

    static TransformerFactory createTransformerFactory (final String classname)
    {
        try {
            return (TransformerFactory)Class.forName(classname).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.severe("Cannot find TransformerFactory class: " + classname);
            throw new RuntimeException(e);
        }
    }

    void run ()
    {
        Driver driver = new Driver(validationFactory);
        Document report = driver.run(testsuite);

        XMLSerializer serializer = new XMLSerializer();
        serializer.serialize(report, output);
    }

}
