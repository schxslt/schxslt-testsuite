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
import java.nio.file.Files;

import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

/**
 * Perform an XSL transformation by executing a commandline application.
 *
 */
final class CommandlineTransformer
{
    private final Map<String, String> parameters = new HashMap<String, String>();
    private final Path stylesheet;
    private final CommandlineBuilder commandlineBuilder;

    CommandlineTransformer (final Path stylesheet, final CommandlineBuilder commandlineBuilder)
    {
        this.stylesheet = stylesheet;
        this.commandlineBuilder = commandlineBuilder;
    }

    void setParameter (final String name, final String value)
    {
        parameters.put(name, value);
    }

    Path transform (final Path document) throws IOException, InterruptedException
    {
        Path target = Files.createTempFile(null, null);
        DeleteTemporaryFiles.add(target);

        commandlineBuilder
            .reset()
            .setDocument(document)
            .setStylesheet(stylesheet)
            .setParameters(parameters)
            .setTarget(target);

        String[] call = commandlineBuilder.build();

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(call);

        int exitcode = process.waitFor();
        if (exitcode != 0) {
            throw new RuntimeException("Executing the external command failed with exit code " + exitcode);
        }

        return target;
    }
}
