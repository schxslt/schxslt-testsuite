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

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

class SchXsltConfiguration
{
    final DefaultParser parser = new DefaultParser();
    final Options options = new Options();

    CommandLine arguments;

    SchXsltConfiguration ()
    {
        options.addRequiredOption("o", "output", true, "Report file");
        options.addRequiredOption("p", "processor", true, "XSLT processor (saxon-he, saxon-pe, saxon-ee, xalan)");
        options.addRequiredOption("q", "queryBinding", true, "Query binding (xslt, xslt2, xslt3)");
        options.addRequiredOption("t", "testsuite", true, "Testsuite");
    }

    void parse (final String[] args)
    {
        try {
            arguments = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp();
            System.exit(1);
        }
    }

    String getTestsuite ()
    {
        return arguments.getOptionValue("t");
    }

    String getQueryBinding ()
    {
        return arguments.getOptionValue("q");
    }

    String getProcessor ()
    {
        return arguments.getOptionValue("p");
    }

    String getOutput ()
    {
        return arguments.getOptionValue("o");
    }

    private void printHelp ()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getName(), options, true);
    }

}
