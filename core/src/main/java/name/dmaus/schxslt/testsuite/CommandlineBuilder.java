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

import java.nio.file.Path;

import java.util.Map;

/**
 * Base class of commandline builders.
 *
 */
abstract class CommandlineBuilder
{
    Path target;
    Path document;
    Path stylesheet;
    Map<String, String> parameters;

    CommandlineBuilder setDocument (final Path path)
    {
        document = path;
        return this;
    }

    CommandlineBuilder setStylesheet (final Path path)
    {
        stylesheet = path;
        return this;
    }

    CommandlineBuilder setTarget (final Path path)
    {
        target = path;
        return this;
    }

    CommandlineBuilder setParameters (final Map<String, String> map)
    {
        parameters = map;
        return this;
    }

    CommandlineBuilder reset ()
    {
        this.target = null;
        this.stylesheet = null;
        this.document = null;
        this.parameters = null;
        return this;
    }

    abstract String[] build ();
    abstract String[] buildIsAvailableCommand ();

}
