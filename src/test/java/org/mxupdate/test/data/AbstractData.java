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

package org.mxupdate.test.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.util.Version;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.testng.Assert;

/**
 * Defines common information from administration objects used to create,
 * update and check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 */
public abstract class AbstractData<DATA extends AbstractData<?>>
{
    /** Related test case where this data piece was created. */
    private final AbstractTest test;

    /** Related configuration item of this data piece. */
    private final AbstractTest.CI ci;

    /** Name of the data piece. */
    private final String name;

    /** Values of this data piece. */
    private final Values values = new Values();

    /** Flag to indicate that this data piece is created.*/
    private boolean created;

    /** Set of versions where this definition is NOT supported. */
    private final Set<Version> notSupportedVersions = new HashSet<Version>();

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     */
    protected AbstractData(final AbstractTest _test,
                           final AbstractTest.CI _ci,
                           final String _name)
    {
        this.test = _test;
        this.ci = _ci;
        if ((_ci != null) && (_ci.getBusType() != null) && (_ci.getMxType() == null))  {
            this.name = _name;
        } else  {
            this.name = (_name != null) ? AbstractTest.PREFIX + _name : null;
        }
    }

    /**
     * Returns related test case with the MQL console.
     *
     * @return related test case
     * @see #test
     */
    protected AbstractTest getTest()
    {
        return this.test;
    }

    /**
     * Returns related configuration item type.
     *
     * @return configuration item type
     * @see #ci
     */
    public AbstractTest.CI getCI()
    {
        return this.ci;
    }

    /**
     * Returns the name of the abstract data element.
     *
     * @return name of the abstract data element
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Defines a new value entry which is put into {@link #values}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this original data instance
     * @see #values
     */
    @SuppressWarnings("unchecked")
    public DATA setValue(final String _key,
                         final Object _value)
    {
        this.values.put(_key, _value);
        return (DATA) this;
    }

    /**
     * Returns the value for given key from the {@link #values} map.
     *
     * @param _key      name of the searched key
     * @return value for related <code>_key</code>; if not found
     *         <code>null</code>
     */
    public Object getValue(final String _key)
    {
        return this.values.get(_key);
    }

    /**
     * Returns all defined {@link #values}.
     *
     * @return defined values
     */
    public Values getValues()
    {
        return this.values;
    }

    /**
     * Returns the name of the configuration item file.
     *
     * @return name of the CI file
     */
    public String getCIFileName()
    {
        final char[] charName = this.name.toCharArray();
        final StringBuilder fileName = new StringBuilder().append(this.getCI().filePrefix);
        for (final char ch : charName) {
            if (ch == '@')  {
                fileName.append("@@");
            } else if (((ch < '(') || (ch > ')'))
                    && ((ch < '+') || (ch > '.'))
                    && ((ch < '0') || (ch > '9'))
                    && ((ch < 'A') || (ch > 'Z'))
                    && ((ch < 'a') || (ch > 'z'))
                    && (ch != ' ') && (ch != '=') && (ch != '_'))  {

                final String hex = String.valueOf(Integer.toHexString(ch));
                fileName.append('@');
                switch (hex.length())  {
                    case 1:
                        fileName.append('0').append(hex);
                        break;
                    case 3:
                        fileName.append("u0").append(hex);
                        break;
                    case 4:
                        fileName.append('u').append(hex);
                        break;
                    default:
                        fileName.append(hex);
                        break;
                }
            } else  {
                fileName.append(ch);
            }
        }
        fileName.append(".tcl");

        return fileName.toString();
    }

    /**
     * Returns the expected name of the configuration item file from the
     * export.
     *
     * @return name of the CI file
     */
    public String getCIFileNameFromExport()
    {
        return this.getCIFileName();
    }


    /**
     * Returns the update definition of this configuration item.
     *
     * @return file content of the configuration item used to make an update
     */
    public abstract String ciFile();

    /**
     * Defines if the related object in MX is already created.
     *
     * @param _created  new created value
     * @see #created
     */
    protected void setCreated(final boolean _created)
    {
        this.created = _created;
    }

    /**
     * Returns <i>true</i> if the related object in MX is created.
     *
     * @return <i>true</i> if the related object in MX is created; otherwise
     *         <i>false</i>
     * @see #created
     */
    protected boolean isCreated()
    {
        return this.created;
    }

    /**
     * Defines {@link #notSupportedVersions not supported versions}.
     *
     * @param _versions     not supported versions
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA notSupported(final Version... _versions)
    {
        this.notSupportedVersions.addAll(Arrays.asList(_versions));
        return (DATA) this;
    }

    /**
     * Checks if current MX version supports this data definition.
     *
     * @param _version      version to test
     * @return <i>true</i> if {@code _version} is supported; otherwise
     *         <i>false</i>
     * @see #notSupportedVersions
     */
    public boolean isSupported(final Version _version)
    {
        return !this.notSupportedVersions.contains(_version);
    }

