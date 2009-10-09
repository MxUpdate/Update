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

package org.mxupdate.test.data.user;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractData;
import org.mxupdate.test.data.user.workspace.CueData;
import org.testng.Assert;

/**
 * The class is used to define all user objects used to create / update and to
 * export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T> class derived from abstract user
 */
public abstract class AbstractUserData<T extends AbstractUserData<?>>
    extends AbstractData<T>
{
    /**
     * Related cues of the workspace data from this user.
     */
    private final Set<CueData<T>> cues = new HashSet<CueData<T>>();

    /**
     * Constructor to initialize this user.
     *
     * @param _test                 related test implementation (where this
     *                              user is defined)
     * @param _ci                   related configuration type
     * @param _name                 name of the user
     * @param _filePrefix           prefix for the file name
     * @param _ciPath               path of the configuration item file
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    protected AbstractUserData(final AbstractTest _test,
                               final AbstractTest.CI _ci,
                               final String _name,
                               final String _filePrefix,
                               final String _ciPath,
                               final Set<String> _requiredExportValues)
    {
        super(_test, _ci, _name, _filePrefix, _ciPath, _requiredExportValues);
    }

    /**
     * Creates for given <code>_name</code> for this user a new cue.
     *
     * @param _name     name of the new cue for this user
     * @return new created cue instance for this user
     * @see #cues
     */
    @SuppressWarnings("unchecked")
    public CueData<T> newCue(final String _name)
    {
        final CueData<T> ret = new CueData<T>(this.getTest(), (T) this, _name);
        this.cues.add(ret);
        return ret;
    }

    /**
     * Creates the part of the CI file for the user specific workspace object.
     * This includes:
     * <ul>
     * <li>{@link #cues}</li>
     * </ul>
     *
     * @return part of the CI file for the user specific workspace objects
     */
    protected String ciFileWorkspaceObjects()
    {
        final StringBuilder cmd = new StringBuilder();

        // cues
        for (final CueData<T> cue : this.cues)  {
            cmd.append(cue.ciFile());
        }

        return cmd.toString();
    }

    /**
     * Creates all workspace objects.
     *
     * @throws MatrixException if create of the workspace objects for this user
     *                         failed
     * @see #cues
     */
    protected void createWorkspaceObjects()
        throws MatrixException
    {
        for (final CueData<T> cue : this.cues)  {
            cue.create();
        }
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * All workspace objects for this user are checked that they are defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        final Set<CueData<T>> tmpCues = new HashSet<CueData<T>>(this.cues);

        for (final ExportParser.Line rootLine : _exportParser.getRootLines())  {
            if (rootLine.getValue().startsWith("escape add cue "))  {
                for (final CueData<T> cue : this.cues)  {
                    final String key = new StringBuilder()
                            .append("escape add cue \"")
                            .append(AbstractTest.convertTcl(cue.getName()))
                            .append("\"").toString();
                    if (key.equals(rootLine.getValue()))  {
                        tmpCues.remove(cue);
                        cue.checkExport(new ExportParser(cue.getName(), rootLine));
                        break;
                    }
                }
            }
        }

        Assert.assertTrue(tmpCues.isEmpty(), "check that all cues are defined in the update file");
    }
}
