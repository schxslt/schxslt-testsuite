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

import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

public final class Driver
{
    static final Logger log = Logger.getLogger(Driver.class.getName());

    ValidationFactory validationFactory;

    public Driver (final ValidationFactory validationFactory)
    {
        this.validationFactory = validationFactory;
    }

    public Document run (final Testsuite testsuite)
    {
        ReportBuilder reportBuilder = new ReportBuilder();

        reportBuilder.setLabel(testsuite.getLabel());
        reportBuilder.setQueryBinding(validationFactory.getQueryBinding());
        reportBuilder.setProductLabel(validationFactory.getLabel());

        Testcase[] testcases = testsuite.getTestcases();
        for (int i = 0; i < testcases.length; i++) {
            testcases[i].populate(validationFactory.getQueryBinding());
            ValidationResult validationResult = runTestcase(testcases[i]);
            reportBuilder.addValidationResult(validationResult);
        }

        return reportBuilder.build();
    }

    ValidationResult runTestcase (final Testcase testcase)
    {
        Validation validation = validationFactory.newInstance();

        validation.setSchema(testcase.getSchema());
        validation.setDocument(testcase.getDocument());
        validation.setPhase(testcase.getPhase());

        ValidationStatus status = ValidationStatus.FAILURE;
        String errorMessage = null;
        Document report = null;

        log.info("Running testcase " + testcase.getId());

        if (isFeatureMatch(validation, testcase)) {

            try {
                boolean success;

                validation.execute();
                if (validation.isValid() == testcase.isExpectValid()) {
                    success = true;
                } else {
                    success = false;
                }

                report = (Document)validation.getReport();

                Expectation[] expectations = testcase.getExpectations();
                for (int i = 0; i < expectations.length; i++) {
                    success = success && expectations[i].isSatisfied(report);
                }
                if (success) {
                    status = ValidationStatus.SUCCESS;
                } else {
                    status = ValidationStatus.FAILURE;
                }
            } catch (ValidationException e) {
                if (testcase.isExpectError()) {
                    status = ValidationStatus.SUCCESS;
                }
                errorMessage = e.getMessage();
            } catch (XPathExpressionException e) {
                errorMessage = e.getMessage();
            }
        } else {
            status = ValidationStatus.SKIPPED;
            errorMessage = String.format("Required features not supported: %s", testcase.getFeatures());
        }

        log.info("Testcase " + testcase.getId() + " " + status);

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
