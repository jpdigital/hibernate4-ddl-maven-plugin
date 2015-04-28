/*
 * Copyright (C) 2014 Jens Pelzetter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.jpdigital.maven.plugins.hibernate4ddl.tests;

import de.jpdigital.maven.plugins.hibernate4ddl.GenerateDdlMojo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * TestSuite for testing the {@link GenerateDdlMojo}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DdlMojoTest {

    /**
     * Directory to place the test files in
     */
    private static final String TEST_DIR = "target/test/ddl/test";
    /**
     * Path to a mock {@code persistence.xml} file.
     */
    private static final String TEST_PERSISTENCE_XML
                                    = "src/test/resources/test-persistence.xml";
    /**
     * An instance of the Mojo under test.
     */
    private GenerateDdlMojo mojo;

    ;
    
    public DdlMojoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Creates the Mojo instance.
     */
    @Before
    public void setUp() {
        mojo = new GenerateDdlMojo();
    }

    /**
     * Resets the Mojo by setting {@link #mojo} to {@code null} and deletes the
     * test directory.
     *
     * @throws IOException Thrown if the test directory could not be deleted
     */
    @After
    public void tearDown() throws IOException {
        //Unset Mojo instance
        mojo = null;

        //Delete test directory
        final Path testDir = Paths.get(TEST_DIR);
        if (Files.exists(testDir)) {
            //First get all files in the test directory (if the test directory
            //exists and delete them. This is necessary because there is no
            //method for recursivly deleting a directory in the Java API.
            try (final DirectoryStream<Path> files = Files.newDirectoryStream(
                testDir)) {
                for (final Path file : files) {
                    Files.deleteIfExists(file);
                }
            } catch (DirectoryIteratorException ex) {
                throw ex.getCause();
            }
            //Delete the (now empty) test directory.
            Files.deleteIfExists(testDir);
        }
    }

    /**
     * Check if the DDL files are generated and have the expected content.
     *
     * @throws MojoExecutionException if something wants wrong when executing
     *                                the Mojo.
     * @throws MojoFailureException   if something wants wrong when executing
     *                                the Mojo.
     * @throws IOException            if the test directory can't be opened or
     *                                created.
     */
    @Test
    public void generateDdl() throws MojoExecutionException,
                                     MojoFailureException,
                                     IOException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        mojo.execute();

        for (String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect.toLowerCase(
                                                  Locale.ENGLISH));
            assertTrue(String.format("DDL file '%s' was not generated.", path),
                       fileExists(path));

            assertTrue(String.format(
                "DDL file '%s' does not contain 'create table' statement for "
                    + "persons entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsPersonEntity(path));

            assertTrue(String.format(
                "DDL file '%s' does not contain 'create table' statement for "
                    + "company entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsCompanyEntity(path));

            assertTrue(String.format(
                "DDL file '%s' does not contains 'create table' statement for "
                    + "reports entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsReportEntity(path));
        }
    }

    /**
     * Check if the DDL files are generated and have the expected content, but
     * this time with Envers enabled.
     *
     * @throws MojoExecutionException if something wants wrong when executing
     *                                the Mojo.
     * @throws MojoFailureException   if something wants wrong when executing
     *                                the Mojo.
     * @throws IOException            if the test directory can't be opened or
     *                                created.
     */
    @Test
    public void generateDdl4Envers() throws MojoExecutionException,
                                            MojoFailureException,
                                            IOException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        mojo.setUseEnvers(true);

        mojo.execute();

        for (final String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect
                                              .toLowerCase(Locale.ENGLISH));

            assertTrue(String.format("DDL file '%s' was not generated.", path),
                       fileExists(path));

            assertTrue(String.format(
                "DDL file '%s' does not contain 'create table' statement for"
                    + "persons entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsPersonEntity(path));
            assertTrue(String.format(
                "DDL file '%s' does not contain 'create table' statement for"
                    + "persons entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsCompanyEntity(path));

            assertTrue(String.format(
                "DDL file '%s' does not contains 'create table' statement for "
                    + "reports entity",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsReportEntity(path));
            assertTrue(String.format(
                "DDL file '%s' does not contains 'create table' statement for "
                    + "reports envers table",
                dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsReportsEnversTable(path));
        }
    }

    /**
     * Check if the DDL files are generated and have the expected content, but
     * this time with Envers enabled.
     *
     * @throws NoSuchMethodException     if something went wrong...
     * @throws IllegalAccessException    if something went wrong...
     * @throws InvocationTargetException if something went wrong...
     */
    @Test
    public void generateDdlWithProperties() throws NoSuchMethodException,
                                                   IllegalAccessException,
                                                   IllegalArgumentException,
                                                   InvocationTargetException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        mojo.setPersistenceXml(new File(TEST_PERSISTENCE_XML));
        assertThat(mojo.getPersistenceXml(), is(notNullValue()));

        final Configuration configuration = new Configuration();
        final Method processMethod = mojo.getClass().getDeclaredMethod(
            "processPersistenceXml", Configuration.class);
        processMethod.setAccessible(true);

        processMethod.invoke(mojo, configuration);

        assertThat(configuration.getProperty(
            "org.hibernate.envers.audit_table_suffix"),
                   is(equalTo("_audit")));
        assertThat(configuration.getProperty(
            "org.hibernate.envers.revision_type_field_name"),
                   is(equalTo("rev_type")));

    }

    /**
     * Check if {@link MojoExecutionException} is thrown if an illegal dialect
     * if configured.
     *
     * @throws MojoExecutionException if the Mojo can't be executed.
     * @throws MojoFailureException   if the execution of the Mojo fails
     *                                (expected here)
     */
    @Test(expected = MojoFailureException.class)
    public void illegalDialect() throws MojoExecutionException,
                                        MojoFailureException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };

        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "fooDB"
        };
        mojo.setDialects(dialects);

        mojo.execute();
    }

    @Test
    public void checkOutputDirGetter() {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        final File outputDirectory = mojo.getOutputDirectory();
        final String path = outputDirectory.getAbsolutePath();

        assertTrue(String.format("The path of the output directory is '%s', "
                                     + "but is expected to end with '%s'.",
                                 path,
                                 TEST_DIR),
                   path.endsWith(TEST_DIR));
    }

    @Test
    public void checkPackagesGetter() {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        final String[] retrievedPackages = mojo.getPackages();

        assertTrue(String.format(
            "Expected an array containing two packages names but found an "
                + "array containing %d package names.",
            retrievedPackages.length),
                   retrievedPackages.length == 2);

        assertEquals(retrievedPackages[0],
                     "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities");
        assertEquals(retrievedPackages[1],
                     "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2");
    }

    @Test
    public void checkDialectsGetter() {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        final String[] retrievedDialects = mojo.getDialects();

        assertTrue(String.format(
            "Expected an array containing three dialects but found an array "
                + "containing %d dialects.",
            retrievedDialects.length),
                   retrievedDialects.length == 3);

        assertEquals(retrievedDialects[0], "hsql");
        assertEquals(retrievedDialects[1], "mysql5");
        assertEquals(retrievedDialects[2], "POSTGRESQL9");

    }

    private boolean fileExists(final String path) {
        final File file = new File(path);
        return file.exists();
    }

    private boolean fileContainsPersonEntity(final String path) throws
        IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH).contains("create table persons");
    }

    private boolean fileContainsReportEntity(final String path) throws
        IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH).contains("create table reports");
    }

    private boolean fileContainsReportsEnversTable(final String path) throws
        IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH).contains(
            "create table reports_revisions");
    }

    private boolean fileContainsCompanyEntity(final String path) throws
        IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH)
            .contains("create table companies");
    }

    /**
     * Checks if the DDL are only overwritten if their content has changed.
     *
     * @throws MojoExecutionException if anything wents wrong when executing the
     *                                Mojo.
     * @throws MojoFailureException   if anything wents wrong when executing the
     *                                Mojo.
     * @throws InterruptedException   If the test thread could not be
     *                                interrupted.
     */
    @Test
    public void noOverwriteIfFileDidNotChange() throws MojoExecutionException,
                                                       MojoFailureException,
                                                       InterruptedException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        mojo.execute();

        final Map<String, Long> timestamps = new HashMap<>();

        for (final String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect.toLowerCase(
                                                  Locale.ENGLISH));
            final File file = new File(path);
            timestamps.put(dialect, file.lastModified());
        }

        Thread.sleep(2500);
        mojo.execute();

        for (final String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect.toLowerCase(
                                                  Locale.ENGLISH));
            final File file = new File(path);

            assertThat(
                "DDL File was overwritten despite its content of the file"
                    + " hasn't changed.",
                file.lastModified(),
                is(timestamps.get(dialect)));
        }
    }

    /**
     * Checks if the DDL files are overwritten when the schema changes.
     *
     * @throws MojoExecutionException if anything wents wrong when executing the
     *                                Mojo.
     * @throws MojoFailureException   if anything wents wrong when executing the
     *                                Mojo.
     * @throws InterruptedException   If the test thread could not be
     *                                interrupted.
     */
    @Test
    public void overwriteFileIfContentChanges() throws MojoExecutionException,
                                                       MojoFailureException,
                                                       InterruptedException {
        mojo.setOutputDirectory(new File(TEST_DIR));

        final String[] packages = new String[]{
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
        };
        mojo.setPackages(packages);

        final String[] dialects = new String[]{
            "hsql",
            "mysql5",
            "POSTGRESQL9"
        };
        mojo.setDialects(dialects);

        mojo.setUseEnvers(false);
        
        mojo.execute();

        final Map<String, Long> timestamps = new HashMap<>();

        for (final String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect.toLowerCase(
                                                  Locale.ENGLISH));
            final File file = new File(path);
            timestamps.put(dialect, file.lastModified());
        }

//        final String[] packages2 = new String[]{
//            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities",
//            "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2"
//        };
//        mojo.setPackages(packages2);
        
        mojo.setUseEnvers(true);

        Thread.sleep(2500);
        mojo.execute();

        for (final String dialect : dialects) {
            final String path = String.format("%s/%s.sql",
                                              TEST_DIR,
                                              dialect.toLowerCase(
                                                  Locale.ENGLISH));
            final File file = new File(path);

            assertThat("DDL File was not overwritten despite its content has "
                           + "changed.",
                       file.lastModified(),
                       greaterThan(timestamps.get(dialect)));
        }
    }

}
