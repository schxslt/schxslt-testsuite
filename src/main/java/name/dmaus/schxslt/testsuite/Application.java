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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.IOException;

public final class Application
{
    final Loader loader = new Loader();

    final ValidationFactory validationFactory;
    final List<String> skipTestcaseIds;

    public Application (final String configfile, final String implementation, final String[] skipTestcaseIds)
    {
        ApplicationContext ctx = new FileSystemXmlApplicationContext(configfile);
        validationFactory = (ValidationFactory)ctx.getBean(implementation);
        if (skipTestcaseIds == null) {
            this.skipTestcaseIds = new ArrayList<String>();
        } else {
            this.skipTestcaseIds = Arrays.asList(skipTestcaseIds);
        }
    }

    public Report run (final Path testsuite)
    {
        Driver driver = new Driver(validationFactory);
        Report report = new Report();

        for (Path file : findTestcases(testsuite)) {
            Testcase testcase = loader.loadTestcase(file);
            if (skipTestcaseIds != null && skipTestcaseIds.contains(testcase.getId())) {
                report.addValidationResult(new ValidationResult(testcase, ValidationStatus.SKIPPED));
            } else {
                report.addValidationResult(driver.run(testcase));
            }
        }
        return report;
    }

    public static void main (final String[] args)
    {
        Configuration config = new Configuration();
        config.parse(args);

        Application app = new Application(config.getConfigfile(), config.getValidationFactoryName(), config.getSkipTestcaseIds());

        Report report = app.run(Paths.get(config.getTestsuite()));
        boolean success = true;
        for (ValidationResult result : report.getValidationResults()) {
            if (result.getStatus() == ValidationStatus.FAILURE && !result.getTestcase().isOptional()) {
                success = false;
            }
            System.out.println(result.getStatus() + " [" + result.getTestcase().getId() + "] " + result.getTestcase().getLabel());
        }
        if (success) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    List<Path> findTestcases (final Path directory)
    {
        TestcaseCollector testcases = new TestcaseCollector();
        try {
            Files.walkFileTree(directory, testcases);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return testcases.files;
    }
}
