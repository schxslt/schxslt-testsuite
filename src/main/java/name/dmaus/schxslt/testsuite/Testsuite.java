/*
 * Copyright (C) 2019,2020 by David Maus <dmaus@dmaus.name>
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.ArrayList;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Testsuite
{
    static final Loader loader = new Loader();

    final TestsuiteSpec spec;
    final List<Testcase> testcases = new ArrayList<Testcase>();

    Testsuite (final TestsuiteSpec spec)
    {
        this.spec = spec;
    }

    public String getLabel ()
    {
        return spec.getLabel();
    }

    public List<Testcase> getTestcases ()
    {
        if (testcases.isEmpty()) {
            URI baseURI;
            try {
                baseURI = new URI(spec.getBaseURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            Path base = Paths.get(baseURI);

            String[] locations = spec.getTestcaseLocations();
            for (int i = 0; i < locations.length; i++) {
                Path path = base.getParent().resolve(locations[i]);
                Testcase testcase = loader.loadTestcase(path);
                testcases.add(testcase);
            }
        }

        return testcases;
    }

    public static Testsuite newInstance (final Path location)
    {
        return loader.loadTestsuite(location);
    }
}
