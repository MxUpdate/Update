/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.test.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * Defines common information from administration objects used to create,
 * update and check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractData<DATA extends AbstractData<?>>
{
    /**
     * Related test case where this data piece was created.
     */
    private final AbstractTest test;

    /**
     * Related configuration item of this data piece.
     */
    private final AbstractTest.CI ci;

    /**
     * Name of the data piece.
     */
    private final String name;

    /**
     * Values of this data piece.
     *
     * @see #setValue(String, String)
     * @see #getValue(String)
     * @see #getValues()
     */
    private final Map<String,String> values = new HashMap<String,String>();

    /**
     * Flag to indicate that this data piece is creatd.
     *
     * @see #isCreated()
     * @see #setCreated(boolean)
     */
    private boolean created;

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
        if (_ci.getBusType() != null)  {
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
                         final String _value)
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
     * @see #values
     */
    public String getValue(final String _key)
    {
        return this.values.get(_key);
    }

    /**
     * Returns the description of the abstract data element.
     *
     * @return description of the abstract data element
     * @see #description
     */
    public Map<String,String> getValues()
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
        for (int idx = 0; idx < charName.length; idx++)  {
            final char ch = charName[idx];
            if (ch == '@')  {
                fileName.append("@@");
            } else if ((ch < '(' || ch > ')')
                    && (ch < '+' || ch > '.')
                    && (ch < '0' || ch > '9')
                    && (ch < 'A' || ch > 'Z')
                    && (ch < 'a' || ch > 'z')
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
     * Creates the related MX object for this data piece.
     *
     * @return this instance
     * @throws MatrixException if create failed
     */
    public abstract DATA create() throws MatrixException;

    /**
     * Exports this data piece from MX. The returned values from the export are
     * checked for:
     * <ul>
     * <li>value is returned</li>
     * <li>returned export value includes exact one export for given
     *     {@link #ci configuration item type}</li>
     * <li>name of the exported object is equal {@link #name}</li>
     * <li>path of the exported object is equal {@link #ciPath}</li>
     * <li>file name of the export object is equal returned value from
     *     {@link #getCIFileName()}</li>
     * </ul>
     *
     * @param _params       parameters
     * @return parsed export
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if MQL calls failed
     */
    public ExportParser export(final String... _params)
        throws IOException, MatrixException
    {
        final Map<String,Collection<String>> files = new HashMap<String,Collection<String>>(1);
        files.put(this.ci.updateType, Arrays.asList(new String[]{this.getName()}));

        final Map<String,String> params = new HashMap<String,String>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }

        final AbstractTest.JPOReturn<Map<String,Collection<Map<String,String>>>> jpoReturn =
                this.test.<Map<String,Collection<Map<String,String>>>>jpoInvoke("org.mxupdate.plugin.Export",
                                                                                "exportByName",
                                                                                files,
                                                                                params);

        // check for no exception
        if (jpoReturn.getException() != null)  {
            throw new MatrixException(jpoReturn.getException());
        }

        // extract values
        final Map<String,Collection<Map<String,String>>> values = jpoReturn.getValues();

        // check existence and element is defined
        Assert.assertNotNull(values);
        Assert.assertTrue(values.containsKey(this.ci.updateType));
        Assert.assertEquals(values.get(this.ci.updateType).size(), 1, "one element is returned");

        // parse first element
        final Map<String,String> exportDesc = values.get(this.getCI().updateType).iterator().next();
        final ExportParser ret = this.parseExport(this.ci, exportDesc.get("code"));

        // check returned configuration item name
        Assert.assertEquals(exportDesc.get("name"),
                            this.name,
                            "returned name is equal to given name");
        if (this.ci != AbstractTest.CI.PRG_JPO)  {
            // check path of the configuration item update file
            Assert.assertEquals(exportDesc.get("path"),
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
            Assert.assertEquals(exportDesc.get("path"),
                                path,
                                "check path where the configuration item update file is located is correct");
        }
        // check file name of the configuration item update file
        Assert.assertEquals(exportDesc.get("filename"),
                            this.getCIFileName(),
                            "check that the correct configuration item file name is returned");

        return ret;
    }

    /**
     * Returns the parsed export instance.
     *
     * @param _ci       related configuration item type
     * @param _code     code of the exported configuration item
     * @return parsed export instance
     */
    protected ExportParser parseExport(final AbstractTest.CI _ci,
                                       final String _code)
    {
        return new ExportParser(_ci, _code);
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
            Assert.assertEquals(_exportParser.getLines("/mql/" + _tag + "/@value").size(),
                                1,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is defined");
            Assert.assertEquals(_exportParser.getLines("/mql/" + _tag + "/@value").get(0),
                                _value,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is " + _value);

        } else  {
            Assert.assertEquals(_exportParser.getLines("/mql/" + _tag + "/@value").size(),
                                0,
                                "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
        }
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
        this.checkSingleValue(_exportParser, _kind, _tag, _exists ? "" : null);
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
}
