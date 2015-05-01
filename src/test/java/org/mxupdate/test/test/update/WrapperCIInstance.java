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

package org.mxupdate.test.test.update;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * Wrapper for CI instances.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the CI data
 */
public class WrapperCIInstance<DATA extends AbstractAdminObject_mxJPO<?>>
{
    /** Data instance to wrap. */
    private final DATA data;

    public WrapperCIInstance(final DATA _data)
    {
        this.data = _data;
    }

    /**
     * Parses the given {@code _ciCode}.
     *
     * @param _ciCode   ci code to parse
     * @throws Exception if parse failed
     */
    public void parseUpdateWOStrip(final String _ciCode)
        throws Exception
    {
        this.data.parseUpdate(_ciCode);
    }

    /**
     * Parses the given {@code _ciCode}.
     *
     * @param _ciCode   ci code to parse
     * @throws Exception if parse failed
     */
    public void parseUpdate(final String _ciCode)
        throws Exception
    {
        this.data.parseUpdate(this.strip(_ciCode));
    }

    /**
     * Parses the ci file of {@code _data} instance.
     *
     * @param _data     data instance as target parse definition
     * @throws Exception if parse failed
     */
    public void parseUpdate(final AbstractAdminData<?> _data)
        throws Exception
    {
        this.parseUpdate(_data.ciFile());
    }

    /**
     * Strips the update code.
     *
     * @param _generated    code to clean
     * @return stripped update code
     */
    protected String strip(final String _generated)
    {
        final String startIndex = "mxUpdate " + this.getTypeDef().getMxAdminName() + " \"${NAME}\" {";
        final int start = _generated.indexOf(startIndex) + startIndex.length() + 1;
        final int end = _generated.length() - 2;
        return (start < end)  ? _generated.substring(start, end) : "";
    }

    /**
     * Evaluates the data from MX.
     *
     * @param _paramCache   parameter cache with context
     * @throws Exception if failed
     */
    public void parse(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final Method write = this.evalMethod("parse", ParameterCache_mxJPO.class);
        write.setAccessible(true);
        try {
            write.invoke(this.data, _paramCache);
        } finally  {
            write.setAccessible(false);
        }
    }

    /**
     * Creates the given object in MX.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create failed
     */
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        this.data.create(_paramCache);
    }

    /**
     * Writes this instance as CI update.
     *
     * @param _paramCache   parameter cache
     * @return written string
     * @throws Exception if write failed
     */
    public String write(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder generated = new StringBuilder();
        final Method write = this.evalMethod("write", ParameterCache_mxJPO.class, Appendable.class);
        write.setAccessible(true);
        try {
            write.invoke(this.data, _paramCache, generated);
        } finally  {
            write.setAccessible(false);
        }
        return generated.toString();
    }

    /**
     * Calculates the delta.
     *
     * @param _paramCache   parameter cache
     * @param _mql          MQL builder
     * @param _current      current instance
     * @throws Exception if calculation failed
     */
    public void calcDelta(final ParameterCache_mxJPO _paramCache,
                          final MultiLineMqlBuilder _mql,
                          final WrapperCIInstance<DATA> _current)
        throws Exception
    {
        Method write = this.evalMethod("calcDelta", ParameterCache_mxJPO.class, MultiLineMqlBuilder.class, this.data.getClass());
        // if not found, try with parent class as parameter
        // (e.g. attributes works then...)
        Class<?> clazz = this.data.getClass().getSuperclass();
        while ((write == null) && (clazz != null))  {
            write = this.evalMethod("calcDelta", ParameterCache_mxJPO.class, MultiLineMqlBuilder.class, clazz);
            clazz = clazz.getSuperclass();
        }
        write.setAccessible(true);
        try {
            write.invoke(this.data, _paramCache, _mql, _current.data);
        } finally  {
            write.setAccessible(false);
        }
    }

    /**
     * Updates this wrapper instance to target instance.
     *
     * @param _file         file used to update
     * @param _paramCache   parameter cache
     * @throws Exception if update failed
     */
    public void store(final File _file,
                      final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final MultiLineMqlBuilder mql;
        if ((this.data.getTypeDef().getMxAdminSuffix()) != null && !this.data.getTypeDef().getMxAdminSuffix().isEmpty())  {
            mql = MqlBuilder_mxJPO.multiLine(_file, "escape mod " + this.data.getTypeDef().getMxAdminName() + " $1 " + this.data.getTypeDef().getMxAdminSuffix(), this.data.getName());
        } else  {
            mql = MqlBuilder_mxJPO.multiLine(_file, "escape mod " + this.data.getTypeDef().getMxAdminName() + " $1", this.data.getName());
        }

        final WrapperCIInstance<DATA> newInstance = this.newInstance();
        newInstance.parse(_paramCache);

        this.calcDelta(_paramCache, mql, newInstance);

        mql.exec(_paramCache);
    }

    /**
     * Initialize new empty instance.
     *
     * @return new instance
     * @throws Exception if initialized failed
     */
    @SuppressWarnings("unchecked")
    private WrapperCIInstance<DATA> newInstance()
        throws Exception
    {
        Constructor<?> constr = null;
        try {
            constr = this.data.getClass().getConstructor(TypeDef_mxJPO.class, String.class);
        } catch (final NoSuchMethodException e)  {
            // data class is an anonymoous class => use super class!
            constr = this.data.getClass().getSuperclass().getConstructor(TypeDef_mxJPO.class, String.class);
        }

        return new WrapperCIInstance<DATA>((DATA) constr.newInstance(this.data.getTypeDef(), this.data.getName()));
    }

    /**
     * Searches for method for given parameters.
     *
     * @param name              name of searched method
     * @param parameterTypes    parameter types
     * @return found method; {@code null} if not found
     */
    private Method evalMethod(final String name,
                              final Class<?>... parameterTypes)
    {
        Method ret = null;
        Class<?> clazz = this.data.getClass();
        try  {
            ret = clazz.getDeclaredMethod(name, parameterTypes);
        } catch (final NoSuchMethodException e)  {
        }

        while ((ret == null) && (clazz != null))  {
            clazz = clazz.getSuperclass();
            if (clazz != null)  {
                try  {
                    ret = clazz.getDeclaredMethod(name, parameterTypes);
                } catch (final NoSuchMethodException e)  {
                }
            }
        }
        return ret;
    }

    /**
     * Returns the type definition instance.
     *
     * @return type definition enumeration
     */
    public final TypeDef_mxJPO getTypeDef()
    {
        return this.data.getTypeDef();
    }
}
