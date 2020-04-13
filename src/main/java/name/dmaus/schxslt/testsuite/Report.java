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

import java.util.Date;

import java.util.List;
import java.util.ArrayList;

public final class Report
{
    final Date timestamp = new Date();
    final List<ValidationResult> results = new ArrayList<ValidationResult>();

    int countSuccess;
    int countFailure;
    int countSkipped;
    int countError;

    String label;

    public Report ()
    {}

    public void addValidationResult (final ValidationResult result)
    {
        switch (result.getStatus()) {
        case SUCCESS:
            countSuccess++;
            break;
        case SKIPPED:
            countSkipped++;
            break;
        case ERROR:
            countError++;
            break;
        default:
            countFailure++;
        }
        results.add(result);
    }

    public List<ValidationResult> getValidationResults ()
    {
        return results;
    }

    public int countSkipped ()
    {
        return countSkipped;
    }

    public int countSuccess ()
    {
        return countSuccess;
    }

    public int countFailure ()
    {
        return countFailure;
    }

    public int countTotal ()
    {
        return results.size();
    }

    public int countError ()
    {
        return countError;
    }

    public Date getTimestamp ()
    {
        return timestamp;
    }

    public boolean hasLabel ()
    {
        if (label == null) {
            return false;
        }
        return true;
    }

    public String getLabel ()
    {
        return label;
    }

    public void setLabel (final String labelStr)
    {
        this.label = labelStr;
    }

}
