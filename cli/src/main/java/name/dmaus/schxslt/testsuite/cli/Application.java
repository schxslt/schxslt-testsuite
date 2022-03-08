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


import name.dmaus.schxslt.testsuite.DirectoryTestcaseProvider;
import name.dmaus.schxslt.testsuite.Populator;
import name.dmaus.schxslt.testsuite.Testcase;
import name.dmaus.schxslt.testsuite.TestcaseResult;
import name.dmaus.schxslt.testsuite.TestcaseProvider;
import name.dmaus.schxslt.testsuite.Testsuite;
import name.dmaus.schxslt.testsuite.TestsuiteResult;
import name.dmaus.schxslt.testsuite.TestsuiteRunner;
import name.dmaus.schxslt.testsuite.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Commandline application.
 */
public final class Application
{
    private Application ()
    {
    }

    public static void main (final String[] args) throws Exception
    {
        Configuration config = new Configuration();
        if (!config.parse(args)) {
            System.exit(1);
        }

        ApplicationContext ctx = new FileSystemXmlApplicationContext(config.getConfig());

        Path tempdir = Files.createTempDirectory(Application.class.getName() + "-");
        Populator populator = new Populator(tempdir);

        String title = config.getTitle();
        String queryBinding = config.getQueryBinding();
        Validator validator = (Validator)ctx.getBean(config.getValidator());
        List<Testcase> testcases = collectTestcases(config.getDirectory());

        Testsuite testsuite = new Testsuite(title, testcases, validator, queryBinding);
        TestsuiteRunner runner = new TestsuiteRunner(populator);

        final TestsuiteResult result = runner.run(testsuite);

        printResult(result);

        FileUtils.deleteDirectory(tempdir.toFile());

        System.exit(0);
    }

    private static void printResult (final TestsuiteResult result)
    {
        System.out.println();
        System.out.println(result.getTestsuite().getTitle());
        System.out.println();

        int pass = 0;
        int fail = 0;
        int skip = 0;

        for (TestcaseResult tcResult : result.getResults()) {
            switch (tcResult.getStatus()) {
            case PASS:
                pass++;
                break;
            case FAIL:
                fail++;
                break;
            case SKIP:
                skip++;
                break;
            default:
                break;
            }
            String msg = String.format("  %s\t%s", tcResult.getStatus(), tcResult.getTestcase().getTitle());
            System.out.println(msg);
        }

        String msg = String.format("%n%d tests, %d failures, %d skipped%n", pass + fail + skip, fail, skip);
        System.out.println(msg);
    }

    private static List<Testcase> collectTestcases (final String basedir) throws IOException
    {
        TestcaseProvider provider = new DirectoryTestcaseProvider(Paths.get(basedir));
        return provider.getTestcases();
    }
}
