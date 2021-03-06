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

import java.util.List;
import java.util.ArrayList;

/**
 * Execute all tests of a testsuite.
 *
 */
public final class TestsuiteRunner
{
    private final Driver driver;
    private final List<String> skipTestcaseIds;

    public TestsuiteRunner (final Driver driver)
    {
        this(driver, new ArrayList<String>());
    }


    public TestsuiteRunner (final Driver driver, final List<String> skipTestcaseIds)
    {
        this.driver = driver;
        this.skipTestcaseIds = skipTestcaseIds;
    }

    public Report run (final Testsuite testsuite)
    {
        Report report = new Report();
        report.setLabel(testsuite.getLabel());

        for (Testcase testcase : testsuite.getTestcases()) {
            ValidationResult result;
            if (skipTestcaseIds.contains(testcase.getId())) {
                result = new ValidationResult(testcase, ValidationStatus.SKIPPED, null, null);
            } else {
                try {
                    result = driver.execute(testcase);
                } catch (Exception e) {
                    result = new ValidationResult(testcase, ValidationStatus.ERROR, null, e.getMessage());
                }
            }
            report.addValidationResult(result);
        }
        return report;
    }

}
