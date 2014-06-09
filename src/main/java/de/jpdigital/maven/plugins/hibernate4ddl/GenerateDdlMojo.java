package de.jpdigital.maven.plugins.hibernate4ddl;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import org.apache.maven.plugin.MojoFailureException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

/**
 * Goal which creates DDL SQL files for the JPA entities in the project (using the Hibernate 4
 * SchemaExport class}.
 */
@Mojo(name = "gen-ddl", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenerateDdlMojo extends AbstractMojo {

    /**
     * Location of the output file.
     */
    @Parameter(defaultValue = "src/main/resources/sql/ddl/", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Packages containing the entity files for which the SQL DDL scripts shall be generated.
     */
    @Parameter(required = true)
    private String[] packages;

    /**
     * Database dialects for which create scripts shall be generated. For available dialects refer
     * to the documentation the {@link Dialect} enumeration.
     */
    @Parameter(required = true)
    private String[] dialects;

    /**
     * The Mojos execute method.
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File outputDir = outputDirectory;

        getLog().info(String.format("Generating DDL SQL files in %s.",
                                    outputDir.getAbsolutePath()));

        //Check if the output directory exists.
        if (!outputDir.exists()) {
            final boolean result = outputDir.mkdirs();
            if (!result) {
                throw new MojoFailureException(
                    "Failed to create output directory for SQL DDL files.");
            }
        }

        //Read the dialects from the parameter and convert them to instances of the dialect enum.
        final Set<Dialect> dialectsList = new HashSet<>();
        for (final String dialect : dialects) {
            convertDialect(dialect, dialectsList);
        }

        //Find the entity classes in the packages.
        final Set<Class<?>> entityClasses = new HashSet<>();
        for (final String packageName : packages) {
            findEntitiesForPackage(packageName, entityClasses);
        }

        //Generate the SQL scripts
        for (final Dialect dialect : dialectsList) {
            generateDdl(dialect, entityClasses);
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String[] getPackages() {
        return Arrays.copyOf(packages, packages.length);
    }

    public void setPackages(final String[] packages) {
        this.packages = Arrays.copyOf(packages, packages.length);
    }

    public String[] getDialects() {
        return Arrays.copyOf(dialects, dialects.length);
    }

    public void setDialects(final String[] dialects) {
        this.dialects = Arrays.copyOf(dialects, dialects.length);
    }

    /**
     * Helper method for converting the dialects from {@code String} to instances of the
     * {@link Dialect} enumeration.
     *
     * @param dialect      The dialect to convert.
     * @param dialectsList The lists of dialects where the converted dialect is stored.
     *
     * @throws MojoFailureException If the dialect string could not be converted, for example if it
     *                              is misspelled. This will cause a {@code Build Failure}
     */
    private void convertDialect(final String dialect, final Set<Dialect> dialectsList)
        throws MojoFailureException {

        try {
            dialectsList.add(Dialect.valueOf(dialect.toUpperCase(Locale.ENGLISH)));
        } catch (IllegalArgumentException ex) {
            final StringBuffer buffer = new StringBuffer();
            for (final Dialect avilable : Dialect.values()) {
                buffer.append(avilable.toString()).append('\n');
            }

            throw new MojoFailureException(
                String.format("Can't convert the configured dialect '%s' to a dialect classname. "
                                  + "Available dialects are:%n"
                                  + "%s",
                              dialect,
                              buffer.toString()),
                ex);
        }
    }

    /**
     * Helper method for finding a entity classes in a package. The entity classes must be annotated
     * with the {@link Entity} annotation. The method uses the Reflections library for finding the
     * entity classes.
     *
     * @param packageName
     * @param entityClasses
     */
    private void findEntitiesForPackage(final String packageName,
                                        final Set<Class<?>> entityClasses) {
        final Reflections reflections = new Reflections(ClasspathHelper.forPackage(packageName));

        final Set<Class<?>> classesWithEntity = reflections.getTypesAnnotatedWith(Entity.class);
        for (final Class<?> entityClass : classesWithEntity) {
            entityClasses.add(entityClass);
        }

        final Set<Class<?>> embeddedables = reflections.getTypesAnnotatedWith(Embeddable.class);
        for (final Class<?> entityClass : embeddedables) {
            entityClasses.add(entityClass);
        }
    }

    /**
     * Helper method for generating the DDL classes for a specific dialect. This is place for the
     * real work is done. The method first creates an instance of the {@link Configuration} class
     * from Hibernate an puts the appropriate values into it. It then creates an instance of the
     * {@link SchemaExport} class from the Hibernate API, configured this class, for example by
     * setting {@code format} to {@code true} so that the generated SQL files are formatted nicely.
     * After that it calls the {@link SchemaExport#execute(boolean, boolean, boolean, boolean)}
     * method which will create the SQL script file. The method is called in a way which requires no
     * database connection.
     *
     *
     * @param dialect
     * @param entityClasses
     */
    private void generateDdl(final Dialect dialect, final Set<Class<?>> entityClasses) {
        final Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        for (final Class<?> entityClass : entityClasses) {
            configuration.addAnnotatedClass(entityClass);
        }

        configuration.setProperty("hibernate.dialect", dialect.getDialectClass());

        final SchemaExport export = new SchemaExport(configuration);
        export.setDelimiter(";");

        final String dirPath;
        if (outputDirectory.getAbsolutePath().endsWith("/")) {
            dirPath = outputDirectory.getAbsolutePath().substring(
                0, outputDirectory.getAbsolutePath().length());
        } else {
            dirPath = outputDirectory.getAbsolutePath();
        }

        export.setOutputFile(String.format("%s/%s.sql",
                                           dirPath,
                                           dialect.name().toLowerCase(Locale.ENGLISH)));
        export.setFormat(true);
        export.execute(true, false, false, false);
    }

}
