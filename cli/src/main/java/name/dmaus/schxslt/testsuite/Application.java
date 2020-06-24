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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.nio.file.Paths;

import java.util.Arrays;

/**
 * Commandline application.
 *
 */
public final class Application
{
    private Application ()
    {
    }

    public static void main (final String[] args) throws Exception
    {
        Configuration config = new Configuration();
        config.parse(args);

        Testsuite testsuite = new DirectoryTestsuite(Paths.get(config.getTestsuite()), config.getLabel());
        ApplicationContext ctx = new FileSystemXmlApplicationContext(config.getConfigfile());
        ValidationFactory factory = (ValidationFactory)ctx.getBean(config.getValidationFactoryName());
        Driver driver = new Driver(factory);
        TestsuiteRunner runner;
        if (config.getSkipTestcaseIds() == null) {
            runner = new TestsuiteRunner(driver);
        } else {
            runner = new TestsuiteRunner(driver, Arrays.asList(config.getSkipTestcaseIds()));
        }
        Report report = runner.run(testsuite);

        System.out.println(report.getLabel());
        System.out.println();

        for (ValidationResult result : report.getValidationResults()) {
            Testcase testcase = result.getTestcase();
            System.out.println(String.format("%.8s %s", result.getStatus(), testcase.getLabel()));
            if (result.getStatus() == ValidationStatus.SUCCESS || result.getStatus() == ValidationStatus.SKIPPED) {
                testcase.deleteTemporaryFiles();
            }
            if (result.getStatus() != ValidationStatus.SUCCESS) {
                if (result.getErrorMessage() != null) {
                    System.out.println(String.format("\tmessage: %s", result.getErrorMessage()));
                }
                System.out.println(String.format("\tid: %s directory: %s", testcase.getId(), testcase.getTempDirectory()));
            }
        }

        System.out.println();
        System.out.println(String.format("[Passed/Skipped/Failed/Total] = [%d/%d/%d/%d]", report.countSuccess(), report.countSkipped(), report.countFailure() + report.countError(), report.countTotal()));
        System.out.println();
    }
}
