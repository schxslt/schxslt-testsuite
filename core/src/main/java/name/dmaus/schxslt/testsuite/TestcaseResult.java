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

/**
 * Result of running a testcase.
 */
public final class TestcaseResult
{
    /**
     * Testcase result status.
     */
    public enum Status { PASS, FAIL, SKIP }

    private final ValidationResult validationResult;
    private final PopulatedTestcase testcase;
    private final Status status;

    TestcaseResult (final Status status, PopulatedTestcase testcase, ValidationResult validationResult)
    {
        this.status = status;
        this.testcase = testcase;
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult ()
    {
        return validationResult;
    }

    public PopulatedTestcase getTestcase ()
    {
        return testcase;
    }

    public Status getStatus ()
    {
        return status;
    }
}
