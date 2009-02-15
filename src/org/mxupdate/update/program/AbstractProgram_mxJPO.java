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

package org.mxupdate.update.program;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractProgram_mxJPO
        extends AbstractObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -6353366924945315894L;

    /**
     *
     * @param _typeDef  type definition of the program
     * @param _mxName   MX name of the program object
     */
    protected AbstractProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                                    final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * All basic properties are read and initialized. Basic attributes are:
     * <ul>
     * <li>name</li>
     * <li>author</li>
     * <li>application</li>
     * <li>installation date</li>
     * <li>installer</li>
     * <li>original name</li>
     * <li>version</li>
     * </ul>
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException if the &quot;basic&quot; properties could not be
     *                         read from the program object
     */
    @Override
    protected void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
    {
        // set author depending on the properties
        this.setAuthor(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.AUTHOR));
        // set application depending on the properties
        this.setApplication(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.APPLICATION));
        // sets the installation date depending on the properties
        this.setInstallationDate(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.INSTALLEDDATE));
        // sets the installer depending on the properties
        this.setInstaller(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.INSTALLER));
        // sets the original name depending on the properties
        this.setOriginalName(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.ORIGINALNAME));
        // sets the version depending on the properties
        this.setVersion(this.getPropValue(_paramCache.getContext(), AdminPropertyDef.VERSION));
    }

    /**
     * Deletes administration object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if delete failed
     */
    @Override
    public void delete(final ParameterCache_mxJPO _paramCache)
            throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("delete ").append(this.getTypeDef().getMxAdminName())
                .append(" \"").append(this.getName()).append("\" ")
                .append(this.getTypeDef().getMxAdminSuffix());
        execMql(_paramCache.getContext(), cmd);
    }

    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _updateCode,
                          final String _newVersion,
                          final File _file)
            throws MatrixException
    {
        // append statement to reset execute user
        final StringBuilder cmd = new StringBuilder()
                .append("mod prog \"").append(this.getName())
                .append("\" execute user \"\";\n");

        // append MQL statements to reset properties
        final String prpStr = execMql(_paramCache.getContext(),
                                      new StringBuilder().append("print program \"").append(this.getName())
                                           .append("\" select property.name property.to dump ' @@@@@@'"));
        final String[] prpArr = prpStr.toString().split("(@@@@@@)");
        final int length = (prpArr.length + 1) / 2;
        for (int idxName = 0, idxTo = length; idxName < length; idxName++, idxTo++)  {
            final String name = prpArr[idxName].trim();
            if (AdminPropertyDef.getEnumByPropName(name) == null)  {
// TODO: if to is defined, the remove must be specified the to ....
                final String to = (idxTo < length) ? prpArr[idxTo].trim() : "";
                cmd.append("mod prog \"").append(this.getName())
                   .append("\" remove property \"").append(name).append("\";\n");
            }
        }

        // append update code
        cmd.append(_updateCode)
           .append("mod prog \"").append(this.getName()).append('\"');

        // define version property
        cmd.append(" add property \"").append(AdminPropertyDef.VERSION.getPropName()).append("\" ")
           .append("value \"").append(_newVersion != null ? _newVersion : "").append('\"');
        // define file date property
        final DateFormat fileFormat = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_FILEDATEFORMAT));
        cmd.append(" add property \"").append(AdminPropertyDef.FILEDATE.getPropName()).append("\" ")
           .append("value \"").append(fileFormat.format(new Date(_file.lastModified()))).append('\"');
        // is installed date property defined?
        if ((this.getInstallationDate() == null) || "".equals(this.getInstallationDate()))  {
            final DateFormat format = new SimpleDateFormat(_paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLEDDATEFORMAT));
            final String date = format.format(new Date());
            _paramCache.logTrace("    - define installed date '" + date + "'");
            cmd.append(" add property \"").append(AdminPropertyDef.INSTALLEDDATE.getPropName()).append("\" ")
               .append("value \"").append(date).append('\"');
        }
        // exists no installer property or installer property not equal?
        final String installer;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_INSTALLER))  {
            installer = _paramCache.getValueString(ParameterCache_mxJPO.KEY_INSTALLER);
        } else if ((this.getInstaller() == null) || "".equals(this.getInstaller()))  {
            installer = _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTINSTALLER);
        } else  {
            installer = null;
        }
        if (installer != null)  {
            _paramCache.logTrace("    - define installer '" + installer + "'");
            cmd.append(" add property \"").append(AdminPropertyDef.INSTALLER.getPropName()).append("\" ")
               .append("value \"").append(installer).append('\"');
        }
        // is original name property defined?
        if ((this.getOriginalName() == null) && "".equals(this.getOriginalName()))  {
            _paramCache.logTrace("    - define original name '" + this.getName() + "'");
            cmd.append(" add property \"").append(AdminPropertyDef.ORIGINALNAME.getPropName()).append("\" ")
               .append("value \"").append(this.getName()).append('\"');
        }
        // exists no application property or application property not equal?
        final String appl;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_APPLICATION))  {
            appl = _paramCache.getValueString(ParameterCache_mxJPO.KEY_APPLICATION);
        } else if ((this.getApplication() == null) || "".equals(this.getApplication()))  {
            appl = _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAPPLICATION);
        } else  {
            appl = null;
        }
        if (appl != null)  {
            _paramCache.logTrace("    - define application '" + appl + "'");
            cmd.append(" add property \"").append(AdminPropertyDef.APPLICATION.getPropName()).append("\" ")
               .append("value \"").append(appl).append('\"');
        }
        // exists no author property or author property not equal?
        final String author;
        if (_paramCache.contains(ParameterCache_mxJPO.KEY_AUTHOR))  {
            author = _paramCache.getValueString(ParameterCache_mxJPO.KEY_AUTHOR);
        } else if ((this.getAuthor() == null) || "".equals(this.getAuthor()))  {
            author = _paramCache.getValueString(ParameterCache_mxJPO.KEY_DEFAULTAUTHOR);
        } else  {
            author = null;
        }
        if (author != null)  {
            _paramCache.logTrace("    - define author '" + author + "'");
            cmd.append(" add property \"").append(AdminPropertyDef.AUTHOR.getPropName()).append("\" ")
               .append("value \"").append(author).append('\"');
        }
        cmd.append(";\n");

        // and execute alls
        execMql(_paramCache.getContext(), cmd);
    }
}
