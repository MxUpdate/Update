/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
 */

package org.mxupdate.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.maven.settings.DefaultMavenSettingsBuilder;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 * Abstract test class to connect to MX and disconnect incl. some helper
 * methods to execute MQL commands and JPOs.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractTest
{
    /**
     * Prefix for all new created test objects. The prefix should be used from
     * all test cases.
     */
    public static final String PREFIX = "MXUPDATE_";

    /**
     * Name of the property used for the URL to the MX instance.
     */
    private static final String PROP_URL = "org.mxupdate.mx.url";

    /**
     * Name of the property used for the user to the MX instance.
     */
    private static final String PROP_USER = "org.mxupdate.mx.user";

    /**
     * Name of the property used for the password to the MX instance.
     */
    private static final String PROP_PASSWORD = "org.mxupdate.mx.password";

    /**
     * Enumeration to define a mapping between the configuration items and the
     * related administration types in MX and MxUpdate Update.
     */
    public enum CI
    {
// CHECKSTYLE:OFF
        DM_ATTRIBUTE_BINARY(    "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeBinary",          "datamodel/attribute",              "BINARY_",                  ".mxu", true),
        DM_ATTRIBUTE_BOOLEAN(   "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeBoolean",         "datamodel/attribute",              "BOOLEAN_",                 ".mxu", true),
        DM_ATTRIBUTE_DATE(      "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeDate",            "datamodel/attribute",              "DATE_",                    ".mxu", true),
        DM_ATTRIBUTE_INTEGER(   "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeInteger",         "datamodel/attribute",              "INTEGER_",                 ".mxu", true),
        DM_ATTRIBUTE_REAL(      "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeReal",            "datamodel/attribute",              "REAL_",                    ".mxu", true),
        DM_ATTRIBUTE_STRING(    "attribute",                "attribute",        null,                                   false, null,                                null,                       "AttributeString",          "datamodel/attribute",              "STRING_",                  ".mxu", true),
        DM_DIMENSION(           "dimension",                "dimension",        null,                                   false, null,                                null,                       "Dimension",                "datamodel/dimension",              "DIMENSION_",               ".mxu", true),
        DM_EXPRESSION(          "expression",               "expression",       null,                                   false, null,                                null,                       "Expression",               "datamodel/expression",             "EXPRESSION_",              ".mxu", true),
        DM_FORMAT(              "format",                   "format",           null,                                   false, null,                                null,                       "Format",                   "datamodel/format",                 "FORMAT_",                  ".mxu", true),
        DM_NUMBERGENERATOR(     "numbergenerator",          null,               "eService Number Generator",            false, "eService Object Generator",         "eService Administration",  "NumberGenerator",          "datamodel/numbergenerator",        "NUMBERGENERATOR_",         ".mxu", true),
        DM_OBJECTGENERATOR(     "objectgenerator",          null,               "eService Object Generator",            false, "eService Object Generator",         "eService Administration",  "ObjectGenerator",          "datamodel/objectgenerator",        "OBJECTGENERATOR_",         ".mxu", true),
        DM_INTERFACE(           "interface",                "interface",        null,                                   false, null,                                null,                       "Interface",                "datamodel/interface",              "INTERFACE_",               ".mxu", true),
        DM_NOTIFICATION(        "notification",             null,               "Notification",                         false, "Business Rule",                     "eService Administration",  "Notification",             "datamodel/notification",           "NOTIFICATION_",            ".mxu", true),
        DM_PATHTYPE(            "pathtype",                 "pathtype",         null,                                   false, null,                                null,                       "DMPathType",               "datamodel/pathtype",               "PATHTYPE_",                ".mxu", true),
        DM_POLICY(              "policy",                   "policy",           null,                                   false, null,                                null,                       "Policy",                   "datamodel/policy",                 "POLICY_",                  ".mxu", true),
        DM_RELATIONSHIP(        "relationship",             "relationship",     null,                                   false, null,                                null,                       "Relationship",             "datamodel/relationship",           "RELATIONSHIP_",            ".mxu", true),
        DM_RULE(                "rule",                     "rule",             null,                                   false, null,                                null,                       "Rule",                     "datamodel/rule",                   "RULE_",                    ".mxu", true),
        DM_TRIGGER(             "trigger",                  null,               "eService Trigger Program Parameters",  false, "eService Trigger Program Policy",   "eService Administration",  "Trigger",                  "datamodel/trigger",                "TRIGGER_",                 ".mxu", true),
        DM_TYPE(                "type",                     "type",             null,                                   false, null,                                null,                       "Type",                     "datamodel/type",                   "TYPE_",                    ".mxu", true),

        IEF_EBOMSYNC_CONFIG(    "ebomsyncconfig",           null,               "IEF-EBOMSyncConfig",                   true,  "IEF-EBOMSyncConfig",                "eService Administration",  "IEFEBOMSyncConfig",        "integration/ebomsync",             "IEFEBOMSYNC_",             ".mxu", true),
        IEF_GLOBAL_CONFIG(      "globalconfig",             null,               "MCADInteg-GlobalConfig",               true,  "MCADInteg-ConfigObjectPolicy",      "eService Administration",  "IEFGlobalConfig",          "integration/globalconfig",         "IEFGLOBALCONFIG_",         ".mxu", true),
        IEF_GLOBAL_REGISTRY(    "globalregistry",           null,               "IEF-GlobalRegistry",                   false, "MCADInteg-ConfigObjectPolicy",      "eService Administration",  "IEFGlobalRegistry",        "integration/globalregistry",       "IEFGLOBALREGISTRY_",       ".mxu", true),
        IEF_MASS_PROMOTE_CONFIG("masspromoteconfig",        null,               "IEF-MassPromoteConfig",                true,  "MCADInteg-ConfigObjectPolicy",      "eService Administration",  "IEFMassPromoteConfig",     "integration/masspromoteconfig",    "IEFMASSPROMOTECONFIG_",    ".mxu", true),
        IEF_UNASSIGNED_REGISTRY("unassignedregistry",       null,               "IEF-UnassignedIntegRegistry",          true,  "MCADInteg-ConfigObjectPolicy",      "eService Administration",  "IEFUnassignedRegistry",    "integration/unassignedregistry",   "IEFUNASSIGNEDREGISTRY_",   ".mxu", true),

        PRG_EKL(                "program",                  "program",          null,                                   false, null,                                null,                       "ProgramEKL",               "program/ekl",                      "",                         ".ekl.mxu",         true),
        PRG_JPO(                "program",                  "program",          null,                                   false, null,                                null,                       "JPO",                      "program/jpo",                      "",                         ".java.mxu",        true),
        PRG_MQL(                "program",                  "program",          null,                                   false, null,                                null,                       "Program",                  "program/mql",                      "",                         ".mql.mxu",         true),
        PRG_EXTERNAL(           "program",                  "program",          null,                                   false, null,                                null,                       "Program",                  "program/mql",                      "",                         ".external.mxu",    true),
        PRG_PAGE(               "page",                     "page",             null,                                   false, null,                                null,                       "Page",                     "program/page",                     "PAGE_",                    ".mxu",             true),

        USR_ASSOCIATION(        "association",              "association",      null,                                   false, null,                                null,                       "Association",              "user/association",                 "ASSOCIATION_",             ".mxu", false),
        USR_GROUP(              "group",                    "group",            null,                                   false, null,                                null,                       "Group",                    "user/group",                       "GROUP_",                   ".mxu", true),
        USR_PERSONADMIN(        "person",                   "person",           null,                                   false, null,                                null,                       "PersonAdmin",              "user/personadmin",                 "PERSONADMIN_",             ".mxu", true),
        USR_ROLE(               "role",                     "role",             null,                                   false, null,                                null,                      "Role",                      "user/role",                        "ROLE_",                    ".mxu", true),

        OTHER_BUSINESSUNIT(     null,                       null,               "Business Unit",                        false, "Organization",                      "eService Production",      "Business Unit", null, null, null, true),
        OTHER_COMPANY(          null,                       null,               "Company",                              false, "Organization",                      "eService Production",      "Company", null, null, null, true),
        OTHER_DEPARTMENT(       null,                       null,               "Department",                           false, "Organization",                      "eService Production",      "Department", null, null, null, true),
        OTHER_PLANT(            null,                       null,               "Plant",                                false, "Organization",                      "eService Production",      "Plant", null, null, null, true),

        SYS_APPLICATION(        "application",              "application",      null,                                   false, null,                                null,                       "SystemApplication",        "system/application",               "APPLICATION_",             ".mxu", true),
        SYS_PACKAGE(            "package",                  "package",          null,                                   false, null,                                null,                       "SystemPackage",            "system/package",                   "PACKAGE_",                 ".mxu", true),
        SYS_SITE(               null,                       "site",             null,                                   false, null,                                null,                       "SystemSite",               "system/site",                      "SITE_",                    ".mxu", false),

        UI_CHANNEL(             "channel",                  "channel",          null,                                   false, null,                                null,                       "Channel",                  "userinterface/channel",            "CHANNEL_",                 ".mxu", true),
        UI_COMMAND(             "command",                  "command",          null,                                   false, null,                                null,                       "Command",                  "userinterface/command",            "COMMAND_",                 ".mxu", true),
        UI_FORM(                "form",                     "form",             null,                                   false, null,                                null,                       "Form",                     "userinterface/form",               "FORM_",                    ".mxu", true),
        UI_INQUIRY(             "inquiry",                  "inquiry",          null,                                   false, null,                                null,                       "Inquiry",                  "userinterface/inquiry",            "INQUIRY_",                 ".mxu", true),
        UI_MENU(                "menu",                     "menu",             null,                                   false, null,                                null,                       "Menu",                     "userinterface/menu",               "MENU_",                    ".mxu", true),
        UI_PORTAL(              "portal",                   "portal",           null,                                   false, null,                                null,                       "Portal",                   "userinterface/portal",             "PORTAL_",                  ".mxu", true),
        UI_TABLE(               "table",                    "table",            null,                                   false, null,                                null,                       "Table",                    "userinterface/table",              "TABLE_",                   ".mxu", false);
// CHECKSTYLE:ON
        /** MxUpdate type. */
        public final String mxUpdateType;

        /** Related admin type name in MX. */
        final String mxType;

        /** Related type of the business object. */
        private final String busType;
        /** Are business objects derived from {@link #busType type}? */
        private final boolean hasDerived;
        /** Related policy of the business object. */
        private final String busPolicy;
        /** Related vault of the business object. */
        private final String busVault;

        /** Related type name in MxUpdate Update. */
        public final String updateType;

        /** Path where the configuration item update file is located. */
        public final String filePath;
        /** Prefix used for the file name. */
        public final String filePrefix;
        /** Prefix used for the file name. */
        public final String fileSuffix;

        /** Does the wild card search for the type works? */
        public final boolean wildcardSearch;

        /**
         * Constructor to initialize an enumeration instance.
         *
         * @param _mxType               related type name in MX
         * @param _busType              related type of the business object
         * @param _hasDerived           does the configuration item works with
         *                              derived types?
         * @param _busPolicy            related policy of the business object
         * @param _busVault             related vault of the business object
         * @param _updateType           related type name in MxUpdate Update
         * @param _filePath             file path
         * @param _filePrefix           prefix used for the file
         * @param _fileSuffix           suffix used for the file
         * @param _wildcardSearch       wild card search allowed?
         * @param _urlTag               url tag
         */
        CI(final String _mxUpdateType,
           final String _mxType,
           final String _busType,
           final boolean _hasDerived,
           final String _busPolicy,
           final String _busVault,
           final String _updateType,
           final String _filePath,
           final String _filePrefix,
           final String _fileSuffix,
           final boolean _wildcardSearch)
        {
            this.mxUpdateType = _mxUpdateType;
            this.mxType = _mxType;
            this.busType = _busType;
            this.hasDerived = _hasDerived;
            this.busPolicy = _busPolicy;
            this.busVault = _busVault;
            this.updateType = _updateType;
            this.filePath = _filePath;
            this.filePrefix = _filePrefix;
            this.fileSuffix = _fileSuffix;
            this.wildcardSearch = _wildcardSearch;
        }

        /**
         * Returns {@link #mxType MX type} of this configuration.
         *
         * @return MX type of this configuration item
         */
        public String getMxType()
        {
            return this.mxType;
        }

        /**
         * Returns the {@link #busType type} of the business object.
         *
         * @return type of the business object
         */
        public String getBusType()
        {
            return this.busType;
        }

        /**
         * Checks if business types could be {@link #hasDerived derived}.
         *
         * @return <i>true</i> if business type could be derived; otherwise
         *         <i>false</i>
         */
        public boolean hasBusTypeDerived()
        {
            return this.hasDerived;
        }

        /**
         * Returns the {@link #busPolicy policy} of the business object.
         *
         * @return policy of the business object
         */
        public String getBusPolicy()
        {
            return this.busPolicy;
        }

        /**
         * Returns the {@link #busVault vault} of the business object.
         *
         * @return vault of the business object
         */
        public String getBusVault()
        {
            return this.busVault;
        }
    }

    /** MX context. */
    private Context context;

    /** Settings for maven. */
    private Settings settings;

    /** Current MX version. */
    private Version version;

    /**
     * Returns depending on the <code>_key</code> related value within the
     * properties.
     *
     * @param _key      searched key within property settings
     * @return value of the property
     * @throws Exception if setting could not be fetched
     * @see #settings
     */
    private String getSetting(final String _key)
        throws Exception
    {
        if (this.settings == null)  {
            final DefaultMavenSettingsBuilder builder = new DefaultMavenSettingsBuilder();
            final File settingsFile = new File(new File(System.getProperty("user.home")), ".m2/settings.xml");
            this.settings = builder.buildSettings(settingsFile);
        }
        String tmp = null;
        for (final String active : this.settings.getActiveProfiles())  {
            tmp = ((Profile) this.settings.getProfilesAsMap().get(active)).getProperties().getProperty(_key);
            if (tmp != null)  {
                break;
            }
        }
        final String ret;
        if (tmp != null)  {
            ret = tmp;
        } else  {
            ret = "";
        }
        return ret;
    }

    /**
     * Returns the {@link #context} connection to MX.
     *
     * @return MX context
     */
    public Context getContext()
    {
        return this.context;
    }

    /**
     * Returns the {@link #version} of used MX.
     *
     * @return MX version
     */
    public Version getVersion()
    {
        return this.version;
    }

    /**
     * Connects to MX.
     *
     * @param _url      URL for the MX connection
     * @param _user     user for the MX connection
     * @param _password password for the MX connection
     * @throws Exception if connect failed
     */
    @BeforeClass()
    @Parameters({AbstractTest.PROP_URL, AbstractTest.PROP_USER, AbstractTest.PROP_PASSWORD})
    public void connect(@Optional() final String _url,
                        @Optional() final String _user,
                        @Optional() final String _password)
        throws Exception
    {
        final String url        = (_url != null)        ? _url      : this.getSetting(AbstractTest.PROP_URL);
        final String user       = (_user != null)       ? _user     : this.getSetting(AbstractTest.PROP_USER);
        final String password   = (_password != null)   ? _password : this.getSetting(AbstractTest.PROP_PASSWORD);

        // work-around that all SSL certificates are allowed (needed for self-signed SSL certificates!)
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null,
                new TrustManager[] {
                    new X509TrustManager() {
                       @Override public X509Certificate[] getAcceptedIssuers() {return null;}
                       @Override public void checkClientTrusted(final X509Certificate[] certs, final String authType) {}
                       @Override public void checkServerTrusted(final X509Certificate[] certs, final String authType) {}
                    }
                },
                new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        this.context = new Context(url);
        this.context.resetContext(user, password, null);
        this.context.connect();

        this.version = Version.valueOf(this.mql("version").replaceAll("\\..*", "").replaceAll("^3DEXPERIENCE ", "").replaceAll("^R", "V6R"));
    }

    /**
     * Disconnects from MX.
     *
     * @throws Exception if disconnect failed
     */
    @AfterClass(dependsOnGroups = "close")
    public void close()
        throws Exception
    {
        this.context.shutdown();
        this.context = null;
    }

    /**
     * Returns the root project directory.
     *
     * @return root project directory
     */
    public String getProjectDir()
    {
        return System.getProperty("user.dir");
    }

    /**
     * Returns the resources directory.
     *
     * @return resources directory
     */
    public String getResourcesDir()
    {
        return this.getProjectDir() + "/src/test/resources";
    }

    /**
     * Returns the target directory.
     *
     * @return target directory
     */
    public String getTargetDir()
    {
        return this.getProjectDir() + "/target/test";
    }

    /**
     * Executes given MQL command statement <code>_cmd</code> and returns the
     * result.
     *
     * @param _cmd  MQL command statement to execute
     * @return returned string value from the called MQL command
     *         <code>_cmd</code>
     * @throws MatrixException if MQL execution failed
     */
    public String mql(final CharSequence _cmd)
        throws MatrixException
    {
        return this.mqlWOTrim(_cmd).trim();
    }

    /**
     * Executes given MQL command statement <code>_cmd</code> and returns the
     * result.
     *
     * @param _cmd  MQL command statement to execute
     * @return returned string value from the called MQL command
     *         <code>_cmd</code>
     * @throws MatrixException if MQL execution failed
     */
    public String mqlWOTrim(final CharSequence _cmd)
        throws MatrixException
    {
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.context, _cmd.toString());
        if ((mql.getError() != null) && !mql.getError().isEmpty())  {
            throw new MatrixException(mql.getError() + "\nMQL command was:\n" + _cmd);
        }
        return mql.getResult();
    }

    /**
     * Executes given MQL command and splits the returned value by new line
     * '\n' and returns this values as a set.
     *
     * @param _cmd                  MQL command to execute
     * @return set of found values
     * @throws MatrixException if MQL execution failed
     */
    public Set<String> mqlAsSet(final CharSequence _cmd)
        throws MatrixException
    {
        final String bck = this.mql(_cmd);
        final Set<String> ret;
        if ("".equals(bck))  {
            ret = new HashSet<String>(0);
        } else  {
            ret = new HashSet<String>(Arrays.asList(bck.split("\n")));
        }
        return ret;
    }

    /**
     * Export given configuration item <code>_type</code> with
     * <code>_name</code>. The returned values from the export are checked for:
     * <ul>
     * <li>value is returned</li>
     * <li>the returned export value includes exact one export for given
     *     <code>_type</code></li>
     * <li>the name of the exported object is equal <code>_name</code></li>
     * </ul>
     *
     * @param _type     type to export
     * @param _name     name to export
     * @return map with the exported object
     * @throws Exception  if MQL calls failed
     */
    protected Export export(final CI _type,
                            final String _name)
        throws Exception
    {
        final Map<?,?> bck = this.executeEncoded("Export",
                                                 null,
                                                 "TypeDef", _type.updateType,
                                                 "Name", _name);
        // extract values
        final Map<?,?> values = (Map<?,?>) bck.get("values");

        Assert.assertNotNull(bck);
        Assert.assertEquals((String) values.get("TypeDef"),
                            _type.updateType,
                            "check correct type definition");

        final Export ret =  new Export(values);

        Assert.assertEquals(ret.getName(), _name, "returned name is equal to given name");

        return ret;
    }

    /**
     * Cleanups given configuration <code>_type</code>. This means that all
     * existing administration object which starts with {@link #PREFIX} are
     * deleted. Only if {@link CI#wildcardSearch} is <i>true</i>, a search via
     * wild card is done; otherwise a list of all administration object is
     * used.
     *
     * @param _type     type of configuration item
     * @throws MatrixException if cleanup for given configuration item
     *                         <code>_type</code> failed
     */
    protected void cleanup(final CI _type)
        throws MatrixException
    {
        // administration objects
        if (_type.getMxType() != null)  {
            final Set<String> elements;
            if (_type == AbstractTest.CI.UI_TABLE)  {
                elements = this.mqlAsSet("escape list table system");
                for (final String element : elements)  {
                    if (element.startsWith(AbstractTest.PREFIX))  {
                        this.mql("escape delete " + _type.mxType + " \"" + AbstractTest.convertMql(element) + "\" system");
                    }
                }
            } else  {
                if (_type.wildcardSearch)  {
                    elements = this.mqlAsSet("escape list " + _type.mxType + " \"" + AbstractTest.PREFIX + "*\"");
                } else  {
                    elements = this.mqlAsSet("escape list " + _type.mxType);
                }
                if (_type == AbstractTest.CI.DM_INTERFACE)  {
                    for (final String element : elements)  {
                        this.mql("escape mod " + _type.mxType + " \"" + AbstractTest.convertMql(element) + "\" remove derived");
                    }
                }
                if ((_type == AbstractTest.CI.DM_RELATIONSHIP) && (this.getVersion().min(Version.V6R2013x)))  {
                    for (final String element : elements)  {
                        if ("TRUE".equals(this.mql("escape print relation \"" + AbstractTest.convertMql(element) + "\" select compositional dump")))  {
                            this.mql("escape mod relation \"" + AbstractTest.convertMql(element) + "\" remove derived from cardinality 1 notpropagateconnection to clone replicate revision replicate notpropagateconnection");
                        } else  {
                            this.mql("escape mod relation \"" + AbstractTest.convertMql(element) + "\" remove derived");
                        }
                    }
                }
                if (_type == AbstractTest.CI.DM_TYPE)  {
                    for (final String element : elements)  {
                        this.mql("escape mod " + _type.mxType + " \"" + AbstractTest.convertMql(element) + "\" remove derived");
                    }
                }
                for (final String element : elements)  {
                    if (element.startsWith(AbstractTest.PREFIX))  {
                        this.mql("escape delete " + _type.mxType + " \"" + AbstractTest.convertMql(element) + "\"");
                    }
                }
            }
        }
        // business objects
        if (_type.getBusType() != null)  {
            final Set<String> busIds = this.mqlAsSet("escape temp query bus \""
                    + AbstractTest.convertMql(_type.getBusType())
                    + "\" \"" + AbstractTest.PREFIX + "*\" * select id dump '\t' recordseparator '\n'");
            for (final String busId : busIds)  {
                this.mql("delete bus " + busId.split("\t")[3]);
            }
        }
    }

    /**
     * Calls given <code>_method</code> in <code>_jpo</code>.
     *
     * @param <T>           class which is returned
     * @param _jpo          name of JPO to call
     * @param _method       method of the called <code>_jpo</code>
     * @param _parameters   list of all parameters for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @see JPOReturn
     */
    public <T> JPOReturn<T> jpoInvoke(final String _jpo,
                                      final String _method,
                                      final Object... _parameters)
        throws IOException, MatrixException
    {
        // encode parameters
        final String[] paramStrings = new String[_parameters.length];
        int idx = 0;
        for (final Object parameter : _parameters)  {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(parameter);
            oos.close();
            paramStrings[idx++] = new String(Base64.encodeBase64(out.toByteArray()));
        }

        return new JPOReturn<T>(JPO.invoke(this.context,
                                           _jpo,
                                           null,
                                           _method,
                                           paramStrings,
                                           Object.class));
    }


    /**
     * Calls given <code>_method</code> in of the MxUpdate eclipse plug-in
     * dispatcher. The MX context {@link #mxContext} is connected to the
     * database if not already done.
     *
     * @param _method       method of the called <code>_jpo</code>
     * @param _params       extra parameters
     * @param _arguments    list of all parameters for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @throws ClassNotFoundException if the class which is decoded from the
     *                          returned string value could not be found
     * @see #mxContext
     * @see #connect()
     */
    public Map<?,?> executeEncoded(final String _method,
                                      final Map<String,String> _params,
                                      final Object... _arguments)
        throws IOException, MatrixException, ClassNotFoundException
    {
        // prepare arguments in a map
        final Map<String,Object> arguments;
        if ((_arguments == null) || (_arguments.length == 0))  {
            arguments = null;
        } else  {
            arguments = new HashMap<String,Object>();
            for (int idx = 0; idx < _arguments.length; )  {
                arguments.put((String) _arguments[idx++], _arguments[idx++]);
            }
        }

        // prepare MQL statement with encoded parameters
        final StringBuilder cmd = new StringBuilder()
            .append("exec prog ").append("org.mxupdate.plugin.Dispatcher \"")
            .append(this.encode(_params)).append("\" \"")
            .append(this.encode(_method)).append("\" \"")
            .append(this.encode(arguments)).append("\"");

        // execute MQL command
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.context, cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  { //$NON-NLS-1$
            throw new MatrixException(mql.getError());
        }

        return this.<Map<?,?>>decode(mql.getResult());
    }

    /**
     * Encodes given <code>_object</code> to a string with <b>base64</b>.
     *
     * @param _object   object to encode
     * @return encoded string
     * @throws IOException if encode failed
     */
    protected String encode(final Object _object)
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(_object);
        oos.close();
        return new String(Base64.encodeBase64(out.toByteArray()));
    }

    /**
     * Decodes given string value to an object of given type
     * <code>&lt;T&gt;</code>. First the string is <b>base64</b> decoded, then
     * the object instance is extracted from the decoded bytes via the Java
     * &quot;standard&quot; feature of the {@link ObjectInputStream}.
     *
     * @param <T>   type of the object which must be decoded
     * @param _arg  string argument with encoded instance of
     *              <code>&lt;T&gt;</code>
     * @return decoded object instance of given type <code>&lt;T&gt;</code>
     * @throws IOException              if the value could not be decoded,
     *                                  the decoder stream could not be
     *                                  opened or the argument at given
     *                                  <code>_index</code> is not defined
     * @throws ClassNotFoundException   if the object itself could not be read
     *                                  from decoder stream
     */
    @SuppressWarnings("unchecked")
    protected final <T> T decode(final String _arg)
        throws IOException, ClassNotFoundException
    {
        final byte[] bytes = Base64.decodeBase64(_arg.getBytes());
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final T ret = (T) ois.readObject();
        ois.close();
        return ret;
    }


    /**
     * Converts given string to MQL by escaping the &quot; so that in escape
     * mode on string could be handled with &quot; and '.
     *
     * @param _text     character sequence to convert
     * @return converted string
     */
    public static String convertMql(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\"")
               : "";
    }

    /**
     * A list of string is joined to one string. Between two string the given
     * separator is set. If quotes parameter is defined each element of the
     * list is surrounded with quotes. Each element is converted to MQL code.
     *
     * @param _separator    separator between two list items
     * @param _quotes       surround the elements of the string with quotes
     * @param _list         list of strings
     * @param _emptyString  string which is written if the list is empty (or
     *                      <code>null</code> if no string for empty list is
     *                      written)
     * @return joined string of the list items
     * @see #convertMql(CharSequence)
     */
    public static String convertMql(final char _separator,
                                    final boolean _quotes,
                                    final Collection<String> _list,
                                    final String _emptyString)
    {
        final StringBuilder ret = new StringBuilder();

        boolean first = true;
        if (_list.isEmpty())  {
            if (_emptyString != null)  {
                ret.append(_emptyString);
            }
        } else  {
            for (final String elem : _list)  {
                if (!first)  {
                    ret.append(_separator);
                } else  {
                    first = false;
                }
                if (_quotes)  {
                    ret.append('\"');
                }
                ret.append(AbstractTest.convertMql(elem));
                if (_quotes)  {
                    ret.append('\"');
                }
            }
        }
        return ret.toString();
    }

    /**
     * Converts given string to MQL by escaping the &quot; so that in escape
     * mode on string could be handled with &quot; and '.
     *
     * @param _text     character sequence to convert
     * @return converted string
     * @see StringUtil_mxJPO#convertUpdate(CharSequence)
     */
    public static String convertUpdate(final CharSequence _text)
    {
        return StringUtil_mxJPO.convertUpdate(_text);
    }

    /**
     * A list of string is joined to one string. Between two string the given
     * separator is set. If quotes parameter is defined each element of the
     * list is surrounded with quotes. Each element is converted to TCL code.
     *
     * @param _quotes       surround the elements of the string with quotes
     * @param _list         list of strings
     * @param _emptyString  string which is written if the list is empty (or
     *                      {@code null} if no string for empty list is
     *                      written)
     * @return joined string of the list items
     */
    public static String convertUpdate(final boolean _quotes,
                                       final Collection<String> _list,
                                       final String _emptyString)
    {
        final StringBuilder ret = new StringBuilder();

        boolean first = true;
        if (_list.isEmpty())  {
            if (_emptyString != null)  {
                ret.append(_emptyString);
            }
        } else  {
            for (final String elem : _list)  {
                if (!first)  {
                    ret.append(' ');
                } else  {
                    first = false;
                }
                if (_quotes)  {
                    ret.append('\"');
                }
                ret.append(AbstractTest.convertUpdate(elem));
                if (_quotes)  {
                    ret.append('\"');
                }
            }
        }
        return ret.toString();
    }

    /**
     * Converts given string to MQL by escaping the &quot; so that in escape
     * mode on string could be handled with &quot; and '.
     *
     * @param _text     character sequence to convert
     * @return converted string
     */
    @Deprecated()
    public static String convertTcl(final CharSequence _text)
    {
        final String text;
        if (_text == null)  {
            text = "";
        } else if (_text.toString().indexOf(' ') >= 0)  {
            text = _text.toString().replaceAll("\\\\", "\\\\\\\\");
        } else  {
            text = _text.toString().replaceAll("\\\\", "\\\\\\\\\\\\\\\\");
        }
        return text.replaceAll("\\\"", "\\\\\"")
                   .replaceAll("\\$", "\\\\\\$")
                   .replaceAll("\\]", "\\\\]")
                   .replaceAll("\\[", "\\\\[")
                   .replaceAll("\\}", "\\\\}")
                   .replaceAll("\\{", "\\\\{");
    }

    /**
     * Class holding the returned values from the JPO.
     *
     * @param <T>   type of the returned values
     * @see AbstractTest#jpoInvoke(String, String, Object...)
     */
    public final class JPOReturn<T>
    {
        /**
         * Returns map from the JPO invoke.
         */
        private final Map<String,?> jpoReturn;

        /**
         * Constructor to hold the return map from the JPO invoke.
         *
         * @param _jpoReturn    return map from the JPO invoke
         */
        @SuppressWarnings("unchecked")
        private JPOReturn(final Object _jpoReturn)
        {
            if (_jpoReturn instanceof String)  {
                this.jpoReturn = new HashMap<String,Object>();
                ((Map<String,Object>) this.jpoReturn).put("values", _jpoReturn);
            } else  {
                this.jpoReturn = (Map<String,?>) _jpoReturn;
            }
        }

        /**
         * Returns the stored values from the JPO return.
         *
         * @return stored values from the JPO return
         * @see #jpoReturn
         */
        @SuppressWarnings("unchecked")
        public T getValues()
        {
            return (T) this.jpoReturn.get("values");
        }

        /**
         * Returns the stored exception from the JPO return.
         *
         * @return stored Exception from the JPO return
         * @see #jpoReturn
         */
        public Exception getException()
        {
            return (Exception) this.jpoReturn.get("exception");
        }
    }

    /**
     * Class to hold an export.
     */
    protected final class Export
    {
        /**
         * Map with the export description.
         */
        private final Map<?,?> exportDesc;

        /**
         * Constructor to initialize this export description.
         *
         * @param _exportDesc   export description with source code, file name,
         *                      etc.
         * @see #exportDesc
         */
        public Export(final Map<?,?> _exportDesc)
        {
            this.exportDesc = _exportDesc;
        }

        /**
         * Returns the source code of a single export.
         *
         * @return source code of a single export
         * @see #exportDesc
         */
        public String getCode()
        {
            return (String) this.exportDesc.get("Code");
        }

        /**
         * Returns the name of a single export.
         *
         * @return name of a single export
         * @see #exportDesc
         */
        public String getName()
        {
            return (String) this.exportDesc.get("Name");
        }

        /**
         * Returns the file name of a single export.
         *
         * @return file name of a single export
         * @see #exportDesc
         */
        public String getFileName()
        {
            return (String) this.exportDesc.get("FileName");
        }

        /**
         * Returns the path of a single export.
         *
         * @return path of a single export
         * @see #exportDesc
         */
        public String getPath()
        {
            return (String) this.exportDesc.get("FilePath");
        }
    }
}
