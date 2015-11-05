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

package org.mxupdate.typedef;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.AttributeCI_mxJPO;
import org.mxupdate.update.datamodel.Dimension_mxJPO;
import org.mxupdate.update.datamodel.Expression_mxJPO;
import org.mxupdate.update.datamodel.Format_mxJPO;
import org.mxupdate.update.datamodel.Interface_mxJPO;
import org.mxupdate.update.datamodel.PathType_mxJPO;
import org.mxupdate.update.datamodel.Policy_mxJPO;
import org.mxupdate.update.datamodel.Relationship_mxJPO;
import org.mxupdate.update.datamodel.Rule_mxJPO;
import org.mxupdate.update.datamodel.Type_mxJPO;
import org.mxupdate.update.program.Page_mxJPO;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.system.PackageCI_mxJPO;
import org.mxupdate.update.system.UniqueKeyCI_mxJPO;
import org.mxupdate.update.user.Association_mxJPO;
import org.mxupdate.update.user.Group_mxJPO;
import org.mxupdate.update.user.PersonCI_mxJPO;
import org.mxupdate.update.user.Role_mxJPO;
import org.mxupdate.update.userinterface.Channel_mxJPO;
import org.mxupdate.update.userinterface.Command_mxJPO;
import org.mxupdate.update.userinterface.Form_mxJPO;
import org.mxupdate.update.userinterface.Inquiry_mxJPO;
import org.mxupdate.update.userinterface.Menu_mxJPO;
import org.mxupdate.update.userinterface.Portal_mxJPO;
import org.mxupdate.update.userinterface.Table_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MqlBuilder;

import matrix.util.MatrixException;

/**
 * Enumeration for MX admin object used as work arround.
 *
 * @author The MxUpdate Team
 */
