package org.mxupdate.update.util;

import java.util.ArrayList;
import java.util.List;

import matrix.util.MatrixException;

public class MqlBuilder_mxJPO
{
    private final String prefix;

    private final List<StringBuilder> lines = new ArrayList<StringBuilder>();

    private StringBuilder lastLine;

    public MqlBuilder_mxJPO(final CharSequence _prefix)
    {
        this.prefix = _prefix.toString();
    }

    public StringBuilder newLine()
    {
        this.lastLine = new StringBuilder();
        this.lines.add(this.lastLine);
        return this.lastLine;
    }

    public StringBuilder lastLine()
    {
        return this.lastLine;
    }

    public void exec(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        for (final StringBuilder line : this.lines)  {
            MqlUtil_mxJPO.execMql(_paramCache, this.prefix + ' ' + line);
        }
    }
}
