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

import java.nio.file.Path;

import java.util.Set;

import org.w3c.dom.Document;

/**
 * Public interface of a Validation implementation.
 *
 * @author David Maus &lt;dmaus@dmaus.name&gt;
 */
public interface Validation
{
    /**
     * Set schema document.
     *
     * @param schema Schema document
     */
    void setSchema (Path schema);

    /**
     * Set instance document.
     *
     * @param document Instance document
     */
    void setDocument (Path document);

    /**
     * Set validation phase.
     *
     * @param phase Validation phase
     */
    void setPhase (String phase);

    /**
     * Return supported features.
     *
     * @return Supported features
     */
    Set<String> getFeatures ();

    /**
     * Return true if the document is valid.
     *
     * @return True if the document is valid, otherwise false.
     */
    boolean isValid ();

    /**
     * Return validation report, if any.
     *
     * @return Validation report or null if there is no report
     */
    Document getReport ();

    /**
     * Execute the validation.
     *
     * @throws ValidationException Error while executing the validation
     */
    void execute () throws ValidationException;
}
