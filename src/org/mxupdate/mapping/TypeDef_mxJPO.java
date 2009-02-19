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

package org.mxupdate.mapping;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;

import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * Enumeration for administration type definitions.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public final class TypeDef_mxJPO
        extends AbstractValue_mxJPO
{
    /**
     * Maps from the name of the type definition group to the related type
     * definition group instance.
     *
     * @see #defineValue(Context, String, String)
     */
    private static final Map<String,TypeDef_mxJPO> MAP
            = new HashMap<String,TypeDef_mxJPO>();

    /**
     * Map between the JPO name and the class name used within MX.
     *
     * @see #resetValues()
     * @see #defineValue(Context, String, String)
     */
    private static final Map<String,String> MAP2CLASS = new HashMap<String,String>();

    /**
     * Used prefix of admin type suffix.
     *
     * @see #defineValue(Context, String, String)
     * @see #adminSuffix
     */
    private static final String PREFIX_ADMIN_SUFFIX = "AdminSuffix";

    /**
     * Used prefix of the admin type name.
     *
     * @see #defineValue(Context, String, String)
     * @see #adminType
     */
    private static final String PREFIX_ADMIN_TYPE = "AdminType";

    /**
     * Used prefix of ignored attributes for business objects within the
     * property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busIgnoredAttributes
     */
    private static final String PREFIX_BUS_IGNOREATTRIBUTES = "BusIgnoreAttributes";

    /**
     * Used prefix of check exists definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busCheckExists
     */
    private static final String PREFIX_BUS_CHECKEXISTS = "BusCheckExists";

    /**
     * Used prefix of policy definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busPolicy
     */
    private static final String PREFIX_BUS_POLICY = "BusPolicy";

    /**
     * Used prefix of both relationship definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busRelsBoth
     */
    private static final String PREFIX_BUS_RELSBOTH = "BusRelsBoth";

    /**
     * Used prefix of from relationship definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busRelsFrom
     */
    private static final String PREFIX_BUS_RELSFROM = "BusRelsFrom";

    /**
     * Used prefix of to relationship definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busRelsTo
     */
    private static final String PREFIX_BUS_RELSTO = "BusRelsTo";

    /**
     * Used prefix of business type definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busType
     */
    private static final String PREFIX_BUS_TYPE = "BusType";

    /**
     * Used prefix of vault definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #busVault
     */
    private static final String PREFIX_BUS_VAULT = "BusVault";

    /**
     * Used prefix of type definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #filePath
     */
    private static final String PREFIX_FILE_PATH = "FilePath";

    /**
     * Used prefix of file last matching definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #fileMatchLast
     */
    private static final String PREFIX_FILE_MATCHLAST = "FileMatchLast";

    /**
     * Used prefix of file prefix definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #filePrefix
     */
    private static final String PREFIX_FILE_PREFIX = "FilePrefix";

    /**
     * Used file suffix of file suffix definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #fileSuffix
     */
    private static final String PREFIX_FILE_SUFFIX = "FileSuffix";

    /**
     * Used prefix of the JPO name.
     *
     * @see #defineValue(Context, String, String)
     * @see #jpoClass
     */
    private static final String PREFIX_JPO = "JPO";

    /**
     * Used logging text of type definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #textLogging
     */
    private static final String PREFIX_TEXT_LOGGING = "TextLogging";

    /**
     * Used title of type definitions within the property file.
     *
     * @see #defineValue(Context, String, String)
     * @see #textTitle
     */
    private static final String PREFIX_TEXT_TITLE = "TextTitle";

    /**
     * Defines the name of the admin type.
     *
     * @see #getMxAdminName()
     * @see #defineValue(Context, String, String)
     */
    private String adminType;

    /**
     * Defines the suffix of the admin type.
     *
     * @see #getMxAdminSuffix()
     * @see #defineValue(Context, String, String)
     */
    private String adminSuffix;

    /**
     * Must be checked if the business type exists?
     *
     * @see #isBusCheckExists
     * @see #defineValue(Context, String, String)
     */
    private boolean busCheckExists = false;

    /**
     * Defines the list of attributes which are automatically ignored
     * within the update.
     *
     * @see #getMxBusIgnoredAttributes()
     * @see #defineValue(Context, String, String)
     */
    private Collection<String> busIgnoredAttributes;

    /**
     * Mapping between internal used type definitions and the MX policy
     * names.
     *
     * @see #getMxBusPolicy()
     * @see #defineValue(Context, String, String)
     */
    private String busPolicy;

    /**
     * Defines the list of both relationships which must be evaluated to write
     * and run of update script.
     *
     * @see #getMxBusRelsBoth()
     * @see #defineValue(Context, String, String)
     */
    private Collection<String> busRelsBoth;

    /**
     * Defines the list of from relationships which must be evaluated to write
     * and run of update script.
     *
     * @see #getMxBusRelsFrom()
     * @see #defineValue(Context, String, String)
     */
    private Collection<String> busRelsFrom;

    /**
     * Defines the list of to relationships which must be evaluated to write
     * and run of update script.
     *
     * @see #getMxBusRelsTo()
     * @see #defineValue(Context, String, String)
     */
    private Collection<String> busRelsTo;

    /**
     * Mapping between internal used type definitions and the MX type
     * names.
     *
     * @see #getMxBusType()
     * @see #defineValue(Context, String, String)
     */
    private String busType;

    /**
     * Mapping between internal used type definitions and the MX vault
     * names.
     *
     * @see #getMxBusVault()
     * @see #defineValue(Context, Context, String, String)
     */
    private String busVault;

    /**
     * Must the matching files matched on last positions? E.g. required if no
     * {@see #filePrefix} and no {@see #fileSuffix} are defined.
     *
     * @see #defineValue(Context, String, String)
     */
    private boolean fileMatchLast = false;

    /**
     * Mapping between internal used type definitions and the file paths.
     *
     * @see #getFilePath()
     * @see #defineValue(Context, String, String)
     */
    private String filePath;

    /**
     * Mapping between internal used administration type definitions and the
     * file prefixes.
     *
     * @see #getFilePrefix()
     * @see #defineValue(Context, String, String)
     */
    private String filePrefix;

    /**
     * Mapping between internal used administration type definitions and the
     * file suffixes.
     *
     * @see #getFileSuffix()
     * @see #defineValue(Context, String, String)
     */
    private String fileSuffix;

    /**
     * Stores the class implementing the MxUpdate functionality.
     *
     * @see #newTypeInstance()
     * @see #defineJPOClass(Context, String)
     */
    private Class<? extends AbstractObject_mxJPO> jpoClass;

    /**
     * Mapping between internal used administration type definitions and the
     * logging string.
     *
     * @see #getLogging()
     * @see #defineValue(Context, String, String)
     */
    private String textLogging;

    /**
     * Mapping between internal used administration type definitions and the
     * titles.
     *
     * @see #getTitle()
     * @see #defineValue(Context, String, String)
     */
    private String textTitle;

    /**
     * Resets type definition map.
     *
     * @see #MAP
     */
    protected static void resetValues()
    {
        MAP.clear();
        MAP2CLASS.clear();
    }

    /**
     *
     * @param _context
     * @param _key
     * @param _value
     * @throws Exception
     * @see #MAP
     * @see #defineJPOClass(Context, String)
     */
    protected static void defineValue(final Context _context,
                                      final String _key,
                                      final String _value)
            throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        TypeDef_mxJPO typeDef = MAP.get(enumName);
        if (typeDef == null)  {
            typeDef = new TypeDef_mxJPO(enumName);
            MAP.put(enumName, typeDef);
        }

        if (key.equals(PREFIX_ADMIN_SUFFIX))  {
            typeDef.adminSuffix = _value;
        } else if (key.equals(PREFIX_ADMIN_TYPE))  {
            typeDef.adminType = _value;
        } else if (key.equals(PREFIX_BUS_CHECKEXISTS))  {
            typeDef.busCheckExists = _value.equalsIgnoreCase("true");
        } else if (key.equals(PREFIX_BUS_IGNOREATTRIBUTES))  {
            typeDef.busIgnoredAttributes = Arrays.asList(_value.split(","));
        } else if (key.equals(PREFIX_BUS_RELSBOTH))  {
            typeDef.busRelsBoth = Arrays.asList(_value.split(","));
        } else if (key.equals(PREFIX_BUS_RELSFROM))  {
            typeDef.busRelsFrom = Arrays.asList(_value.split(","));
        } else if (key.equals(PREFIX_BUS_RELSTO))  {
            typeDef.busRelsTo = Arrays.asList(_value.split(","));
        } else if (key.equals(PREFIX_BUS_POLICY))  {
            typeDef.busPolicy = _value;
        } else if (key.equals(PREFIX_BUS_TYPE))  {
            typeDef.busType = _value;
        } else if (key.equals(PREFIX_BUS_VAULT))  {
            typeDef.busVault = _value;
        } else if (key.equals(PREFIX_FILE_MATCHLAST))  {
            typeDef.fileMatchLast = _value.equalsIgnoreCase("true");
        } else if (key.equals(PREFIX_FILE_PATH))  {
            typeDef.filePath = _value;
        } else if (key.equals(PREFIX_FILE_PREFIX))  {
            typeDef.filePrefix = _value;
        } else if (key.equals(PREFIX_FILE_SUFFIX))  {
            typeDef.fileSuffix = _value;
        } else if (key.equals(PREFIX_JPO))  {
            typeDef.defineJPOClass(_context, _value);
        } else if (key.equals(PREFIX_TEXT_LOGGING))  {
            typeDef.textLogging = _value;
        } else if (key.equals(PREFIX_TEXT_TITLE))  {
            typeDef.textTitle = _value;
        } else  {
            typeDef.defineValues(key, _value);
        }
    }

    /**
     * Returns for given name the related type definition instance.
     *
     * @param _name name of the searched type definition instance
     * @return found type definition instance (or <code>null</code> if not
     *         found)
     * @see #MAP
     */
    public static TypeDef_mxJPO valueOf(final String _name)
    {
        return MAP.get(_name);
    }

    /**
     * Returns the list of all type definition instances.
     *
     * @return list of all type definition instances
     * @see #MAP
     */
    public static Collection<TypeDef_mxJPO> values()
    {
        return MAP.values();
    }

    /**
     * The constructor is defined private so that a new instance could only
     * created within this class.
     *
     * @param _name     name of the type definition group
     */
    private TypeDef_mxJPO(final String _name)
    {
        super(_name);
    }

    /**
     * Defines the class for given JPO name.
     *
     * @param _context  MX context for this request
     * @param _jpoName  name of searched JPO
     * @throws Exception if the list of MxUdpate JPOs could not evaluated or
     *                   if the related class could not be found
     */
    @SuppressWarnings("unchecked")
    private void defineJPOClass(final Context _context,
                                final String _jpoName)
            throws Exception
    {
        if (MAP2CLASS.isEmpty())  {
            final String tmp = MqlUtil_mxJPO.execMql(_context, "list prog org.mxupdate.* select name classname dump '\t'");
            for (final String line : tmp.split("\n"))  {
                final String[] arr = line.split("\t");
                if (arr.length > 1)  {
                    MAP2CLASS.put(arr[0], arr[1]);
                }
            }
        }
        this.jpoClass = (Class<? extends AbstractObject_mxJPO>) Class.forName(MAP2CLASS.get(_jpoName));
    }

    /**
     * Returns the related administration type fsufix used within MX. The
     * method returns only correct values if the initialize method was
     * called!
     *
     * @return MX suffix of the administration type definition
     * @see #adminSuffix
     */
    public String getMxAdminSuffix()
    {
        return this.adminSuffix;
    }

    /**
     * Returns the related administration type name used within MX. The
     * method returns only correct values if the initialize method was
     * called!
     *
     * @return MX name of the administration type definition
     * @see #adminType
     */
    public String getMxAdminName()
    {
        return this.adminType;
    }

    /**
     * Must be checked if the business type exists? The method returns only
     * correct values if the initialize method was called!
     *
     * @return <i>true</i> if a check must be done if the type exists;
     *         otherwise <i>false</i>
     * @see #busCheckExists
     */
    public boolean isBusCheckExists()
    {
        return this.busCheckExists;
    }

    /**
     * Checks if the business type defining this type definition exists.
     *
     * @param _context      MX context for this request
     * @return <i>true</i> if the business type exists; otherwise <i>false</i>
     * @throws MatrixException if the check failed
     */
    public boolean existsBusType(final Context _context)
            throws MatrixException
    {
        final String tmp = execMql(_context,
                new StringBuilder().append("list type '").append(this.busType).append("'"));
        return (tmp.length() > 0);
    }

    /**
     * Returns the list of attributes for business object which are ignored for
     * the update (means this attributes are not reseted). The method returns
     * only correct values if the initialize method was called!
     *
     * @return MX name of the business type definition
     * @see #busIgnoredAttributes
     */
    public Collection<String> getMxBusIgnoredAttributes()
    {
        return this.busIgnoredAttributes;
    }

    /**
     * Returns the related business policy name used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return MX name of the business type definition
     * @see #busPolicy
     */
    public String getMxBusPolicy()
    {
        return this.busPolicy;
    }

    /**
     * Returns the related both relationship used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return list of both relationships
     * @see #busRelsBoth
     */
    public Collection<String> getMxBusRelsBoth()
    {
        return this.busRelsBoth;
    }

    /**
     * Returns the related from relationship used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return list of from relationships
     * @see #busRelsFrom
     */
    public Collection<String> getMxBusRelsFrom()
    {
        return this.busRelsFrom;
    }

    /**
     * Returns the related from relationship used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return list of from relationships
     * @see #busRelsTo
     */
    public Collection<String> getMxBusRelsTo()
    {
        return this.busRelsTo;
    }

    /**
     * Returns the related business type name used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return MX name of the business type definition
     * @see #busType
     */
    public String getMxBusType()
    {
        return this.busType;
    }

    /**
     * Returns the related business vault name used within MX. The method
     * returns only correct values if the initialize method was called!
     *
     * @return MX name of the business vault definition
     * @see #busVault
     */
    public String getMxBusVault()
    {
        return this.busVault;
    }

    /**
     * Return <i>true</i> if matching files must be checked on last position
     * after file name checks for all other types. Only if a file before does
     * not match, this files could be checked against this type. If this
     * behavior for matching files is not required, a <i>false</i> is returned.
     * The parameter is only evaluated it the path parameter is defined!
     *
     * @return <i>true</i> if matching files must be checked at last (after all
     *         other types are checked); otherwise <i>false</i>
     * @see #fileMatchLast
     */
    public boolean isFileMatchLast()
    {
        return this.fileMatchLast;
    }

    /**
     * Returns the related file path. The method returns only correct
     * values if the initialize method was called!
     *
     * @return file path of the administration type definition
     * @see #filePath
     */
    public String getFilePath()
    {
        return this.filePath;
    }

    /**
     * Returns the related file name prefix. The method returns only
     * correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     * @see TypeDefValues#filePrefix
     */
    public String getFilePrefix()
    {
        return this.filePrefix;
    }

    /**
     * Returns the related file name suffix. The method returns only
     * correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     * @see #fileSuffix
     */
    public String getFileSuffix()
    {
        return this.fileSuffix;
    }

    /**
     * Returns the related logging string. The method returns only correct
     * values if the initialize method was called!
     *
     * @return logging string of the administration type definition
     * @see #textLogging
     */
    public String getLogging()
    {
        return this.textLogging;
    }

    /**
     * Returns the related title used in the MX update files. The method
     * returns only correct values if the initialize method was called!
     *
     * @return title of the business type definition
     * @see #textTitle
     */
    public String getTitle()
    {
        return this.textTitle;
    }

    /**
     *
     * @param _mxName   MX name of the new instance
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public AbstractObject_mxJPO newTypeInstance(final String _mxName)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                   InstantiationException, IllegalAccessException, InvocationTargetException
    {
        return this.jpoClass.getConstructor(TypeDef_mxJPO.class, String.class)
                            .newInstance(this, _mxName);
    }
}