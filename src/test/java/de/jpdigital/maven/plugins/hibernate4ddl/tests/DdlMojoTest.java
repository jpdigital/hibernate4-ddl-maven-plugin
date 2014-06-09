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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class DdlMojoTest {

    private static final String TEST_DIR = "target/test/ddl/test";
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

    @Before
    public void setUp() {
        mojo = new GenerateDdlMojo();
    }

    @After
    public void tearDown() {
        mojo = null;
    }

    @Test
    public void generateDdl() throws MojoExecutionException, MojoFailureException, IOException {
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
                                              dialect.toLowerCase(Locale.ENGLISH));
            assertTrue(String.format("DDL file '%s' was not generated.", path), fileExists(path));

            assertTrue(String.format("DDL file '%s' does not contain 'create table' statement for"
                                         + "persons entity",
                                     dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsPersonEntity(path));
            assertTrue(String.format("DDL file '%s' does not contain 'create table' statement for"
                                         + "persons entity",
                                     dialect.toLowerCase(Locale.ENGLISH)),
                       fileContainsCompanyEntity(path));
        }

    }

    @Test(expected = MojoFailureException.class)
    public void illegalDialect() throws MojoExecutionException, MojoFailureException {
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

        assertTrue(String.format("Expected an array containing two packages names but found an "
                                     + "array containing %d package names.",
                                 retrievedPackages.length),
                   retrievedPackages.length == 2);

        assertEquals(retrievedPackages[0], "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities");
        assertEquals(retrievedPackages[1], "de.jpdigital.maven.plugins.hibernate4ddl.tests.entities2");
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

        assertTrue(String.format("Expected an array containing three dialects but found an array "
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

    private boolean fileContainsPersonEntity(final String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH).contains("create table persons");
    }

    private boolean fileContainsCompanyEntity(final String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        final String sql = new String(encoded, StandardCharsets.UTF_8);

        return sql.toLowerCase(Locale.ENGLISH).contains("create table companies");
    }
    
   

}
