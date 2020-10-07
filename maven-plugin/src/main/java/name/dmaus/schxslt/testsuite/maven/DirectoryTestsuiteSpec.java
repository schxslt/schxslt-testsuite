/*
 * Copyright 2020 by David Maus <dmaus@dmaus.name>
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

package name.dmaus.schxslt.testsuite.maven;

import name.dmaus.schxslt.testsuite.Testsuite;
import name.dmaus.schxslt.testsuite.DirectoryTestsuite;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

import java.util.List;

/**
 * Directory based Testsuite descriptor.
 *
 */
public final class DirectoryTestsuiteSpec implements TestsuiteSpec
{
    @Parameter(required = true)
    private File directory;

    @Parameter(required = true)
    private String label;

    @Parameter(required = true)
    private String processorId;

    @Parameter(required = false)
    private List<String> skip;

    public Testsuite createTestsuite ()
    {
        return new DirectoryTestsuite(directory.toPath(), label);
    }

    File getDirectory ()
    {
        return directory;
    }

    public String getLabel ()
    {
        return label;
    }

    public String getProcessorId ()
    {
        return processorId;
    }

    public List<String> getSkip ()
    {
        return skip;
    }
}
