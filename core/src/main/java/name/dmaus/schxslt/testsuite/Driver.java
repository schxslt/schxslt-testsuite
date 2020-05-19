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

import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import java.util.List;

import org.w3c.dom.Document;

public final class Driver
{
    final XMLSerializer serializer = new XMLSerializer();

    ValidationFactory validationFactory;

    public Driver (final ValidationFactory validationFactory)
    {
        this.validationFactory = validationFactory;
    }

    ValidationResult execute (final Testcase testcase)
    {
        ValidationStatus status = ValidationStatus.FAILURE;
        String errorMessage = null;
        Document report = null;

        try {
            Validation validation = validationFactory.newInstance();

            if (isFeatureMatch(validation, testcase)) {
                testcase.populate(validationFactory.getQueryBinding());

                validation.setSchema(testcase.getSchema());
                validation.setDocument(testcase.getDocument());
                validation.setPhase(testcase.getPhase());

                boolean success;

                validation.execute();
                if (validation.isValid() == testcase.isExpectValid()) {
                    success = true;
                } else {
                    success = false;
                }

                List<Expectation> expectations = testcase.getExpectations();
                report = (Document)validation.getReport();
                if (!expectations.isEmpty() && report == null) {
                    throw new ValidationException("Cannot check expectations because there is no report");
                } else {
                    serializer.serialize(report, testcase.getReport());
                    for (Expectation expectation : expectations) {
                        success = success && expectation.isSatisfied(report);
                    }
                }
                if (success) {
                    status = ValidationStatus.SUCCESS;
                } else {
                    status = ValidationStatus.FAILURE;
                }
            } else {
                status = ValidationStatus.SKIPPED;
                errorMessage = String.format("Required features not supported: %s", testcase.getFeatures());
            }
        } catch (ValidationException e) {
            if (testcase.isExpectError()) {
                status = ValidationStatus.SUCCESS;
            }
            errorMessage = e.getMessage();
        } catch (XPathExpressionException e) {
            errorMessage = e.getMessage();
        }

        return new ValidationResult(testcase, status, report, errorMessage);
    }

    boolean isFeatureMatch (final Validation validation, final Testcase testcase)
    {
        Set<String> validationFeatures = validation.getFeatures();
        Set<String> testcaseFeatures = testcase.getFeatures();
        if (validationFeatures.containsAll(testcaseFeatures)) {
            return true;
        }
        return false;
    }
}
