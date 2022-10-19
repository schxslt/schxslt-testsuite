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

import java.util.ArrayList;
import java.util.List;

/**
 * Run a complete testsuite.
 */
public final class TestsuiteRunner
{
    private final Populator populator;

    public TestsuiteRunner (final Populator populator)
    {
        this.populator = populator;
    }

    public TestsuiteResult run (final Testsuite testsuite)
    {
        String queryBinding = testsuite.getQueryBinding();
        TestcaseRunner runner = new TestcaseRunner(testsuite.getValidator(), populator);
        List<TestcaseResult> results = new ArrayList<TestcaseResult>();
        for (Testcase testcase : testsuite.getTestcases()) {
            if (testcase.getQueryBindings().contains(queryBinding)) {
                TestcaseResult result = runner.run(testcase, queryBinding);
                results.add(result);
            } else {
                TestcaseResult result = new TestcaseResult(TestcaseResult.Status.SKIP, testcase.getTitle(), null);
                results.add(result);
            }
        }
        return new TestsuiteResult(testsuite, results);
    }
}
