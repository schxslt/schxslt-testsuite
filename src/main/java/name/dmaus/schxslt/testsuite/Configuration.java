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

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

class Configuration
{
    final DefaultParser parser = new DefaultParser();
    final Options options = new Options();

    CommandLine arguments;

    Configuration ()
    {
        options.addRequiredOption("b", "bean", true, "Name of ValidationFactory bean");
        options.addRequiredOption("c", "config", true, "Spring beans configuration file");
        options.addRequiredOption("t", "testcases", true, "Path to directory with testcases");
        options.addOption("s", "skip", true, "Skip testcases with this id");
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

    String getValidationFactoryName ()
    {
        return arguments.getOptionValue("b");
    }

    String getTestsuite ()
    {
        return arguments.getOptionValue("t");
    }

    String getConfigfile ()
    {
        return arguments.getOptionValue("c");
    }

    String[] getSkipTestcaseIds ()
    {
        return arguments.getOptionValues("s");
    }

    void printHelp ()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getName(), options, true);
    }

}
