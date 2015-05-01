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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.apache.commons.codec.binary.Base64;
import org.apache.maven.settings.DefaultMavenSettingsBuilder;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Settings;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

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
        /** Configuration item Data Model Binrary Attribute. */
        DM_ATTRIBUTE_BINARY("attribute", null, false, null, null, "AttributeBinary", "ATTRIBUTE", "BINARY_", "datamodel/attribute", true, "updateAttribute"),
        /** Configuration item Data Model Boolean Attribute. */
        DM_ATTRIBUTE_BOOLEAN("attribute", null, false, null, null, "AttributeBoolean", "ATTRIBUTE", "BOOLEAN_", "datamodel/attribute", true, "updateAttribute"),
        /** Configuration item Data Model Date Attribute. */
        DM_ATTRIBUTE_DATE("attribute", null, false, null, null, "AttributeDate", "ATTRIBUTE", "DATE_", "datamodel/attribute", true, "updateAttribute"),
        /** Configuration item Data Model Integer Attribute. */
        DM_ATTRIBUTE_INTEGER("attribute", null, false, null, null, "AttributeInteger", "ATTRIBUTE", "INTEGER_", "datamodel/attribute", true, "updateAttribute"),
        /** Configuration item Data Model Real Attribute. */
        DM_ATTRIBUTE_REAL("attribute", null, false, null, null, "AttributeReal", "ATTRIBUTE", "REAL_", "datamodel/attribute", true, "updateAttribute"),
        /** Configuration item Data Model String Attribute. */
        DM_ATTRIBUTE_STRING("attribute", null, false, null, null, "AttributeString", "ATTRIBUTE", "STRING_", "datamodel/attribute", true, "updateAttribute"),

        /** Configuration item Data Model Dimension. */
        DM_DIMENSION("dimension", null, false, null, null, "Dimension", "DIMENSION", "DIMENSION_", "datamodel/dimension", true, "updateDimension"),

        /** Configuration item Data Model Expression. */
        DM_EXPRESSION("expression", null, false, null, null, "Expression", "EXPRESSION", "EXPRESSION_", "datamodel/expression", true, "mql"),

        /** Configuration item Data Model Format. */
        DM_FORMAT("format", null, false, null, null, "Format", "FORMAT", "FORMAT_", "datamodel/format", true, "updateFormat"),

        /** Configuration item Data Model Interface. */
        DM_INTERFACE("interface", null, false, null, null, "Interface", "INTERFACE", "INTERFACE_", "datamodel/interface", true, "mql"),

        /** Configuration item IEF Global Configuration. */
        DM_NOTIFICATION(null, "Notification", false, "Business Rule", "eService Administration",
                              "Notification", "NOTIFICATION", "NOTIFICATION_", "datamodel/notification", true, "mql"),

        /** Configuration item Data Model Policy. */
        DM_POLICY("policy", null, false, null, null, "Policy", "POLICY", "POLICY_", "datamodel/policy", true, "updatePolicy"),

        /** Configuration item Data Model Relationship. */
        DM_RELATIONSHIP("relationship", null, false, null, null, "Relationship", "RELATIONSHIP", "RELATIONSHIP_", "datamodel/relationship", true, "mql"),

        /** Configuration item Data Model Rule. */
        DM_RULE("rule", null, false, null, null, "Rule", "RULE", "RULE_", "datamodel/rule", true, "mql"),

        /** Configuration item Data Model Type. */
        DM_TYPE("type", null, false, null, null, "Type", "TYPE", "TYPE_", "datamodel/type", true, "mql"),

        /** Configuration item IEF Global Configuration. */
        IEF_EBOMSYNC_CONFIG(null, "IEF-EBOMSyncConfig", true, "IEF-EBOMSyncConfig", "eService Administration",
                            "IEFEBOMSyncConfig", "IEFEBOMSYNC", "IEFEBOMSYNC_", "integration/ebomsync", true, "mql"),
        /** Configuration item IEF Global Configuration. */
        IEF_GLOBAL_CONFIG(null, "MCADInteg-GlobalConfig", true, "MCADInteg-ConfigObjectPolicy", "eService Administration",
                          "IEFGlobalConfig", "IEFGLOBALCONFIG", "IEFGLOBALCONFIG_", "integration/globalconfig", true, "mql"),
        /** Configuration item IEF Global Registry.*/
        IEF_GLOBAL_REGISTRY(null, "IEF-GlobalRegistry", false, "MCADInteg-ConfigObjectPolicy", "eService Administration",
                            "IEFGlobalRegistry", "IEFGLOBALREGISTRY", "IEFGLOBALREGISTRY_", "integration/globalregistry", true, "mql"),
        /** Configuration item IEF Mass Promote Configuration. */
        IEF_MASS_PROMOTE_CONFIG(null, "IEF-MassPromoteConfig", true, "MCADInteg-ConfigObjectPolicy", "eService Administration",
                                "IEFMassPromoteConfig", "IEFMASSPROMOTECONFIG", "IEFMASSPROMOTECONFIG_", "integration/masspromoteconfig", true, "mql"),
        /** Configuration item IEF unassigned registry. */
        IEF_UNASSIGNED_REGISTRY(null, "IEF-UnassignedIntegRegistry", true, "MCADInteg-ConfigObjectPolicy", "eService Administration",
                                "IEFUnassignedRegistry", "IEFUNASSIGNEDREGISTRY", "IEFUNASSIGNEDREGISTRY_", "integration/unassignedregistry", true, "mql"),

        /** Configuration item JPO program. */
        PRG_JPO("program", null, false, null, null, "JPO", null, "", "program/jpo", true, "mql"),

        /** Configuration item MQL program. */
        PRG_MQL_PROGRAM("program", null, false, null, null, "Program", null, "", "program/mql", true, "mql"),

        /** Configuration item page program. */
        PRG_PAGE("page", null, false, null, null, "Page", "PAGE", "PAGE_", "program/page", true, "mql"),

        /** Configuration item group. */
        USR_GROUP("group", null, false, null, null, "Group", "GROUP", "GROUP_", "user/group", true, "mql"),
        /** Configuration item person. */
        USR_PERSON("person", "Person", false, "Person", "eService Production", "Person", "PERSON", "PERSON_", "user/person", true, "mql"),
        /** Configuration item administration person. */
        USR_PERSONADMIN("person", null, false, null, null, "PersonAdmin", "PERSONADMIN", "PERSONADMIN_", "user/personadmin", true, "mql"),

        /** Configuration item role. */
        USR_ROLE("role", null, false, null, null, "Role", "ROLE", "ROLE_", "user/role", true, "mql"),

        /** Other definition for business unit. */
        OTHER_BUSINESSUNIT(null, "Business Unit", false, "Organization", "eService Production", "Business Unit", null, null, null, true, "mql"),
        /** Other definition for company. */
        OTHER_COMPANY(null, "Company", false, "Organization", "eService Production", "Company", null, null, null, true, "mql"),
        /** Other definition for department. */
        OTHER_DEPARTMENT(null, "Department", false, "Organization", "eService Production", "Department", null, null, null, true, "mql"),
        /** Other definition for plant. */
        OTHER_PLANT(null, "Plant", false, "Organization", "eService Production", "Plant", null, null, null, true, "mql"),

        /** Configuration item channel. */
        UI_CHANNEL("channel", null, false, null, null, "Channel", "CHANNEL", "CHANNEL_", "userinterface/channel", true, "mxUpdate"),
        /** Configuration item command. */
        UI_COMMAND("command", null, false, null, null, "Command", "COMMAND", "COMMAND_", "userinterface/command", true, "mxUpdate"),
        /** Configuration item form. */
        UI_FORM("form", null, false, null, null, "Form", "FORM", "FORM_", "userinterface/form", true, "mql"),
        /** Configuration item inquiry. */
        UI_INQUIRY("inquiry", null, false, null, null, "Inquiry", "INQUIRY", "INQUIRY_", "userinterface/inquiry", true, "mql"),
        /** Configuration item menu. */
        UI_MENU("menu", null, false, null, null, "Menu", "MENU", "MENU_", "userinterface/menu", true, "mxUpdate"),
        /** Configuration item portal. */
        UI_PORTAL("portal", null, false, null, null, "Portal", "PORTAL", "PORTAL_", "userinterface/portal", true, "mxUpdate"),
        /** Configuration item table.*/
        UI_TABLE("table", null, false, null, null, "Table", "TABLE", "TABLE_", "userinterface/table", false, "mql"),

        /** Configuration item site (not handled as configuration item from the update tool itself). */
        OTHER_SITE("site", null, false, null, null, "Site", null, null, null, true, "mql");

        /** Related type name in MX. */
        final String mxType;

        /** Related type of the business object. */
        private final String busType;
        /** Are business objects derived from {@link #busType type}? */
        private final boolean hasDerived;
        /** Related policy of the business object. */
        private final String busPolicy;
        /** Related vault of the business object. */
        private final String busVault;

        /** Starting tag used in the CI file. */
        private final String urlTag;

        /** Related type name in MxUpdate Update. */
        public final String updateType;

        /** Used name in the header. */
        public final String header;

        /** Prefix used for the file name. */
        public final String filePrefix;
        /** Path where the configuration item update file is located. */
        public final String filePath;

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
         * @param _header               used text in the header to define the
         *                              name
         * @param _filePrefix           prefix used for the file
         * @param _filePath             file path
         * @param _wildcardSearch       wild card search allowed?
         * @param _urlTag               url tag
         */
        CI(final String _mxType,
           final String _busType,
           final boolean _hasDerived,
           final String _busPolicy,
           final String _busVault,
           final String _updateType,
           final String _header,
           final String _filePrefix,
           final String _filePath,
           final boolean _wildcardSearch,
           final String _urlTag)
        {
            this.mxType = _mxType;
            this.busType = _busType;
            this.hasDerived = _hasDerived;
            this.busPolicy = _busPolicy;
            this.busVault = _busVault;
            this.updateType = _updateType;
            this.header = _header;
            this.filePrefix = _filePrefix;
            this.filePath = _filePath;
            this.wildcardSearch = _wildcardSearch;
            this.urlTag = _urlTag;
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

        /**
         * Returns the {@link #urlTag Url tag}.
         *
         * @return Url tag
         */
        public String getUrlTag()
        {
            return this.urlTag;
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
    @AfterClass()
    public void close()
        throws Exception
    {
        this.context.shutdown();
        this.context = null;
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
        return MqlUtil_mxJPO.execMql(this.context, _cmd, true);
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
     * Makes a clean up for given configuration item <code>_type</code> with
     * <code>_name</code>. This means the configuration item object is deleted.
     *
     * @param _type     configuration item type to clean up (delete)
     * @param _name     name of the configuration item to clean up (delete)
     * @throws MatrixException if delete failed
     */
    protected void cleanup(final CI _type,
                           final String _name)
        throws MatrixException
    {
        if (!"".equals(this.mql("list " + _type.mxType + " '" + _name + "'")))  {
            this.mql("delete " + _type.mxType + " '" + _name + "'");
        }
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
     * Converts given string to MQL by escaping the &quot; so that in escape
     * mode on string could be handled with &quot; and '.
     *
     * @param _text     character sequence to convert
     * @return converted string
     */
    public static String convertUpdate(final CharSequence _text)
    {
        final String text;
        if (_text == null)  {
            text = "";
        } else  {
            text = _text.toString();
        }
        return text.replaceAll("\\\\", "\\\\\\\\")
                   .replaceAll("\\\"", "\\\\\"");
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
