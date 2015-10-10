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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.mxupdate.mapping.AbstractValue_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO;
import org.mxupdate.typedef.export.IExport_mxJPO;
import org.mxupdate.typedef.filenames.IMatcherFileNames_mxJPO;
import org.mxupdate.typedef.mxnames.IMatcherMxNames_mxJPO;
import org.mxupdate.typedef.update.IUpdate_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.CacheKey;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

import matrix.util.MatrixException;

/**
 * Enumeration for administration type definitions.
 *
 * @author The MxUpdate Team
 */
public final class TypeDef_mxJPO
    extends AbstractValue_mxJPO
    implements Comparable<TypeDef_mxJPO>
{
    /** Defines the name of the admin type. */
    private String adminType;
    /** Defines the suffix of the admin type. */
    private String adminSuffix = "";

    /** Must be checked if the business type exists? */
    private boolean busCheckExists = false;
    /** Defines the list of attributes which are automatically ignored within the update. */
    private final Set<String> busIgnoredAttributes = new HashSet<>();
    /** Mapping between internal used type definitions and the MX policy names. */
    private String busPolicy;
    /** Defines the list of both relationships which must be evaluated to write and run of update script. */
    private Collection<String> busRelsBoth;
    /** Defines the list of from relationships which must be evaluated to write and run of update script. */
    private Collection<String> busRelsFrom;
    /** Defines the list of to relationships which must be evaluated to write and run of update script. */
    private Collection<String> busRelsTo;
    /** Mapping between internal used type definitions and the MX type names. */
    private String busType;
    /** If set to <i>true</i> business objects are using types which are derived from the defined {@link #busType business type}. */
    private boolean busTypeDerived = false;
    /** Mapping between internal used type definitions and the MX vault names. */
    private String busVault;

    /** Mapping between internal used type definitions and the file paths. */
    private String filePath;
    /** Mapping between internal used administration type definitions and the file prefixes. */
    private String filePrefix;
    /** Mapping between internal used administration type definitions and the file suffixes. */
    private String fileSuffix;

    /** Name of the mxUpdate kind for this type definition */
    private String mxUpdateKind;
    /** Name of the mxUpdate type for this type definition */
    private String mxUpdateType;

    /** JPO implementing the CI MxUpdate functionality. */
    private Class<? extends AbstractObject_mxJPO<?>> jpoClass;
    /** JPO implementing the export interface. */
    private Class<? extends IExport_mxJPO> jpoExport;
    /** JPO implementing the Match File Names interface. */
    private Class<? extends IMatcherFileNames_mxJPO> jpoMatchFileNames;
    /** JPO implementing the Fetch-Mx-Names interface. */
    private Class<? extends IMatcherMxNames_mxJPO> jpoMatchMxNames;
    /** JPO implementing the Update interface. */
    private Class<? extends IUpdate_mxJPO> jpoUpdate;

    /** Order number used within update. */
    private int orderNo = Integer.MAX_VALUE;

    /** Mapping between internal used administration type definitions and the logging string. */
    private String textLogging;

    /** Mapping between internal used administration type definitions and the titles. */
    private String textTitle;

    /**
     * Defines the values of the type definition.
     *
     * @param _paramCache   parameter cache
     * @param _typeDefMap   type definition map
     * @param _key          key of the type definition (including the name of
     *                      type definition and the kind of the type definition
     *                      separated by a point)
     * @param _value        value of the related value
     * @throws Exception if the values could not be defined or the JPO names
     *                   could not be extracted
     * @see Mapping_mxJPO#getTypeDefMap()
     * @see #defineJPOClass(ParameterCache_mxJPO, Mapping_mxJPO, String)
     */
    @SuppressWarnings("unchecked")
    public static void defineValue(final ParameterCache_mxJPO _paramCache,
                                   final Map<String,TypeDef_mxJPO> _typeDefMap,
                                   final String _key,
                                   final String _value)
        throws Exception
    {
        final String enumName = _key.replaceAll("\\..*", "");
        final String key = _key.substring(enumName.length() + 1);

        TypeDef_mxJPO typeDef = _typeDefMap.get(enumName);
        if (typeDef == null)  {
            typeDef = new TypeDef_mxJPO(enumName);
            _typeDefMap.put(enumName, typeDef);
        }

        Prefix prefix;
        try  {
            prefix = Prefix.valueOf(key);
        } catch (final IllegalArgumentException e)  {
            prefix = null;
        }

        if (prefix == null)  {
            typeDef.defineValues(key, _value);
        } else  {
            switch (prefix)  {
                case AdminSuffix:           typeDef.adminSuffix = _value;break;
                case AdminType:             typeDef.adminType = _value;break;

                case BusCheckExists:        typeDef.busCheckExists = _value.equalsIgnoreCase("true");break;
                case BusIgnoreAttributes:   typeDef.busIgnoredAttributes.addAll(Arrays.asList(_value.split(",")));break;
                case BusRelsBoth:           typeDef.busRelsBoth = Arrays.asList(_value.split(","));break;
                case BusRelsFrom:           typeDef.busRelsFrom = Arrays.asList(_value.split(","));break;
                case BusRelsTo:             typeDef.busRelsTo = Arrays.asList(_value.split(","));break;
                case BusPolicy:             typeDef.busPolicy = _value;break;
                case BusType:               typeDef.busType = _value;break;
                case BusTypeDerived:        typeDef.busTypeDerived = Boolean.valueOf(_value);break;
                case BusVault:              typeDef.busVault = _value;break;

                case FilePath:              typeDef.filePath = _value;break;
                case FilePrefix:            typeDef.filePrefix = _value;break;
                case FileSuffix:            typeDef.fileSuffix = _value;break;

                case MxUpdateKind:          typeDef.mxUpdateKind = _value;break;
                case MxUpdateType:          typeDef.mxUpdateType = _value;break;

                case JPO:                   typeDef.jpoClass            = (Class<? extends AbstractObject_mxJPO<?>>) TypeDef_mxJPO.fetchJPOClass(_paramCache, _value);break;
                case JpoExport:             typeDef.jpoExport           = (Class<? extends IExport_mxJPO>)           TypeDef_mxJPO.fetchJPOClass(_paramCache, _value);break;
                case JpoMatchFileNames:     typeDef.jpoMatchFileNames   = (Class<? extends IMatcherFileNames_mxJPO>) TypeDef_mxJPO.fetchJPOClass(_paramCache, _value);break;
                case JpoMatchMxNames:       typeDef.jpoMatchMxNames     = (Class<? extends IMatcherMxNames_mxJPO>)   TypeDef_mxJPO.fetchJPOClass(_paramCache, _value);break;
                case JpoUpdate:             typeDef.jpoUpdate           = (Class<? extends IUpdate_mxJPO>)           TypeDef_mxJPO.fetchJPOClass(_paramCache, _value);break;

                case OrderNo:               typeDef.orderNo = Integer.parseInt(_value);break;

                case TextLogging:           typeDef.textLogging = _value;break;
                case TextTitle:             typeDef.textTitle = _value;break;
                default:                    typeDef.defineValues(key, _value);break;
            }
        }
    }

    /**
     * Defines the class for given JPO name.
     *
     * @param _paramCache   parameter cache
     * @param _jpoName      name of searched JPO
     * @throws Exception if the list of MxUdpate JPOs could not evaluated or
     *                   if the related class could not be found
     * @see #MQL_LISTPROG
     * @see #jpoClass
     */
    private static Class<?> fetchJPOClass(final ParameterCache_mxJPO _paramCache,
                                          final String _jpoName)
        throws Exception
    {
        Class<?> ret;
        try  {
            // evaluate as standard Java class
            ret = Class.forName(_jpoName + "_mxJPO");
        } catch (final ClassNotFoundException e)  {
            // evaluate as JPO
            @SuppressWarnings("unchecked")
            Map<String,String> jpos = (Map<String,String>) _paramCache.getCache(CacheKey.TypeDefJPOs);

            if (jpos == null)  {
                jpos = new HashMap<>();
                _paramCache.setCache(CacheKey.TypeDefJPOs, jpos);

                final String tmp = MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape list program ").arg("org.mxupdate.*")
                        .cmd(" select ").arg("name").cmd(" ").arg("classname")
                        .cmd(" dump ").arg("\t").exec(_paramCache);

                for (final String line : tmp.split("\n"))  {
                    final String[] arr = line.split("\t");
                    if (arr.length > 1)  {
                        jpos.put(arr[0], arr[1]);
                    }
                }
            }
            final String jpoClassName = jpos.get(_jpoName);
            if (jpoClassName == null)  {
                throw new Exception("unknown jpo class definition for " + _jpoName);
            }

            ret = Class.forName(jpoClassName);
        }
        return ret;
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
     * @param _paramCache   parameter cache with MX context
     * @return <i>true</i> if the business type exists; otherwise <i>false</i>
     * @throws MatrixException if the check failed
     */
    public boolean existsBusType(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        final String tmp = MqlBuilderUtil_mxJPO.mql() .cmd("escape list type ").arg(this.busType).exec(_paramCache);
        return (tmp.length() > 0);
    }

    /**
     * Returns the set of attributes for business object which are ignored for
     * the update (means this attributes are not reseted). The method returns
     * only correct values if the initialize method was called!
     *
     * @return MX name of the business type definition
     */
    public Set<String> getMxBusIgnoredAttributes()
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
     * Returns if business objects are using types which are derived from given
     * {@link #busType business type}.
     *
     * @return <i>true</i> if business objects are using derived business
     *          types; otherwise <i>false</i>
     * @see #busTypeDerived
     */
    public boolean hasMxBusTypeDerived()
    {
        return this.busTypeDerived;
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
     * Returns the related {@link #filePath file path}. The method returns only
     * correct values if the initialize method was called!
     *
     * @return file path of the administration type definition
     */
    public String getFilePath()
    {
        return this.filePath;
    }

    /**
     * Returns the related {@link #filePrefix file name prefix}. The method
     * returns only correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     */
    public String getFilePrefix()
    {
        return this.filePrefix;
    }

    /**
     * Returns the related {@link #fileSuffix file name suffix}. The method
     * returns only correct values if the initialize method was called!
     *
     * @return file name prefix of the administration type definition
     */
    public String getFileSuffix()
    {
        return this.fileSuffix;
    }

    /**
     * Returns the related {@link #mxUpdateKind MxUpdate kind}. The method
     * returns only correct values if the initialize method was called!
     *
     * @return MxUpdate kind of the administration type definition
     */
    public String getMxUpdateKind()
    {
        return this.mxUpdateKind;
    }

    /**
     * Returns the related {@link #mxUpdateType MxUpdate type}. The method
     * returns only correct values if the initialize method was called!
     *
     * @return MxUpdate type of the administration type definition
     */
    public String getMxUpdateType()
    {
        return this.mxUpdateType;
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
    public AbstractObject_mxJPO<?> newTypeInstance(final String _mxName)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        final AbstractObject_mxJPO<?> ret;
        final EMxAdmin_mxJPO mxClassDef = EMxAdmin_mxJPO.valueOfByClass(this.mxUpdateType);
        if (mxClassDef == null)  {
            ret = this.jpoClass.getConstructor(TypeDef_mxJPO.class, String.class).newInstance(this, _mxName);
        } else  {
            ret = mxClassDef.newTypeInstance(_mxName);
        }
        return ret;
    }

    /**
     * Exports given admin object.
     *
     * @param _paramCache   parameter cache
     * @param _files        files to test
     * @param _matches      matches to fulfill
     * @return map of MX names and depending files for this type definition
     * @throws ParseException               if parse failed
     * @throws MatrixException              if fetch failed
     * @throws IOException                  if write failed
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoExport} itself
     *                                      throws an exception
     * @throws IllegalAccessException       if the constructor is not public
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoExport} itself
     *                                      throws an exception
     * @throws NoSuchMethodException        if the constructor does not exists
     */
    public void export(final ParameterCache_mxJPO _paramCache,
                       final String _mxName,
                       final File _path)
        throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, MatrixException, ParseException
    {
        this.jpoExport.getConstructor().newInstance().export(_paramCache, this, _mxName, _path);
    }

    /**
     * Matches all file names to MX names for this type definition.
     *
     * @param _paramCache   parameter cache
     * @param _files        files to test
     * @param _matches      matches to fulfill
     * @return map of MX names and depending files for this type definition
     * @throws NoSuchMethodException        if the constructor does not exists
     * @throws InstantiationException       if a new instance of the class
     *                                      {@link #jpoMatchMxNames} could not
     *                                      be created
     * @throws IllegalAccessException       if the constructor is not public
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoMatchMxNames} itself
     *                                      throws an exception
     * @throws UpdateException_mxJPO        if match failed
     */
    public SortedMap<String,File> matchFileNames(final ParameterCache_mxJPO _paramCache,
                                                 final Collection<File> _files,
                                                 final Collection<String> _matches)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UpdateException_mxJPO
    {
        return this.jpoMatchFileNames.getConstructor().newInstance().match(_paramCache, this, _files, _matches);
    }

    /**
     * Matches all file names to MX names for this type definition.
     *
     * @param _paramCache   parameter cache
     * @param _files        files to test
     * @param _matches      matches to fulfill
     * @return map of MX names and depending files for this type definition
     * @throws NoSuchMethodException        if the constructor does not exists
     * @throws InstantiationException       if a new instance of the class
     *                                      {@link #jpoMatchMxNames} could not
     *                                      be created
     * @throws IllegalAccessException       if the constructor is not public
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoMatchMxNames} itself
     *                                      throws an exception
     * @throws UpdateException_mxJPO        if match failed
     */
    public SortedMap<String,File> matchFileNames(final ParameterCache_mxJPO _paramCache,
                                                 final Collection<File> _files)
        throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, UpdateException_mxJPO
    {
        return this.jpoMatchFileNames.getConstructor().newInstance().match(_paramCache, this, _files, null);
    }

    /**
     * Fetches all MX names for this type definition for given {@code _matches}.
     *
     * @param _paramCache   parameter cache
     * @param _matches      matches to fulfill
     * @return set of MX names for this type definition
     * @throws NoSuchMethodException        if the constructor does not exists
     * @throws InstantiationException       if a new instance of the class
     *                                      {@link #jpoMatchMxNames} could not
     *                                      be created
     * @throws IllegalAccessException       if the constructor is not public
     * @throws InvocationTargetException    if the constructor of the
     *                                      {@link #jpoMatchMxNames} itself
     *                                      throws an exception
     * @throws MatrixException              if fetch of Mx names failed
     */
    public SortedSet<String> matchMxNames(final ParameterCache_mxJPO _paramCache,
                                          final Collection<String> _matches)
        throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, MatrixException
    {
        return this.jpoMatchMxNames.getConstructor().newInstance().match(_paramCache, this, _matches);
    }

    /**
     * Updates given CI object with defined file.
     *
     * @param _paramCache   parameter cache
     * @param _create       first run and the object is new created
     * @param _mxName       MX name
     * @param _file         file to update
     * @throws Exception if update failed
     */
    public void update(final ParameterCache_mxJPO _paramCache,
                       final boolean _create,
                       final String _mxName,
                       final File _file)
       throws Exception
    {
        this.jpoUpdate.getConstructor().newInstance().update(_paramCache, this, _create, _mxName, _file);
    }

    /**
     * Compares this type definition with the <code>_other</code> type
     * definition. First the {@link #orderNo order number} is compared. If the
     * {@link #orderNo order number} is equal, the {@link #getName() name} is
     * compared.
     *
     * @param _other    other type definition to compare
     * @return negative integer, zero, or positive integer as this object is
     *         less than, equal to, or greater than the specified object
     */
    @Override()
    public int compareTo(final TypeDef_mxJPO _other)
    {
        int ret = Integer.valueOf(this.orderNo).compareTo(_other.orderNo);
        if (ret == 0)  {
            ret = this.getName().compareTo(_other.getName());
        }
        return ret;
    }

    /**
     * All prefix defined the mapping file.
     */
    enum Prefix
    {
        /** Used prefix of admin type suffix. */
        AdminSuffix,
        /** Used prefix of the admin type name. */
        AdminType,

        /** Used prefix of ignored attributes for business objects within the property file. */
        BusIgnoreAttributes,
        /** Used prefix of check exists definitions within the property file. */
        BusCheckExists,
        /** Used prefix of policy definitions within the property file. */
        BusPolicy,
        /** Used prefix of both relationship definitions within the property file. */
        BusRelsBoth,
        /** Used prefix of from relationship definitions within the property file. */
        BusRelsFrom,
        /** Used prefix of to relationship definitions within the property file. */
        BusRelsTo,
        /** Used prefix of business type definitions within the property file. */
        BusType,
        /** Used prefix of business type derived definitions within the property file. */
        BusTypeDerived,
        /** Used prefix of vault definitions within the property file. */
        BusVault,

        /** Used prefix of type definitions within the property file. */
        FilePath,
        /** Used prefix of file prefix definitions within the property file. */
        FilePrefix,
        /** Used prefix of file suffix definitions within the property file. */
        FileSuffix,

        /** Used prefix of mxupdate kind definitions within the property file. */
        MxUpdateKind,
        /** Used prefix of mxupdate type definitions within the property file. */
        MxUpdateType,

        /** Used prefix of the JPO name. */
        JPO,
        /** Used prefix of the JPO names to export as file. */
        JpoExport,
        /** Used prefix of the JPO names to match file names. */
        JpoMatchFileNames,
        /** Used prefix of the JPO names to fetch MX names. */
        JpoMatchMxNames,
        /** Used prefix of the JPO name to update. */
        JpoUpdate,

        /** Used logging text of type definitions within the property file. */
        TextLogging,
        /** Used title of type definitions within the property file. */
        TextTitle,

        /** Used order number within update. */
        OrderNo;
    }
}
