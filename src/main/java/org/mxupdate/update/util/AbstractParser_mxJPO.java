/*
 * Copyright 2008-2014 The MxUpdate Team
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
            final Field field = this.getField(_object, _fieldName).field;
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
            final Field field = this.getField(_object, _fieldName).field;
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
            final Field field = this.getField(_object, _fieldName).field;
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
     * Sets the new <code>_values</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _values       new values of the field
     */
    @SuppressWarnings("unchecked")
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Collection<?> _values)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                final Collection<Object> set = (Collection<Object>) field.get(_object);
                set.addAll(_values);
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
            final Field field = this.getField(_object, _fieldName).field;
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
     * Returns the new value for field {@code _fieldName} of {@code _object}.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @return found value
     */
    protected Object getValue(final Object _object,
                              final String _fieldName)
    {
        final Object ret;
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                ret = field.get(_object);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
        return ret;
    }

    /**
     * Searches for given name the field within the object.
     *
     * @param _object       object where the field is searched
     * @param _fieldNames   path of searched fields
     * @return found field with related object
     */
    protected FieldObject getField(final Object _object,
                                   final String... _fieldNames)
    {
        FieldObject ret = new FieldObject();
        ret.object = _object;
        Class<?> clazz = _object.getClass();
        try  {
            ret.field = clazz.getDeclaredField(_fieldNames[0]);
        } catch (final NoSuchFieldException e)  {
        }
        while ((ret.field == null) && (clazz != null))  {
            clazz = clazz.getSuperclass();
            if (clazz != null)  {
                try  {
                    ret.field = clazz.getDeclaredField(_fieldNames[0]);
                } catch (final NoSuchFieldException e)  {
                }
            }
        }
        if ((_fieldNames.length > 1) && (ret.field != null))  {
            final boolean accessible = ret.field.isAccessible();
            final Object object;
            try  {
                ret.field.setAccessible(true);
                 object = ret.field.get(_object);
            } catch (final IllegalAccessException e)  {
                throw new ParseUpdateError(e);
            } finally  {
                ret.field.setAccessible(accessible);
            }
            final String[] newFieldNames = new String[_fieldNames.length - 1];
            System.arraycopy(_fieldNames, 1, newFieldNames, 0, _fieldNames.length - 1);
            ret = this.getField(object, newFieldNames);
        }
        return ret;
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
    public static class ParseUpdateError
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

    /**
     * Class used to store depending on a field the related object.
     *
     * @see AbstractParser_mxJPO#getField(Object, String...)
     */
    protected static class FieldObject
    {
        /**
         * Field.
         */
        private Field field;

        /**
         * Object.
         */
        private Object object;

        /**
         * Returns the field.
         *
         * @return field
         * @see #field
         */
        public Field getField()
        {
            return this.field;
        }

        /**
         * Returns current value of {@link #field} within {@link #object}.
         *
         * @param <T>   type of value
         * @return current value
         */
        @SuppressWarnings("unchecked")
        public <T> T get()
        {
            final Object ret;
            try  {
                final boolean accessible = this.field.isAccessible();
                try  {
                    this.field.setAccessible(true);
                    ret = this.field.get(this.object);
                } finally  {
                    this.field.setAccessible(accessible);
                }
            } catch (final Exception e)  {
                throw new AbstractParser_mxJPO.ParseUpdateError(e);
            }
            return (T) ret;
        }

        /**
         * Defines new value for given {@link #field} on given {@link #object}.
         *
         * @param <T>       type of the value
         * @param _value    value itself
         */
        public <T> void set(final T _value)
        {
            try  {
                final boolean accessible = this.field.isAccessible();
                try  {
                    this.field.setAccessible(true);
                    this.field.set(this.object, _value);
                } finally  {
                    this.field.setAccessible(accessible);
                }
            } catch (final Exception e)  {
                throw new AbstractParser_mxJPO.ParseUpdateError(e);
            }
        }
    }
}
