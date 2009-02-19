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

package org.mxupdate.update.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;

import org.mxupdate.mapping.ParameterDef_mxJPO;

/**
 * The class is used to stored the defined parameters from the console.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class ParameterCache_mxJPO
{
    /**
     * String of the key within the parameter cache for the application
     * parameter.
     */
    public static final String KEY_APPLICATION = "Application";

    /**
     * String of the key within the parameter cache for the author parameter.
     */
    public static final String KEY_AUTHOR = "Author";

    /**
     * String of the key within the parameter cache for the default application
     * parameter.
     */
    public static final String KEY_DEFAULTAPPLICATION = "DefaultApplication";

    /**
     * String of the key within the parameter cache for the default author
     * parameter.
     */
    public static final String KEY_DEFAULTAUTHOR = "DefaultAuthor";

    /**
     * String of the key within the parameter cache for the default installer
     * parameter.
     */
    public static final String KEY_DEFAULTINSTALLER = "DefaultInstaller";

    /**
     * String of the key within the parameter cache for the use file date as
     * version parameter.
     */
    public static final String KEY_FILEDATE2VERSION = "FileDate2Version";

    /**
     * String of the key within the parameter cache for the installer
     * parameter.
     */
    public static final String KEY_INSTALLER = "Installer";

    /**
     * String of the key within the parameter cache for the version parameter.
     */
    public static final String KEY_VERSION = "Version";

    /**
     * Mapping between parameter definition and the related boolean value.
     *
     * @see #ParameterCache_mxJPO(Context,Collection)
     * @see #evalParameter(ParameterDef_mxJPO, String[], int)
     * @see #getValueBoolean(String)
     * @see #defineValueBoolean(String, Boolean)
     */
    final Map<String,Boolean> mapBoolean;

    /**
     * Mapping between parameter definition and the related integer value.
     *
     * @see #ParameterCache_mxJPO(Context,Collection)
     * @see #evalParameter(ParameterDef_mxJPO, String[], int)
     * @see #getValueInteger(String)
     * @see #defineValueInteger(String, Integer)
     */
    final Map<String,Integer> mapInteger;

    /**
     * Mapping between parameter definition and the related list of string
     * values.
     *
     * @see #ParameterCache_mxJPO(Context,Collection)
     * @see #evalParameter(ParameterDef_mxJPO, String[], int)
     */
    final Map<String,Collection<String>> mapList;

    /**
     * Mapping between parameter definition and the related map of string
     * values.
     *
     * @see #ParameterCache_mxJPO(Context,Collection)
     * @see #evalParameter(ParameterDef_mxJPO, String[], int)
     */
    final Map<String,Map<String,?>> mapMap;

    /**
     * Mapping between the enumeration name of the parameter and the string
     * value.
     *
     * @see #ParameterCache_mxJPO(Context,Collection)
     * @see #evalParameter(ParameterDef_mxJPO, String[], int)
     */
    final Map<String,String> mapString;

    /**
     * Stores as parameter the related MX context.
     *
     * @see #getContext()
     */
    final Context context;

    /**
     * Creates a new instance of the parameter cache. All default values from
     * the parameter definitions are predefined in the parameter cache.
     *
     * @param _context      MX context
     * @param _paramDefs    all parameter definitions
     * @see #context
     * @see #mapBoolean
     * @see #mapList
     * @see #mapMap
     * @see #mapString
     */
    public ParameterCache_mxJPO(final Context _context,
                                final Collection<ParameterDef_mxJPO> _paramDefs)
    {
        this.context = _context;
        this.mapBoolean = new HashMap<String,Boolean>();
        this.mapInteger = new HashMap<String,Integer>();
        this.mapList = new HashMap<String,Collection<String>>();
        this.mapMap = new HashMap<String,Map<String,?>>();
        this.mapString = new HashMap<String,String>();

        for (final ParameterDef_mxJPO paramDef : _paramDefs)  {
            if (paramDef.getDefaultValue() != null)  {
                if (paramDef.getType() == ParameterDef_mxJPO.Type.BOOLEAN)  {
                    this.mapBoolean.put(paramDef.getName(),
                                        Boolean.parseBoolean(paramDef.getDefaultValue()));
                } else if (paramDef.getType() == ParameterDef_mxJPO.Type.INTEGER)  {
                    this.mapInteger.put(paramDef.getName(),
                                        Integer.parseInt(paramDef.getDefaultValue()));
                } else if (paramDef.getType() == ParameterDef_mxJPO.Type.LIST)  {
                    this.mapList.put(paramDef.getName(),
                                     new ArrayList<String>(Arrays.asList(paramDef.getDefaultValue().split(","))));
                } else if (paramDef.getType() == ParameterDef_mxJPO.Type.STRING)  {
                    this.mapString.put(paramDef.getName(),
                                       paramDef.getDefaultValue());
                }
            }
        }
    }

    /**
     * Creates a new parameter caches class used for the clone. The new
     * parameter cache instance holds the new defined cache but all already
     * defined value maps {@link #mapBoolean}, {@link #mapList} and
     * {@link #mapString}.
     *
     * @param _context      new matrix context
     * @param _original     original parameter cache class
     * @see #clone()
     * @see #context
     * @see #mapBoolean
     * @see #mapList
     * @see #mapMap
     * @see #mapString
     */
    private ParameterCache_mxJPO(final Context _context,
                                 final ParameterCache_mxJPO _original)
    {
        this.context = _context;
        this.mapBoolean = _original.mapBoolean;
        this.mapInteger = _original.mapInteger;
        this.mapList = _original.mapList;
        this.mapMap = _original.mapMap;
        this.mapString = _original.mapString;
    }

    /**
     * Clones current parameter cache instance. A clone is needed if the MX
     * context is changed (e.g. if the MxUpdate is called within the JPO
     * called).
     *
     * @param _context  new matrix context for the cloned parameter cache
     *                  instance
     * @return new parameter cache instance
     * @see #ParameterCache_mxJPO(Context, ParameterCache_mxJPO)
     */
    public ParameterCache_mxJPO clone(final Context _context)
    {
        return new ParameterCache_mxJPO(_context, this);
    }

    /**
     * Evaluated given parameter and defines the values for given parameter
     * depending on the list of arguments.
     * <ul>
     * <li><b>boolean parameter</b>: the boolean map defines for the parameter
     *     the converted value from the default value</li>
     * <li><b>list parameter</b>: the next argument is added to the value list
     *     </li>
     * <li><b>string parameter</b>: the next argument is defined as string
     *     value</li>
     * </ul>
     *
     * @param _paramDef parameter definition
     * @param _args     list of arguments from the command line
     * @param _index    current index within the list of arguments
     * @return new index of the current index within the list of arguments
     * @see #mapBoolean
     * @see #mapList
     * @see #mapString
     */
    public int evalParameter(final ParameterDef_mxJPO _paramDef,
                             final String[] _args,
                             final int _index)
    {
        int index = _index;

        if (_paramDef.getType() == ParameterDef_mxJPO.Type.BOOLEAN)  {
            this.mapBoolean.put(_paramDef.getName(),
                                !Boolean.parseBoolean(_paramDef.getDefaultValue()));
        } else if (_paramDef.getType() == ParameterDef_mxJPO.Type.INTEGER)  {
            this.mapInteger.put(_paramDef.getName(),
                                Integer.parseInt(_args[++index]));
        } else if (_paramDef.getType() == ParameterDef_mxJPO.Type.LIST)  {
            if (!this.mapList.containsKey(_paramDef.getName()))  {
                this.mapList.put(_paramDef.getName(), new ArrayList<String>());
            }
            this.mapList.get(_paramDef.getName()).add(_args[++index]);
        } else if (_paramDef.getType() == ParameterDef_mxJPO.Type.STRING)  {
            this.mapString.put(_paramDef.getName(), _args[++index]);
        }

        return index;
    }

    /**
     * Evaluated if given key is defined in the value maps.
     *
     * @param _key      searched key
     * @return <i>true</i> if the given key is defined; otherwise <i>false</i>
     *         is returned
     * @see #mapBoolean
     * @see #mapList
     * @see #mapMap
     * @see #mapString
     */
    public boolean contains(final String _key)
    {
        return this.mapBoolean.containsKey(_key)
               || this.mapList.containsKey(_key)
               || this.mapMap.containsKey(_key)
               || this.mapString.containsKey(_key);
    }

    /**
     * Returns the MX context stored in this parameter cache.
     *
     * @return MX context
     * @see #context
     */
    public Context getContext()
    {
        return this.context;
    }

    /**
     * Returns for given key the related boolean value.
     *
     * @param _key  key of searched boolean value
     * @return value of the boolean (or <code>null</code> if for the key no
     *         boolean value is defined)
     * @see #mapBoolean
     */
    public Boolean getValueBoolean(final String _key)
    {
        return this.mapBoolean.get(_key);
    }

    /**
     * Defines for given key a related boolean value.
     *
     * @param _key      key of the boolean value to define
     * @param _value    related value
     * @see #mapBoolean
     */
    public void defineValueBoolean(final String _key,
                                   final Boolean _value)
    {
        this.mapBoolean.put(_key, _value);
    }

    /**
     * Returns for given key the related integer value.
     *
     * @param _key  key of searched integer value
     * @return value of the integer (or <code>null</code> if for the key no
     *         integer value is defined)
     * @see #mapInteger
     */
    public Integer getValueInteger(final String _key)
    {
        return this.mapInteger.get(_key);
    }

    /**
     * Defines for given key a related integer value.
     *
     * @param _key      key of the integer value to define
     * @param _value    related value
     * @see #mapInteger
     */
    public void defineValueInteger(final String _key,
                                   final Integer _value)
    {
        this.mapInteger.put(_key, _value);
    }

    /**
     * Returns for given key the related list value.
     *
     * @param _key  key of searched list value
     * @return list for related key (or <code>null</code> if no list is not
     *         defined)
     * @see #mapList
     */
    public Collection<String> getValueList(final String _key)
    {
        return this.mapList.get(_key);
    }

    /**
     * Returns for given key the related map value.
     *
     * @param <T>       class of the value of the map
     * @param _key      key of searched map value
     * @param _clazz    clazz of the value of the map
     * @return map for related key (or <code>null</code> if map is not defined)
     * @see #mapMap
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String,T> getValueMap(final String _key,
                                         final Class<T> _clazz)
    {
        return (Map<String,T>) this.mapMap.get(_key);
    }

    /**
     * Creates for given key a new map if not exists and returns this map.
     *
     * @param <T>       class of the value of the map
     * @param _key      key of searched / new created map value
     * @param _clazz    clazz of the value of the map
     * @return map for related key
     * @see #mapMap
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String,T> defineValueMap(final String _key,
                                            final Class<T> _clazz)
    {
        if (!this.mapMap.containsKey(_key))  {
            this.mapMap.put(_key, new HashMap<String,T>());
        }
        return (Map<String,T>) this.mapMap.get(_key);
    }

    /**
     * Returns for given key the related string value.
     *
     * @param _key  key of searched string value
     * @return string value for related key (or <code>null</code> if no string
     *         value is defined)
     * @see #mapString
     */
    public String getValueString(final String _key)
    {
        return this.mapString.get(_key);
    }

    /**
     * Logging in error level.
     *
     * @param _text     error text
     */
    public void logError(final String _text)
    {
        System.out.println("ERROR!" + _text);
    }

    /**
     * Logging in warning level.
     *
     * @param _text     warning text
     */
    public void logWarning(final String _text)
    {
        System.out.println("WARNING!" + _text);
    }

    /**
     * Logging in level information.
     *
     * @param _text     info text
     */
    public void logInfo(final String _text)
    {
        System.out.println(_text);
    }

    /**
     * Logging in debug level.
     *
     * @param _text     trace text
     */
    public void logDebug(final String _text)
    {
        System.out.println(_text);
    }

    /**
     * Logging in trace level.
     *
     * @param _text     trace text
     */
    public void logTrace(final String _text)
    {
        System.out.println(_text);
    }

    /**
     * Returns the string representation of the parameter cache. The string
     * representation includes all boolean values, list values and string
     * values.
     *
     * @return string representation of the parameter cache
     * @see #mapBoolean
     * @see #mapList
     * @see #mapMap
     * @see #mapString
     */
    @Override
    public String toString()
    {
        return new StringBuilder()
                .append("[Parameter Cache ")
                    .append("boolean = ").append(this.mapBoolean).append(", ")
                    .append("list = ").append(this.mapList).append(", ")
                    .append("map = ").append(this.mapMap).append(", ")
                    .append("string = ").append(this.mapString)
                .append("]")
                .toString();
    }
}
