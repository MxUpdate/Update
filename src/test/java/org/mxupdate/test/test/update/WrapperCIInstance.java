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
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.mxupdate.test.data.AbstractData;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.BusObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Wrapper for CI instances.
 *
 * @author The MxUpdate Team
 * @param <DATA> class of the CI data
 */
public class WrapperCIInstance<DATA extends AbstractObject_mxJPO<?>>
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
        this.data.parseUpdate(new File("dummy.txt").getAbsoluteFile(), _ciCode);
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
        this.data.parseUpdate(new File("dummy.txt").getAbsoluteFile(), this.strip(_ciCode));
    }

    /**
     * Parses the ci file of {@code _data} instance.
     *
     * @param _data     data instance as target parse definition
     * @throws Exception if parse failed
     */
    public void parseUpdate(final AbstractData<?> _data)
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
    public String strip(final String _generated)
    {
        final String ret;
        final String startAdmIndex = " \"${NAME}\" {";
        final String startBusIndex = " \"${NAME}\" \"${REVISION}\" {";

        if (_generated.indexOf(startAdmIndex) > 0)  {
            final int start = _generated.indexOf(startAdmIndex) + startAdmIndex.length() + 1;
            final int end = _generated.length() - 2;
            ret = (start < end) ? _generated.substring(start, end) : "";
        } else if (_generated.indexOf(startBusIndex) > 0)  {
            final int start = _generated.indexOf(startBusIndex) + startBusIndex.length() + 1;
            final int end = _generated.length() - 2;
            ret = (start < end) ? _generated.substring(start, end) : "";
        } else  {
            ret = _generated;
        }
        return ret;
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
        this.data.parse(_paramCache);
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
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO("", _paramCache);
        updateBuilder.childStart("mxUpdate \"${NAME}\"");
        this.data.writeUpdate(updateBuilder);
        updateBuilder.childEnd();
        return updateBuilder.toString().substring(MessageFormat.format(_paramCache.getValueString(ValueKeys.ExportFileHeader), "").length());
    }

    /**
     * Writes this instance as CI update w/o mxUpdate call.
     *
     * @param _paramCache   parameter cache
     * @return written string
     * @throws Exception if write failed
     */
    public String writeWOMxUpdateCall(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO("", _paramCache);
        this.data.writeUpdate(updateBuilder);
        return updateBuilder.toString().substring(MessageFormat.format(_paramCache.getValueString(ValueKeys.ExportFileHeader), "").length());
    }

    /**
     * Calculates the delta.
     *
     * @param _paramCache   parameter cache
     * @param _file         file to update
     * @param _current      current instance
     * @throws Exception if calculation failed
     */
    public MultiLineMqlBuilder calcDelta(final ParameterCache_mxJPO _paramCache,
                                         final File _file,
                                         final WrapperCIInstance<DATA> _current)
        throws Exception
    {
        final MultiLineMqlBuilder ret;

        // initialize MQL builder depending on the type
        if (this.data instanceof AbstractAdminObject_mxJPO<?>)  {
            final EMxAdmin_mxJPO mxClassDef = ((AbstractAdminObject_mxJPO<?>) this.data).mxClassDef();
            if (mxClassDef.hasMxClassSuffix())  {
                ret = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod " + mxClassDef.mxClass() + " $1 " + mxClassDef.mxClassSuffix(), _current.data.getName());
            } else  {
                ret = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod " + mxClassDef.mxClass() + " $1", this.data.getName());
            }
        } else  {
            final BusObject_mxJPO bus = (BusObject_mxJPO) this.data;
            ret = MqlBuilderUtil_mxJPO.multiLine(_file, "escape mod bus $1 $2 $3", bus.getBusType(), bus.getBusName(), bus.getBusRevision());
        }

        // casting is work-arround so that calc delta method can be called
        ((AbstractObject_mxJPO) this.data).calcDelta(_paramCache, ret, _current.data);

        return ret;
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
        final WrapperCIInstance<DATA> newInstance = this.newInstance();
        newInstance.parse(_paramCache);
        this.calcDelta(_paramCache, _file, newInstance).exec(_paramCache.getContext());
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
        WrapperCIInstance<DATA> ret = null;
        try {
            ret = new WrapperCIInstance<>((DATA) this.data.getClass().getConstructor(String.class).newInstance(this.data.getName()));
        } catch (final NoSuchMethodException e)  {
            // data class is a business object
            ret = new WrapperCIInstance<>((DATA) this.data.getClass().getConstructor(TypeDef_mxJPO.class, String.class).newInstance(((BusObject_mxJPO) this.data).getTypeDef(), this.data.getName()));
        }

        return ret;
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
}
