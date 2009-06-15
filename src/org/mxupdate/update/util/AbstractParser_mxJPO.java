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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.mxupdate.update.AbstractAdminObject_mxJPO;

/**
 * The class is used to define common methods for parsers within updates.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractParser_mxJPO
{
    /**
     * Calls for the administration object <code>_object</code> the prepare
     * method.
     *
     * @param _paramCache   parameter cache
     * @param _object       cache for which the prepare method must be called
     */
    protected void prepareObject(final ParameterCache_mxJPO _paramCache,
                                 final AbstractAdminObject_mxJPO _object)
    {
        try {
            final Method method = _object.getClass().getDeclaredMethod("prepare", ParameterCache_mxJPO.class);
            try  {
                method.setAccessible(true);
                method.invoke(_object, _paramCache);
            } finally  {
                method.setAccessible(false);
            }
        } catch (final IllegalArgumentException e) {
            throw new ParseUpdateError(e);
        } catch (final IllegalAccessException e) {
            throw new ParseUpdateError(e);
        } catch (final InvocationTargetException e) {
            throw new ParseUpdateError(e);
        } catch (final SecurityException e) {
            throw new ParseUpdateError(e);
        } catch (final NoSuchMethodException e) {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final String _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName);
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Double _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName);
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Boolean _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName);
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Appends for field with <code>_fieldName</code> the <code>_value</code>
     * for object <code>_object</code>.
     *
     * @param _object       object
     * @param _fieldName    name of the field
     * @param _value        value to append
     */
    @SuppressWarnings("unchecked")
    protected void appendValue(final Object _object,
                               final String _fieldName,
                               final Object _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName);
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                final Collection<Object> collection = (Collection<Object>) field.get(_object);
                collection.add(_value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Searches for given name the field within the object.
     *
     * @param _object       object where the field is searched
     * @param _fieldName    name of searched field
     * @return found field
     */
    protected Field getField(final Object _object,
                             final String _fieldName)
    {
        Class<?> clazz = _object.getClass();
        Field field = null;
        try  {
            field = clazz.getDeclaredField(_fieldName);
        } catch (final NoSuchFieldException e)  {
        }
        while ((field == null) && (clazz != null))  {
            clazz = clazz.getSuperclass();
            if (clazz != null)  {
                try  {
                    field = clazz.getDeclaredField(_fieldName);
                } catch (final NoSuchFieldException e)  {
                }
            }
        }
        return field;
    }

    /**
     * Extracts from the parsed string the related Java string (without quotes,
     * backslashes etc.).
     *
     * @param _token    string token
     * @return extracted string
     */
    protected String getString(final String _token)
    {
        return _token
            .replaceAll("^\"", "")
            .replaceAll("\"$", "")
            .replaceAll("\\\\\\\"", "\"")
            .replaceAll("\\\\\\{", "{")
            .replaceAll("\\\\\\}", "}");
    }

    /**
     * Extracts from the parsed single string the related Java string.
     *
     * @param _token    single string token
     * @return extracted single string
     */
    protected String getSingle(final String _token)
    {
        return _token.replaceAll("\\\\\\\"", "\"");
    }

    /**
     * The error is thrown if the object which is currently read could not
     * updated.
     */
    public class ParseUpdateError
        extends Error
    {
        /**
         * Defines the serialize version unique identifier.
         */
        private static final long serialVersionUID = -7688744873954882911L;

        /**
         * Default constructor of the parse update error with a
         * <code>_cause</code>.
         *
         * @param _cause    cause of the parse update error
         */
        public ParseUpdateError(final Throwable _cause)
        {
            super(_cause);
        }
    }
}
