/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

import org.apache.commons.codec.binary.Base64;
import org.mxupdate.test.data.AbstractData;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract test class to connect to MX and disconnect incl. some helper
 * methods to execute MQL commands and JPOs.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AbstractTest
{
    /**
     * Prefix for all new created test objects. The prefix should be used from
     * all test cases.
     */
    public static final String PREFIX = "MXUPDATE_";

    /**
     * Enumeration to define a mapping between the configuration items and the
     * related administration types in MX and MxUpdate Update.
     */
    public enum CI
    {
        /**
         * Configuration item Data Model Boolean Attribute.
         */
        ATTRIBUTE_BOOLEAN("attribute", "AttributeBoolean", "ATTRIBUTE", "BOOLEAN_", "datamodel/attribute", true),

        /**
         * Configuration item Data Model Date Attribute.
         */
        ATTRIBUTE_DATE("attribute", "AttributeDate", "ATTRIBUTE", "DATE_", "datamodel/attribute", true),

        /**
         * Configuration item Data Model Integer Attribute.
         */
        ATTRIBUTE_INTEGER("attribute", "AttributeInteger", "ATTRIBUTE", "INTEGER_", "datamodel/attribute", true),

        /**
         * Configuration item Data Model Real Attribute.
         */
        ATTRIBUTE_REAL("attribute", "AttributeReal", "ATTRIBUTE", "REAL_", "datamodel/attribute", true),

        /**
         * Configuration item Data Model String Attribute.
         */
        ATTRIBUTE_STRING("attribute", "AttributeString", "ATTRIBUTE", "STRING_", "datamodel/attribute", true),

        /**
         * Configuration item Data Model Interface.
         */
        INTERFACE("interface", "Interface", "INTERFACE", "INTERFACE_", "datamodel/interface", true),

        /**
         * Configuration item Data Model Policy.
         */
        POLICY("policy", "Policy", "POLICY", "POLICY_", "datamodel/policy", true),

        /**
         * Configuration item Data Model Relationship.
         */
        RELATIONSHIP("relationship", "Relationship", "RELATIONSHIP", "RELATIONSHIP_", "datamodel/relationship", true),

        /**
         * Configuration item Data Model Type.
         */
        TYPE("type", "Type", "TYPE", "TYPE_", "datamodel/type", true),

        /**
         * Configuration item JPO program.
         */
        JPO("program", "JPO", null, "", "program/jpo", true),

        /**
         * Configuration item MQL program.
         */
        MQL_PROGRAM("program", "Program", null, "", "program/mql", true),

        /**
         * Configuration item page program.
         */
        PAGE("page", "Page", null, "PAGE_", "program/page", true),

        /**
         * Configuration item group.
         */
        GROUP("group", "Group", "GROUP", "GROUP_", "user/group", true),

        /**
         * Configuration item administration person.
         */
        PERSONADMIN("person", "PersonAdmin", "PERSONADMIN", "PERSONADMIN_", "user/personadmin", true),

        /**
         * Configuration item role.
         */
        ROLE("role", "Role", "ROLE", "ROLE_", "user/role", true),

        /**
         * Configuration item command.
         */
        COMMAND("command", "Command", "COMMAND", "COMMAND_", "userinterface/command", true),

        /**
         * Configuration item inquiry.
         */
        INQUIRY("inquiry", "Inquiry", "INQUIRY", "INQUIRY_", "userinterface/inquiry", true),

        /**
         * Configuration item menu.
         */
        MENU("menu", "Menu", "MENU", "MENU_", "userinterface/menu", true),

        /**
         * Configuration item site (not handled as configuration item from the
         * update tool itself).
         */
        SITE("site", "Site", null, null, null, true);

        /**
         * Related type name in MX.
         */
        final String mxType;

        /**
         * Related type name in MxUpdate Update.
         */
        public final String updateType;

        /**
         * Used name in the header.
         */
        public final String header;

        /**
         * Prefix used for the file name.
         */
        public final String filePrefix;

        /**
         * Path where the configuration item update file is located.
         */
        public final String filePath;

        /**
         * Does the wild card search for the type works?
         */
        public final boolean wildcardSearch;

        /**
         * Constructor to initialize an enumeration instance.
         *
         * @param _mxType               related type name in MX
         * @param _updateType           related type name in MxUpdate Update
         * @param _header               used text in the header to define the
         *                              name
         * @param _filePrefix           prefix used for the file
         * @param _filePath             file path
         * @param _wildcardSearch       wild card search allowed?
         */
        CI(final String _mxType,
           final String _updateType,
           final String _header,
           final String _filePrefix,
           final String _filePath,
           final boolean _wildcardSearch)
        {
            this.mxType = _mxType;
            this.updateType = _updateType;
            this.header = _header;
            this.filePrefix = _filePrefix;
            this.filePath = _filePath;
            this.wildcardSearch = _wildcardSearch;
        }

        /**
         * Returns MX type of this configuration.
         *
         * @return MX type of this configuration item
         */
        public String getMxType()
        {
            return this.mxType;
        }
    }

    /**
     * MX context.
     */
    private Context context;

    /**
     * Returns the {@link #context} connection to MX.
     *
     * @return MX context
     * @see #context
     */
    public Context getContext()
    {
        return this.context;
    }

    /**
     * Connects to MX.
     *
     * @throws Exception if connect failed
     */
    @BeforeClass
    public void connect()
        throws Exception
    {
//        this.context = new Context("http://172.16.62.120:8080/ematrix");
//        this.context = new Context("http://172.16.62.130:8080/ENOVIA");
        this.context = new Context("http://172.16.62.130:8080/enovia");
        this.context.resetContext("creator", "", null);
        this.context.connect();
    }

    /**
     * Disconnects from MX.
     *
     * @throws Exception if disconnect failed
     */
    @AfterClass
    public void close()
        throws Exception
    {
        this.context.disconnect();
        this.context.closeContext();
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
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if MQL calls failed
     */
    protected Export export(final CI _type,
                            final String _name)
        throws IOException, MatrixException
    {
        final Map<String,Collection<String>> params = new HashMap<String,Collection<String>>(1);
        params.put(_type.updateType, Arrays.asList(new String[]{_name}));
        final Map<String,Collection<Map<String,String>>> bck =
                this.<Map<String,Collection<Map<String,String>>>>jpoInvoke("org.mxupdate.plugin.Export",
                                                                           "exportByName",
                                                                           params)
                    .getValues();

        Assert.assertNotNull(bck);
        Assert.assertTrue(bck.containsKey(_type.updateType));
        Assert.assertEquals(bck.get(_type.updateType).size(), 1, "one element is returned");

        final Export ret =  new Export(bck.get(_type.updateType).iterator().next());

        Assert.assertEquals(ret.getName(), _name, "returned name is equal to given name");

        return ret;
    }

    /**
     * Makes an update for given administration <code>_object</code>
     * definition.
     *
     * @param _object       object if the update definition
     * @param _errorCode    expected error code
     */
    protected void updateFailure(final AbstractData<?> _object,
                                 final UpdateException_mxJPO.Error _errorCode)
    {
        this.updateFailure(_object.getCIFileName(), _object.ciFile(), _errorCode);
    }

    /**
     * Makes an update for given <code>_fileName</code> and <code>_code</code>.
     *
     * @param _fileName     name of the file to update
     * @param _code         TCL update code
     * @param _errorCode    expected error code
     */
    protected void updateFailure(final String _fileName,
                                 final String _code,
                                 final UpdateException_mxJPO.Error _errorCode)
    {
         Exception ex = null;
         try  {
             final Map<String,String> params = new HashMap<String,String>();
             params.put(_fileName, _code);
             this.<String>jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);
         } catch (final Exception e)  {
             ex = e;
         }
         Assert.assertNotNull(ex, "check that action is not allowed");
         Assert.assertTrue(ex.getMessage().indexOf("UpdateError #" + _errorCode.getCode() + ":") >= 0,
                           "check for correct error code #" + _errorCode.getCode());
    }

    /**
     * Makes an update for given administration <code>_object</code>
     * definition.
     *
     * @param _object   object if the update definition
     * @return returned string with the update logging
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if MQL calls failed
     */
    protected JPOReturn<String> update(final AbstractData<?> _object)
        throws IOException, MatrixException
    {
        return this.update(_object.getCIFileName(), _object.ciFile());
    }

    /**
     * Makes an update for given <code>_fileName</code> and <code>_code</code>.
     *
     * @param _fileName name of the file to update
     * @param _code     TCL update code
     * @return returned string with the update logging
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if MQL calls failed
     */
    protected JPOReturn<String> update(final String _fileName,
                                       final String _code)
        throws IOException, MatrixException
    {
        final Map<String,String> params = new HashMap<String,String>();
        params.put(_fileName, _code);
        return this.<String>jpoInvoke("org.mxupdate.plugin.Update", "updateByContent", params);
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
        final Set<String> elements;
        if (_type.wildcardSearch)  {
            elements = this.mqlAsSet("escape list " + _type.mxType + " \"" + AbstractTest.PREFIX + "*\"");
        } else  {
            elements = this.mqlAsSet("escape list " + _type.mxType);
        }
        if (_type == AbstractTest.CI.INTERFACE)  {
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

    /**
     * Calls given <code>_method</code> in <code>_jpo</code>. The MX context
     * {@link #mxContext} is connected to the database if not already done.
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
     * @see #context
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
    public static String convertTcl(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\"")
                                 .replaceAll("\\$", "\\\\\\$")
                                 .replaceAll("\\]", "\\\\]")
                                 .replaceAll("\\[", "\\\\[")
                                 .replaceAll("\\}", "\\\\}")
                                 .replaceAll("\\{", "\\\\{")
               : "";
    }

    /**
     * Converts given string to MQL by escaping the &quot; so that in escape
     * mode on string could be handled with &quot; and '.
     *
     * @param _text     character sequence to convert
     * @return converted string
     */
    public static String convertTclDoubleEscaped(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\\\\\\\\\"")
                                 .replaceAll("\\$", "\\\\\\$")
                                 .replaceAll("\\}", "\\\\}")
                                 .replaceAll("\\{", "\\\\{")
               : "";
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
    }

    /**
     * Class to hold an export.
     */
    protected final class Export
    {
        /**
         * Map with the export description.
         */
        private final Map<String,String> exportDesc;

        /**
         * Constructor to initialize this export description.
         *
         * @param _exportDesc   export description with source code, file name,
         *                      etc.
         * @see #exportDesc
         */
        public Export(final Map<String,String> _exportDesc)
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
            return this.exportDesc.get("code");
        }

        /**
         * Returns the name of a single export.
         *
         * @return name of a single export
         * @see #exportDesc
         */
        public String getName()
        {
            return this.exportDesc.get("name");
        }

        /**
         * Returns the file name of a single export.
         *
         * @return file name of a single export
         * @see #exportDesc
         */
        public String getFileName()
        {
            return this.exportDesc.get("filename");
        }

        /**
         * Returns the path of a single export.
         *
         * @return path of a single export
         * @see #exportDesc
         */
        public String getPath()
        {
            return this.exportDesc.get("path");
        }
    }
}
