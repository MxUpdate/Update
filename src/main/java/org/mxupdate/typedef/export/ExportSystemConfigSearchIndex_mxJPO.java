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

package org.mxupdate.typedef.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;

import matrix.util.MatrixException;

/**
 * Handles the export of the search index.
 *
 * @author The MxUpdate Team
 */
public class ExportSystemConfigSearchIndex_mxJPO
    implements IExport_mxJPO
{
    /** MQL statement to get the search index configuration from the system. */
    private final static String MQL_GET_INDEX = "escape print system searchindex"; //$NON-NLS-1$
    /** Start string of the returned search index configuration (because thesearch index is stored as a property...). */
    private final static String START_KEYWORD = "SearchIndex="; //$NON-NLS-1$
    /** Length of the start string of the returned search index configuration. */
    private final static int START_KEYWORD_LENGTH = ExportSystemConfigSearchIndex_mxJPO.START_KEYWORD.length();

    @Override()
    public void export(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final String _mxName,
                       final File _path)
        throws IOException, MatrixException
    {
        // extract code of search index
        final String code;
        final String tmp = MqlBuilderUtil_mxJPO.mql().cmd(ExportSystemConfigSearchIndex_mxJPO.MQL_GET_INDEX).exec(_paramCache.getContext());
        if (tmp.startsWith(ExportSystemConfigSearchIndex_mxJPO.START_KEYWORD))  {
            code = tmp.substring(ExportSystemConfigSearchIndex_mxJPO.START_KEYWORD_LENGTH);
        } else  {
            code = "";
        }

        // write code
        final File file = new File(new File(_path, _typeDef.getFilePath()), FileUtils_mxJPO.calcCIFileName(_typeDef, _mxName));
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        try  {
            out.write(code);
            out.flush();
        } finally  {
            out.close();
        }
    }
}
