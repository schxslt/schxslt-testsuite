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

import java.nio.file.Path;

/**
 * A testcase populated to filesystem.
 */
public final class PopulatedTestcase
{
    private final ValidationResult.Status expectedValidationResultStatus;
    private final String title;
    private final Path document;
    private final Path schema;

    PopulatedTestcase (final String title, final ValidationResult.Status expectedValidationResultStatus, final Path schema, final Path document)
    {
        this.title = title;
        this.schema = schema;
        this.document = document;
        this.expectedValidationResultStatus = expectedValidationResultStatus;
    }

    public String getTitle ()
    {
        return title;
    }

    ValidationResult.Status getExpectedValidationResultStatus ()
    {
        return expectedValidationResultStatus;
    }

    Path getSchema ()
    {
        return schema;
    }

    Path getDocument ()
    {
        return document;
    }

}