public enum EMxAdmin_mxJPO
{
    Association(Association_mxJPO.class)
    {
        @Override public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            final SortedSet<String> ret = new TreeSet<>();
            final String tmp = MqlBuilderUtil_mxJPO.mql().cmd("escape list association").exec(_paramCache.getContext());
            if (!tmp.isEmpty())  {
                ret.addAll(Arrays.asList(tmp.split("\n")));
            }
            return ret;
        }
    },
    Attribute(AttributeCI_mxJPO.class)
    {
        @Override public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            final SortedSet<String> ret = new TreeSet<>();
            final MqlBuilder mql = MqlBuilderUtil_mxJPO.mql().cmd("escape list attribute ").arg("*");
            if (_paramCache.getValueBoolean(ValueKeys.DMAttrSupportsOwner))  {
                // new enovia version => only attribute w/o defined owner...
                mql.cmd(" where ").arg("owner==\"\"");
            }
            final String tmp = mql
                    .cmd(" select ").arg("name")
                    .cmd(" dump")
                    .exec(_paramCache.getContext());
            if (!tmp.isEmpty())  {
                ret.addAll(Arrays.asList(tmp.split("\n")));
            }
            return ret;
        }
    },
    Channel(Channel_mxJPO.class),
    Command(Command_mxJPO.class),
    Dimension(Dimension_mxJPO.class),
    Expression(Expression_mxJPO.class),
    Form(Form_mxJPO.class),
    Format(Format_mxJPO.class),
    Group(Group_mxJPO.class),
    Inquiry(Inquiry_mxJPO.class),
    Interface(Interface_mxJPO.class),
    Menu(Menu_mxJPO.class),
    Package(PackageCI_mxJPO.class),
    Page(Page_mxJPO.class),
    PathType(PathType_mxJPO.class)
    {
        @Override public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            final SortedSet<String> ret = new TreeSet<>();
            final String pathTypeStr = MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape list pathtype ").arg("*").cmd(" ")
                            .cmd("select ").arg("name").cmd(" ").arg("owner").cmd(" ")
                            .cmd("dump ").arg(EMxAdmin_mxJPO.SELECT_KEY)
                            .exec(_paramCache.getContext());
            if (!pathTypeStr.isEmpty())  {
                for (final String nameOwnerStr : pathTypeStr.split("\n"))  {
                    final String[] nameOwnerArr = nameOwnerStr.split(EMxAdmin_mxJPO.SELECT_KEY);
                    if ((nameOwnerArr.length < 2) || nameOwnerArr[1].isEmpty())  {
                        ret.add(nameOwnerArr[0]);
                    }
                }
            }
            return ret;
        }
    },
    Person(PersonCI_mxJPO.class),
    Policy(Policy_mxJPO.class),
    Portal(Portal_mxJPO.class),
    Program(ProgramCI_mxJPO.class),
    Relationship(Relationship_mxJPO.class),
    Role(Role_mxJPO.class),
    Rule(Rule_mxJPO.class),
    Table(Table_mxJPO.class) {
        @Override public String mxClassSuffix()
        {
            return "system";
        }
        @Override public boolean hasMxClassSuffix()
        {
            return true;
        }
        @Override public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            final SortedSet<String> ret = new TreeSet<>();
            final String tmp = MqlBuilderUtil_mxJPO.mql().cmd("escape list table system").exec(_paramCache.getContext());
            if (!tmp.isEmpty())  {
                ret.addAll(Arrays.asList(tmp.split("\n")));
            }
            return ret;
        }
    },
    Type(Type_mxJPO.class),
    UniqueKey(UniqueKeyCI_mxJPO.class) {
        @Override public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            final SortedSet<String> ret = super.evalListWOCache(_paramCache);
            ret.removeAll(_paramCache.getValueList(ValueKeys.SystemUniqueKeyMXSystemUniqueKeys));
            return ret;
        }
    };

    /** Key used for the select statements. */
    private static final String SELECT_KEY = "@@@2@@@2@@@";

    private static final Map<String,EMxAdmin_mxJPO> MAP_FROM_MXCLASS = new HashMap<>();
    static  {
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Association.name().toLowerCase(),   Association);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Attribute.name().toLowerCase(),     Attribute);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Channel.name().toLowerCase(),       Channel);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Command.name().toLowerCase(),       Command);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Dimension.name().toLowerCase(),     Dimension);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Expression.name().toLowerCase(),    Expression);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Form.name().toLowerCase(),          Form);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Format.name().toLowerCase(),        Format);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Group.name().toLowerCase(),         Group);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Inquiry.name().toLowerCase(),       Inquiry);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Interface.name().toLowerCase(),     Interface);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Menu.name().toLowerCase(),          Menu);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Package.name().toLowerCase(),       Package);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Page.name().toLowerCase(),          Page);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(PathType.name().toLowerCase(),      PathType);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Person.name().toLowerCase(),        Person);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Policy.name().toLowerCase(),        Policy);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Portal.name().toLowerCase(),        Portal);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Program.name().toLowerCase(),       Program);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Relationship.name().toLowerCase(),  Relationship);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Role.name().toLowerCase(),          Role);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Rule.name().toLowerCase(),          Rule);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Table.name().toLowerCase(),         Table);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(Type.name().toLowerCase(),          Type);
        EMxAdmin_mxJPO.MAP_FROM_MXCLASS.put(UniqueKey.name().toLowerCase(),     UniqueKey);
    }

    /** Depending class implementing this MX class definition. */
    private final Class<? extends AbstractAdminObject_mxJPO<?>> clazzCI;

    private EMxAdmin_mxJPO(final Class<? extends AbstractAdminObject_mxJPO<?>> _clazzCI)
    {
        this.clazzCI = _clazzCI;
    }

    /**
     * Returns for given {@code _mxClass} name depending MX class definition.
     *
     * @param _mxClass  MX class name
     * @return MX Class definition; {@code null} if not found
     */
    public static EMxAdmin_mxJPO valueOfByClass(final String _mxClass)
    {
        return EMxAdmin_mxJPO.MAP_FROM_MXCLASS.get(_mxClass);
    }

    /**
     * Returns the MX class name for given MX admin object..
     *
     * @return MX class name
     */
    public String mxClass()
    {
        return this.name().toLowerCase();
    }

    /**
     * Returns the suffix for MX class.
     *
     * @return suffix for this MX Class
     */
    public String mxClassSuffix()
    {
        return "";
    }

    /**
     * Returns <i>true</i> if a suffix for MX class exists.
     *
     * @return <i>true</i> if suffix exists; otherwise <i>false</i>
     */
    public boolean hasMxClassSuffix()
    {
        return false;
    }

    /**
     * Checks if the given object with {@code _mxName} already exists in the
     * database.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       MX name
     * @return <i>true</i> if object exists; otherwise <i>false</i>
     * @throws MatrixException if check failed
     */
    public boolean exist(final ParameterCache_mxJPO _paramCache,
                         final String _mxName)
        throws MatrixException
    {
        return this.evalList(_paramCache).contains(_mxName);
    }

    /**
     * Checks if the current list of objects exists and if not evaluates the
     * list of all objects.
     *
     * @param _paramCache   parameter cache
     * @return set of all object names
     * @throws MatrixException if evaluate failed
     */
    public SortedSet<String> evalList(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final SortedSet<String> ret;
        @SuppressWarnings("unchecked")
        Map<EMxAdmin_mxJPO,SortedSet<String>> mxNames = (Map<EMxAdmin_mxJPO,SortedSet<String>>) _paramCache.getCache(CacheKey.MxNames);
        if (mxNames == null)  {
            mxNames = new HashMap<>();
            _paramCache.setCache(CacheKey.MxNames, mxNames);
        }
        if (mxNames.containsKey(this))  {
            ret = mxNames.get(this);
        } else  {
            ret = this.evalListWOCache(_paramCache);
        }
        return ret;
    }

    /**
     * Evaluates the list of all objects.
     *
     * @param _paramCache   parameter cache
     * @return list of all existing objects
     * @throws MatrixException if evaluate failed
     */
    public SortedSet<String> evalListWOCache(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final SortedSet<String> ret = new TreeSet<>();
        final String tmp = MqlBuilderUtil_mxJPO.mql().cmd("escape list ").cmd(this.mxClass()).cmd(" ").arg("*").exec(_paramCache.getContext());
        if (!tmp.isEmpty())  {
            ret.addAll(Arrays.asList(tmp.split("\n")));
        }
        return ret;
    }

    /**
     *
     * @param _mxName   MX name of the new instance
     * @return instance of the administration object used for create, update or
     *                  delete
     * @throws NoSuchMethodException        if the constructor with
     *                                      {@link TypeDef_mxJPO} and
     *                                      {@link String} does not exists
     * @throws InstantiationException       if a new instance of the class
     *                                      {@link #jpoClass} could not be
     *                                      created
     * @throws IllegalAccessException       if the constructor is not public
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoClass} itself throws an
     *                                      exception
     */
    public AbstractAdminObject_mxJPO<?> newTypeInstance(final String _mxName)
        throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        return this.clazzCI.getConstructor(String.class).newInstance(_mxName);
    }
}
