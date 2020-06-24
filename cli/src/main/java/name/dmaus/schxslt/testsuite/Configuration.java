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

/**
 * Commandline application configuration.
 *
 */
final class Configuration
{
    private static final String OPTION_BEAN_SHORT = "b";
    private static final String OPTION_CONFIG_SHORT = "c";
    private static final String OPTION_TESTCASES_SHORT = "t";
    private static final String OPTION_LABEL_SHORT = "l";
    private static final String OPTION_SKIP_SHORT = "s";

    private final DefaultParser parser = new DefaultParser();
    private final Options options = new Options();

    private CommandLine arguments;

    Configuration ()
    {
        options.addRequiredOption(OPTION_BEAN_SHORT, "bean", true, "Name of ValidationFactory bean");
        options.addRequiredOption(OPTION_CONFIG_SHORT, "config", true, "Spring beans configuration file");
        options.addRequiredOption(OPTION_TESTCASES_SHORT, "testcases", true, "Testsuite directory");
        options.addOption(OPTION_LABEL_SHORT, "label", true, "Testsuite label");
        options.addOption(OPTION_SKIP_SHORT, "skip", true, "Skip testcases with this id");
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

    String getLabel ()
    {
        return arguments.getOptionValue(OPTION_LABEL_SHORT);
    }

    String getValidationFactoryName ()
    {
        return arguments.getOptionValue(OPTION_BEAN_SHORT);
    }

    String getTestsuite ()
    {
        return arguments.getOptionValue(OPTION_TESTCASES_SHORT);
    }

    String getConfigfile ()
    {
        return arguments.getOptionValue(OPTION_CONFIG_SHORT);
    }

    String[] getSkipTestcaseIds ()
    {
        return arguments.getOptionValues(OPTION_SKIP_SHORT);
    }

    void printHelp ()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getName(), options, true);
    }

}
