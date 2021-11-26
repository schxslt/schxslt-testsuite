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

package name.dmaus.schxslt.testsuite.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Commandline application configuration.
 */
final class Configuration
{
    private static final String OPTION_CONFIG_SHORT = "c";
    private static final String OPTION_DIRECTORY_SHORT = "d";
    private static final String OPTION_TITLE_SHORT = "t";
    private static final String OPTION_VALIDATOR_SHORT = "v";
    private static final String OPTION_QUERYBINDING_SHORT = "q";

    private final DefaultParser parser = new DefaultParser();
    private final Options options = new Options();

    private CommandLine arguments;

    Configuration ()
    {
        options.addRequiredOption(OPTION_CONFIG_SHORT, "config", true, "Spring Beans configuration file");
        options.addRequiredOption(OPTION_TITLE_SHORT, "title", true, "Testsuite title");
        options.addRequiredOption(OPTION_DIRECTORY_SHORT, "directory", true, "Directory with testcase files");
        options.addRequiredOption(OPTION_VALIDATOR_SHORT, "validator", true, "Name of Spring Bean implementing Validator");
        options.addRequiredOption(OPTION_QUERYBINDING_SHORT, "queryBinding", true, "Query language binding");
    }

    boolean parse (final String[] args)
    {
        try {
            arguments = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp();
            return false;
        }
        return true;
    }

    String getValidator ()
    {
        return arguments.getOptionValue(OPTION_VALIDATOR_SHORT);
    }

    String getQueryBinding ()
    {
        return arguments.getOptionValue(OPTION_QUERYBINDING_SHORT);
    }

    String getDirectory ()
    {
        return arguments.getOptionValue(OPTION_DIRECTORY_SHORT);
    }

    String getConfig ()
    {
        return arguments.getOptionValue(OPTION_CONFIG_SHORT);
    }

    String getTitle ()
    {
        return arguments.getOptionValue(OPTION_TITLE_SHORT);
    }

    void printHelp ()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getName(), options, true);
    }
}
