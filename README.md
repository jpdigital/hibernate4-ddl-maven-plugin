hibernate4-ddl-maven-plugin
===========================

The hibernate4-ddl-maven-plugin is a simple Maven plugin for creating SQL DDL
files for JPA entities. The plugin uses Hibernates API for SchemaExport. To use the plugin, you have 
to add my Maven repository to your POM or your settings:

    ...
    <pluginRepositories>
    	...
	<pluginRepository>
	    <id>jp-digital.de</id>
	    <name>jp-digital.de</name>
	    <url>http://archiva.jp-digital.de</url>
	    <releases>
	    	<enabled>true</enabled>		
	    </releases>
	    <snapshots>
	    	<enabled>false</enabled>
	    </snapshots>
	</pluginRepository>
	...
    </pluginRepositories>
    ...

The code is available at [GitHub](http://github.com/jpdigital/hibernate4-ddl-maven-plugin) at 
<http://github.com/jpdigital/hibernate4-ddl-maven-plugin>. The 
[projects web site](http://jpdigital.github.com/hibernate4-maven-plugin) is also available on GitHub 
at <http://jpdigital.github.com/hibernate4-maven-plugin>.