    /**
     * Creates the related MX object for this data piece.
     *
     * @return this instance
     * @throws MatrixException if create failed
     */
    public abstract DATA create() throws MatrixException;

    /**
     * Updates current configuration item.
     *
     * @param _params   parameters
     * @return this data instance
     * @throws Exception if update failed
     */
    public DATA update(final String... _params)
        throws Exception
    {
        return this.updateWithCode(this.ciFile(), _params);
    }

    /**
     * Updates current configuration item and checks that given
     * <code>_error</code> is thrown.
     *
     * @param _error    error code
     * @param _params   parameters
     * @return this data instance
     * @throws Exception if update failed
     */
    public DATA failureUpdate(final UpdateException_mxJPO.Error _error,
                              final String... _params)
        throws Exception
    {
        return this.failedUpdateWithCode(this.ciFile(), _error, _params);
    }

    /**
     * Makes an update for given <code>_code</code>.
     *
     * @param _code         TCL update code
     * @param _params       parameters
     * @return this data instance
     * @throws Exception  if update failed
     */
    @SuppressWarnings("unchecked")
    public DATA updateWithCode(final String _code,
                               final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(this.getCIFileName(), _code);
        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.getTest().executeEncoded("Update", params, "FileContents", files);
        if (bck.get("exception") != null)  {
            throw (Exception) bck.get("exception");
        }
        return (DATA) this;
    }

    /**
     * Makes an update for given <code>_code</code> and checks that given
     * <code>_error</code> is thrown.
     *
     * @param _code         TCL update code
     * @param _error        error which must be thrown
     * @param _params       parameters
     * @return this data instance
     * @throws Exception  if update failed
     */
    @SuppressWarnings("unchecked")
    public DATA failedUpdateWithCode(final String _code,
                                     final UpdateException_mxJPO.Error _error,
                                     final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<String,String>();
        files.put(this.getCIFileName(), _code);
        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.getTest().executeEncoded("Update", params, "FileContents", files);
        Assert.assertTrue(
                bck.containsKey("exception"),
                "check exception exists");
        Assert.assertNotNull(
                bck.get("exception"),
                "check exception is not null");
        Assert.assertTrue(
                ((Exception) bck.get("exception")).getMessage().indexOf("UpdateError #" + _error.getCode() + ":") >= 0,
                "check for correct error code #" + _error.getCode() + " (have: " + ((Exception) bck.get("exception")).getMessage() + ")");

        return (DATA) this;
    }

    /**
     * Exports this data piece from MX. The returned values from the export are
     * checked for:
     * <ul>
     * <li>value is returned</li>
     * <li>returned export value includes exact one export for given
     *     {@link #ci configuration item type}</li>
     * <li>name of the exported object is equal {@link #name}</li>
     * <li>path of the exported object is equal {@link #ciFile()}</li>
     * <li>file name of the export object is equal returned value from
     *     {@link #getCIFileName()}</li>
     * </ul>
     *
     * @param _params       parameters
     * @return parsed export
     * @throws Exception if export failed
     */
    public ExportParser export(final String... _params)
        throws Exception
    {
        final Map<String,Collection<String>> files = new HashMap<String,Collection<String>>(1);
        files.put(this.ci.updateType, Arrays.asList(new String[]{this.getName()}));

        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }

        final Map<?,?> bck = this.test.executeEncoded("Export",
                                                      params,
                                                      "TypeDef", this.ci.updateType,
                                                      "Name", this.getName());

        // check for no exception
        if (bck.get("exception") != null)  {
            throw new MatrixException((Exception) bck.get("exception"));
        }

        // extract values
        final Map<?,?> values = (Map<?,?>) bck.get("values");

        // check existence and element is defined
        Assert.assertNotNull(values);
        Assert.assertEquals((String) values.get("TypeDef"),
                            this.ci.updateType,
                            "check correct type definition");

        // parse first element
        final ExportParser ret = this.parseExport(
                this.ci,
                (String) values.get("Code"),
                (String) bck.get("log"));

        // check no error occurred in the export
        Assert.assertFalse(
                ret.getLog().contains("[ERROR]"),
                "check that no error occured (have " + ret.getLog() + ")");

        // check returned configuration item name
        Assert.assertEquals(
                (String) values.get("Name"),
                this.name,
                "returned name is equal to given name");
        if (this.ci != AbstractTest.CI.PRG_JPO)  {
            // check path of the configuration item update file
            Assert.assertEquals(
                    (String) values.get("FilePath"),
                    this.getCI().filePath,
                    "check path where the configuration item update file is located is correct");
        } else  {
            // check path for JPOs (because of packages...)
            final String path;
            if (this.name.indexOf('.') < 0)  {
                path = this.ci.filePath;
            } else  {
                path = this.ci.filePath + "/" + this.name.replaceAll("\\.[^.]*$", "").replaceAll("\\.", "/");
            }
            Assert.assertEquals(
                    (String) values.get("FilePath"),
                    path,
                    "check path where the configuration item update file is located is correct");
        }
        // check file name of the configuration item update file
        Assert.assertEquals(
                (String) values.get("FileName"),
                this.getCIFileNameFromExport(),
                "check that the correct configuration item file name is returned");

