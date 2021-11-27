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

package name.dmaus.schxslt.testsuite;

import javax.xml.xpath.XPathExpressionException;

/**
 * Run a single testcase.
 */
final class TestcaseRunner
{
    private final Validator validator;
    private final Populator populator;

    TestcaseRunner (final Validator validator, final Populator populator)
    {
        this.validator = validator;
        this.populator = populator;
    }

    TestcaseResult run (final Testcase testcase, final String queryBinding)
    {
        PopulatedTestcase populatedTestcase = populator.populate(testcase, queryBinding);
        ValidationResult validationResult = validator.validate(populatedTestcase.getDocument(), populatedTestcase.getSchema());

        TestcaseResult.Status status;
        if (populatedTestcase.getExpectedValidationResultStatus().equals(validationResult.getStatus())) {
            status = TestcaseResult.Status.PASS;
        } else {
            status = TestcaseResult.Status.FAIL;
        }

        if (status.equals(TestcaseResult.Status.PASS) && populatedTestcase.getAssertions().size() > 0) {
            if (validationResult.hasReport()) {
                try {
                    for (Assertion assertion : populatedTestcase.getAssertions()) {
                        if (!assertion.test(validationResult.getReport())) {
                            status = TestcaseResult.Status.FAIL;
                            break;
                        }
                    }
                } catch (XPathExpressionException e) {
                    status = TestcaseResult.Status.FAIL;
                }
            } else {
                status = TestcaseResult.Status.FAIL;
            }
        }
        return new TestcaseResult(status, populatedTestcase, validationResult);
    }
}
