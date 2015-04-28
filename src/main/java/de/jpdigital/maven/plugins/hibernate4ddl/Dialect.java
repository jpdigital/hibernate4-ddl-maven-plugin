/*
 * Copyright (C) 2014 Jens Pelzetter <jens@jp-digital.de>
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
package de.jpdigital.maven.plugins.hibernate4ddl;

/**
 * This enumeration provides constants for all dialects supported by Hibernate.
 *
 * The dialects supported by Hibernate can be found in the 
 * <a href="http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#configuration-optional-dialects">
 * Hibernate documentation</a>.
 * Also this enumeration provides the convenient method
 * {@link #getDialectClass()} for getting the classname of the Hibernate
 * dialect.
 *
 * @author <a href="mailto:jens@jp-digital.de">Jens Pelzetter</a>
 */
public enum Dialect {

    CUBRID("org.hibernate.dialect.CUBRIDDialect"),
    DB2("org.hibernate.dialect.DB2Dialect"),
    DB2_AS400("org.hibernate.dialect.DB2400Dialect"),
    DB2_OS390("org.hibernate.dialect.DB2390Dialect"),
    FIREBIRD("org.hibernate.dialect.FirebirdDialect"),
    FRONTBASE("org.hibernate.dialect.FrontBaseDialect"),
    H2("org.hibernate.dialect.H2Dialect"),
    HSQL("org.hibernate.dialect.HSQLDialect"),
    INFORMIX("org.hibernate.dialect.InformixDialect"),
    INGRES("org.hibernate.dialect.IngresDialect"),
    INGRES9("org.hibernate.dialect.Ingres9Dialect"),
    INGRES10("org.hibernate.dialect.Ingres10Dialect"),
    INTERBASE("org.hibernate.dialect.InterbaseDialect"),
    INTERSYSTEMS_CACHE("org.hibernate.dialect.Cache71Dialect"),
    JDATASTORE("org.hibernate.dialect.JDataStoreDialect"),
    MCKOISQL("org.hibernate.dialect.MckoiDialect"),
    SQLSERVER2000("org.hibernate.dialect.SQLServerDialect"),
    SQLSERVER2005("org.hibernate.dialect.SQLServer2005Dialect"),
    SQLSERVER2008("org.hibernate.dialect.SQLServer2008Dialect"),
    SQLSERVER2012("org.hibernate.dialect.SQLServer2012Dialect"),
    MIMERSQL("org.hibernate.dialect.MimerSQLDialect"),
    MYSQL("org.hibernate.dialect.MySQLDialect"),
    MYSQL_INNODB("org.hibernate.dialect.MySQLInnoDBDialect"),
    MYSQL_MYISAM("org.hibernate.dialect.MySQLMyISAMDialect"),
    MYSQL5("org.hibernate.dialect.MySQL5Dialect"),
    MYSQL5_INNODB("org.hibernate.dialect.MySQL5InnoDBDialect"),
    ORACLE8I("org.hibernate.dialect.Oracle8iDialect"),
    ORACLE9I("org.hibernate.dialect.Oracle9iDialect"),
    ORACLE10G("org.hibernate.dialect.Oracle10gDialect"),
    ORACLE_TIMES_TEN("org.hibernate.dialect.TimesTenDialect"),
    POINTBASE("org.hibernate.dialect.PointbaseDialect"),
    POSTGRESQL81("org.hibernate.dialect.PostgreSQL81Dialect"),
    POSTGRESQL82("org.hibernate.dialect.PostgreSQL82Dialect"),
    POSTGRESQL9("org.hibernate.dialect.PostgreSQL9Dialect"),
    PROGRESS("org.hibernate.dialect.ProgressDialect"),
    SAP_DB("org.hibernate.dialect.SAPDBDialect"),
    SAP_HANA_COL("org.hibernate.dialect.HANAColumnStoreDialect"),
    SAP_HANA_ROW("org.hibernate.dialect.HANARowStoreDialect"),
    SYBASE("org.hibernate.dialect.SybaseDialect"),
    SYBASE11("org.hibernate.dialect.Sybase11Dialect"),
    SYBASE_ASE155("org.hibernate.dialect.SybaseASE15Dialect"),
    SYBASE_ASE157("org.hibernate.dialect.SybaseASE157Dialect"),
    SYBASE_ANYWHERE("org.hibernate.dialect.SybaseAnywhereDialect"),
    TERADATA("org.hibernate.dialect.TeradataDialect"),
    UNISYS_OS_2200_RDMS("org.hibernate.dialect.RDMSOS2200Dialect");

    /**
     * Property for holding the name of the Hibernate dialect class.
     */
    private final String dialectClass;

    /**
     * Private constructor, used to create the Enum instances for each dialect.
     *
     * @param dialectClass The dialect class for the specific dialect.
     */
    private Dialect(final String dialectClass) {
        this.dialectClass = dialectClass;
    }

    /**
     * Getter for the dialect class.
     *
     * @return The name of the dialect class, for example
     *         {@code org.hibernate.dialect.PostgreSQL9Dialect} for
     *         {@link #POSTGRESQL9}.
     */
    public String getDialectClass() {
        return dialectClass;
    }

}