        return ret;
    }

    /**
     * Returns the parsed export instance.
     *
     * @param _ci       related configuration item type
     * @param _code     code of the exported configuration item
     * @param _log      logging from the server
     * @return parsed export instance
     */
    protected ExportParser parseExport(final AbstractTest.CI _ci,
                                       final String _code,
                                       final String _log)
    {
        return new ExportParser(_ci, _code, _log);
    }

    /**
     * Checks the exported values.
     *
     * @param _params   parameters for the export
     * @return this instance
     * @throws Exception if check failed
     * @see #checkExport(ExportParser)
     * @see #export(String...)
     */
    @SuppressWarnings("unchecked")
    public DATA checkExport(final String... _params)
        throws Exception
    {
        this.checkExport(this.export(_params));
        return (DATA) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        Assert.assertEquals(_exportParser.getName(), this.getName(), "check name");
    }

    /**
     * Checks for given <code>_tag</code> in the <code>_exportParser</code>
     * if the <code>_value</code> exists and is correct defined. If
     * <code>_value</code> is <code>null</code>, it is checked that no value is
     * defined.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     */
    public void checkSingleValue(final ExportParser _exportParser,
                                 final String _kind,
                                 final String _tag,
                                 final String _value)
    {
        if (_value != null)  {
            Assert.assertEquals(_exportParser.getLines("/" + this.ci.getUrlTag() + "/" + _tag + "/@value").size(),
                                1,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is defined");
            Assert.assertEquals(_exportParser.getLines("/" + this.ci.getUrlTag() + "/" + _tag + "/@value").get(0),
                                _value,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is " + _value);

        } else  {
            Assert.assertEquals(_exportParser.getLines("/" + this.ci.getUrlTag() + "/" + _tag + "/@value").size(),
                                0,
                                "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
        }
    }

    /**
     * Checks in the {@code _exportParser} that given {@code _tag} does not
     * exits.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     */
    protected void checkNotExistingSingleValue(final ExportParser _exportParser,
                                               final String _kind,
                                               final String _tag)
    {
        Assert.assertEquals(_exportParser.getLines("/" + this.ci.getUrlTag() + "/" + _tag + "/@value").size(),
                            0,
                            "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
    }

    /**
     * Checks in the <code>_exportParser</code> if given <code>_tag</code>
     * exists (with zero length value).
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _exists           <i>true</i> means that the tag must exists;
     *                          otherwise value must not defined
     */
    public void checkValueExists(final ExportParser _exportParser,
                                 final String _kind,
                                 final String _tag,
                                 final boolean _exists)
    {
        if (_exists)  {
            this.checkSingleValue(_exportParser, _kind, _tag, "");
        } else  {
            this.checkNotExistingSingleValue(_exportParser, _kind, _tag);
        }
    }

    /**
     * Returns the string representation of this data piece as concatenation of
     * the configuration type {@link #ci} and the {@link #name}.
     *
     * @return string representation of this data piece
     */
    @Override()
    public String toString()
    {
        return "[" + this.ci.updateType + " '" + this.name + "']";
    }

    public class Values
        extends HashMap<String,Object>
    {
        /** Serial Version UID. */
        private static final long serialVersionUID = -2198651793145001986L;

        /**
         * Appends the defined flags to the TCL code {@code _cmd} of the
         * configuration item file.
         *
         * @param _prefix   prefix in front of the values
         * @param _cmd      string builder with the TCL commands of the
         *                  configuration item file
         * @param _suffix   suffix after the values
         */
        public void append4CIFileValues(final String _prefix,
                                        final StringBuilder _cmd,
                                        final String _suffix)
        {
            for (final Map.Entry<String,Object> entry : this.entrySet())  {
                if ((entry.getValue() instanceof Character) || (entry.getValue() instanceof Integer))  {
                    _cmd.append(_prefix).append(entry.getKey()).append(' ').append(entry.getValue()).append(_suffix);
                } else  {
                    _cmd.append(_prefix).append(entry.getKey()).append(" \"")
                        .append(AbstractTest.convertTcl(entry.getValue().toString()))
                        .append('\"')
                        .append(_suffix);
                }
            }
        }

        /**
         * Checks for all defined values.
         *
         * @param _exportParser     parsed export
         */
        public void checkExport(final ExportParser _exportParser)
        {
            for (final Map.Entry<String,Object> entry : this.entrySet())  {
                if ((entry.getValue() instanceof Character) || (entry.getValue() instanceof Integer))  {
                    AbstractData.this.checkSingleValue(
                            _exportParser,
                            entry.getKey(),
                            entry.getKey(),
                            entry.getValue().toString());
                } else  {
                    AbstractData.this.checkSingleValue(
                            _exportParser,
                            entry.getKey(),
                            entry.getKey(),
                            "\"" + AbstractTest.convertTcl(entry.getValue().toString()) + "\"");
                }
            }
        }
    }
}
