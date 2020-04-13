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

import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.List;
import java.util.ArrayList;

import javax.xml.transform.TransformerFactory;

public final class JavaValidationFactory implements ValidationFactory
{
    final String[] features;
    final List<String> compilerSteps;
    final TransformerFactory transformerFactory;

    final String label;
    final String queryBinding;

    Path baseDirectory;

    public JavaValidationFactory (final String label, final String queryBinding, final TransformerFactory transformerFactory, final String[] features, final List<String> compilerSteps)
    {
        this.label = label;
        this.queryBinding = queryBinding;
        this.transformerFactory = transformerFactory;
        this.compilerSteps = compilerSteps;
        this.features = features;
    }

    public void setBaseDirectory (final Path basedir)
    {
        baseDirectory = basedir;
    }

    public String getLabel ()
    {
        return label;
    }

    public String getQueryBinding ()
    {
        return queryBinding;
    }

    public JavaValidation newInstance ()
    {
        List<Path> steps = new ArrayList<Path>();
        for (String step : compilerSteps) {
            steps.add(baseDirectory.resolve(Paths.get(step)));
        }

        return new JavaValidation(transformerFactory, features, steps);
    }
}
