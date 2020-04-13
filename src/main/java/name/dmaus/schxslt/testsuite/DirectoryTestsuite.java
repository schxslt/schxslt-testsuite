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

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.List;
import java.util.ArrayList;

public class DirectoryTestsuite implements Testsuite
{
    final Path directory;
    final String label;

    public DirectoryTestsuite (final Path directory, final String label)
    {
        this.label = label;
        this.directory = directory;
    }

    public String getLabel ()
    {
        return label;
    }

    public List<Testcase> getTestcases ()
    {
        final List<Testcase> testcases = new ArrayList<Testcase>();
        final TestcaseLoader loader = new TestcaseLoader();
        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile (final Path file, final BasicFileAttributes attrs) throws IOException
                {
                    if (file.toString().endsWith(".xml")) {
                        testcases.add(loader.load(file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            };
        try {
            Files.walkFileTree(directory, visitor);
        } catch (IOException e) {
            throw new RuntimeException("Error walking directory " + directory.toAbsolutePath().toString(), e);
        }
        return testcases;
    }
}
