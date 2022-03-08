/*
 * Copyright (C) 2022 by David Maus <dmaus@dmaus.name>
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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide testcases from a directory.
 */
public final class DirectoryTestcaseProvider implements TestcaseProvider
{
    private final Path directory;
    private final PathMatcher matcher;
    private final TestcaseFactory testcaseFactory = new TestcaseFactory();

    public DirectoryTestcaseProvider (final Path directory)
    {
        this(directory, null);
    }

    public DirectoryTestcaseProvider (final Path directory, final PathMatcher matcher)
    {
        this.directory = directory;
        if (matcher == null) {
            this.matcher = directory.getFileSystem().getPathMatcher("glob:**.xml");
        } else {
            this.matcher = matcher;
        }
    }

    public List<Testcase> getTestcases () throws IOException
    {
        List<Testcase> testcases = new ArrayList<Testcase>();
        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile (final Path file, final BasicFileAttributes attrs) throws IOException
                {
                    if (attrs.isRegularFile() && matcher.matches(file)) {
                        testcases.add(testcaseFactory.newInstance(file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            };
        Files.walkFileTree(directory, visitor);
        return testcases;
    }
}
