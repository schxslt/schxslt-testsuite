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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import name.dmaus.schxslt.testsuite.ValidationResult;
import name.dmaus.schxslt.testsuite.ValidationStatus;
import name.dmaus.schxslt.testsuite.Report;
import name.dmaus.schxslt.testsuite.ReportSerializer;
import name.dmaus.schxslt.testsuite.Application;
import name.dmaus.schxslt.testsuite.XMLSerializer;

import java.util.List;

import java.io.File;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.nio.file.Paths;


@Mojo(name = "test-schematron")
public class TestSchematronMojo extends AbstractMojo
{
    @Parameter(required = true)
    File testDir;

    @Parameter(required = true)
    File configFile;

    @Parameter(required = true)
    List<Processor> processors;

    public void execute () throws MojoExecutionException, MojoFailureException
    {
        boolean failMojoExecution = false;
        for (Processor processor : processors) {
            Application app = new Application(configFile.toURI().toString(), processor.id, processor.skip);
            Report report = app.run(Paths.get(testDir.toURI()));

            for (ValidationResult result : report.getValidationResults()) {
                final String msg = String.format("Status: %s Id: %s Label: %s", result.getStatus(), result.getTestcase().getId(), result.getTestcase().getLabel());
                if (result.getStatus() == ValidationStatus.FAILURE) {
                    getLog().error(msg);
                } else if (result.getStatus() == ValidationStatus.SKIPPED) {
                    getLog().info(msg);
                }
            }

            final String msg = String.format("[Passed/Skipped/Failed/Total] = [%d/%d/%d/%d]", report.countSuccess(), report.countSkipped(), report.countFailure(), report.countTotal());
            if (report.countFailure() > 0) {
                failMojoExecution = true;
                getLog().error(msg);
            } else {
                getLog().info(msg);
            }

            if (processor.report != null) {
                serializeReport(report, processor.report);
            }

        }
        if (failMojoExecution) {
            throw new MojoFailureException("Some Schematron tests failed");
        }
    }

    void serializeReport (final Report report, final File file) throws MojoExecutionException
    {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            ReportSerializer reportSerializer = new ReportSerializer(builder);
            XMLSerializer xmlSerializer = new XMLSerializer();

            xmlSerializer.serialize(reportSerializer.serialize(report), Paths.get(file.toURI()));

        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException("Cannot create DocumentBuilder instance", e);
        }
    }
}
