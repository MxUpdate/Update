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

package org.mxupdate.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mxupdate.update.util.ParameterCache_mxJPO;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 * Builder used to defined MQL commands.
 *
 * @author The MxUpdate Team
 */
public final class MqlBuilderUtil_mxJPO
{
    // hint: to avoid challenges with backslashes, they are defined directly via character
    /** Regular expression for the matcher in the converter. */
    private final static String MQL_MATCHER                 = "" + ((char) 0x005c) + "$[0-9]+";
    /** Regular expression to replace backslashes. */
    private final static String MQL_CONVERT_BACKSLASH_FROM  = "" + ((char) 0x005c) + ((char) 0x005c);
    /** String which will be the target for backslashes. */
    private final static String MQL_CONVERT_BACKSLASH_TO    = "" + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c);
    /** Regular expression to replace quotations. */
    private final static String MQL_CONVERT_QUOTATION_FROM  = "" + ((char) 0x005c) + ((char) 0x0022);
    /** String which will be the target for quotations. */
    private final static String MQL_CONVERT_QUOTATION_TO    = "" + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x0022);

    /**
     * Private constructor to avoid external initialization.
     */
    private MqlBuilderUtil_mxJPO()
    {
    }

    /**
     * Initializes new single line MQL builder.
     *
     * @return new MQL builder instance
     */
    public static MqlBuilder mql()
    {
        return new MqlBuilder();
    }

    /**
     * Initializes new MQL builder.
     *
     * @param _file     file which is executed
     * @param _prefix   MQL command for the prefix
     * @param _args     arguments for the MQL command prefix
     * @return new MQL builder instance
     */
    public static MultiLineMqlBuilder multiLine(final File _file,
                                                final CharSequence _prefix,
                                                final String... _args)
    {
        return new MultiLineMqlBuilder(_file, _prefix, _args);
    }

    /**
     * MQL builder for multi-line MQL statements.
     */
    public final static class MultiLineMqlBuilder
    {
        /** File which is currently executed. */
        private final File file;
        /** Prefix line for all executed lines. */
        private final Stack<MqlBuilder> prefix = new Stack<MqlBuilder>();
        /** All lines. */
        private final List<MqlBuilder> lines = new ArrayList<MqlBuilder>();
        /** Latest appended line. */
        private MqlBuilder lastLine;

        /**
         * Initializes the multi-line MQL builder.
         *
         * @param _file     file which is executed
         * @param _prefix   MQL command for the prefix
         * @param _args     arguments for the MQL command prefix
         */
        private MultiLineMqlBuilder(final File _file,
                                    final CharSequence _prefix,
                                    final String... _args)
        {
            this.file = _file;
            this.prefix.push(new MqlBuilder());
            this.prefix.peek().cmd.append(_prefix.toString());
            this.prefix.peek().args.addAll(Arrays.asList(_args));
        }

        /**
         * Returns the {@link #file} which is executed.
         *
         * @return file
         */
        public File getFile()
        {
            return this.file;
        }

        /**
         * Checks if {@link #lines} are defined.
         *
         * @return <i>true<i> if new lines exist; otherwise <i>false</i>
         */
        public boolean hasNewLines()
        {
            return !this.lines.isEmpty();
        }

        /**
         * Defines new prefix.
         *
         * @param _prefix   MQL command for the prefix
         * @param _args     arguments for the MQL command prefix
         * @return this MQL builder
         */
        public MultiLineMqlBuilder pushPrefix(final CharSequence _prefix,
                                              final String... _args)
        {
            this.prefix.push(new MqlBuilder());
            this.prefix.peek().cmd.append(_prefix.toString());
            this.prefix.peek().args.addAll(Arrays.asList(_args));
            return this;
        }

        /**
         * Defines new prefix by appending further definition to previous prefix.
         *
         * @param _prefix   MQL command for the prefix
         * @param _args     arguments for the MQL command prefix
         * @return this MQL builder
         */
        public MultiLineMqlBuilder pushPrefixByAppending(final CharSequence _prefix,
                                                         final String... _args)
        {
            final MqlBuilder prev = this.prefix.peek();

            this.prefix.push(new MqlBuilder());
            this.prefix.peek().cmd.append(prev.cmd).append(' ').append(_prefix.toString());
            this.prefix.peek().args.addAll(prev.args);
            this.prefix.peek().args.addAll(Arrays.asList(_args));
            return this;
        }

        /**
         * Removes the latest prefix definition back the prefix defined before the
         * latest.
         *
         * @return this MQL builder
         */
        public MultiLineMqlBuilder popPrefix()
        {
            this.prefix.pop();
            return this;
        }

        /**
         * Appends a new line to the MQL builder.
         *
         * @return this MQL builder
         */
        public MultiLineMqlBuilder newLine()
        {
            this.lastLine = new MqlBuilder();
            if ((this.prefix.peek().cmd.length() > 0) && !this.prefix.peek().args.isEmpty())  {
                this.lastLine.cmd.append(this.prefix.peek().cmd).append(' ');
                this.lastLine.args.addAll(this.prefix.peek().args);
            }
            this.lines.add(this.lastLine);
            return this;
        }

        /**
         * Appends given MQL command and arguments to latest line.
         *
         * @param _cmd      MQL command line to append
         * @return this MQL builder
         */
        public MultiLineMqlBuilder cmd(final CharSequence _cmd)
        {
            if (this.lastLine == null) {
                this.newLine();
            }
            this.lastLine.cmd(_cmd);
            return this;
        }

        /**
         * Appends given MQL command and arguments to latest line.
         *
         * @param _cmd      MQL command line to append
         * @param _args     arguments for the MQL command line (if {@code null}, an
         *                  empty string is defined!
         * @return this MQL builder
         */
        public MultiLineMqlBuilder arg(final String _argument)
        {
            this.lastLine.arg(_argument);
            return this;
        }

        /**
         * Executes all lines of the MQL builder.
         *
         * @param _paramCache       parameter cache with the context
         * @throws MatrixException if execute failed
         */
        public void exec(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            this.exec(_paramCache.getContext());
        }

        /**
         * Executes all lines of the MQL builder.
         *
         * @param _paramCache       parameter cache with the context
         * @throws MatrixException if execute failed
         */
        public void exec(final Context _context)
            throws MatrixException
        {
            for (final MqlBuilder line : this.lines)  {
                line.exec(_context);
            }
        }

        @Override()
        public String toString()
        {
            final StringBuilder ret = new StringBuilder();
            for (final MqlBuilder line : this.lines)  {
                ret.append(line.toString()).append("\n");
            }
            return ret.toString();
        }
    }

    /**
     * Handles one MQL line.
     */
    public final static class MqlBuilder
    {
        /** MQL line. */
        private final StringBuilder cmd = new StringBuilder();
        /** Arguments of the line. */
        final List<String> args = new ArrayList<String>();

        /**
         * Private constructor to avoid external initialization.
         */
        private MqlBuilder()
        {
        }

        /**
         * Appends given MQL command and arguments to latest line.
         *
         * @param _cmd      MQL command line to append
         * @return this MQL builder
         */
        public MqlBuilder cmd(final CharSequence _cmd)
        {
            if (_cmd != null)  {
                this.cmd.append(_cmd);
            }
            return this;
        }

        /**
         * Appends given MQL command and arguments to latest line.
         *
         * @param _cmd      MQL command line to append
         * @param _args     arguments for the MQL command line (if {@code null}, an
         *                  empty string is defined!
         * @return this MQL builder
         */
        public MqlBuilder arg(final String _argument)
        {
            this.cmd.append("$").append(this.args.size() + 1);
            this.args.add((_argument != null) ? _argument : "");
            return this;
        }

        /**
         * Executes given MQL command.
         *
         * @param _paramCache       parameter cache with the context
         * @throws MatrixException if execute failed
         */
        public String exec(final ParameterCache_mxJPO _paramCache)
            throws MatrixException
        {
            return this.exec(_paramCache.getContext());
        }

        /**
         * Executes given MQL command.
         *
         * @param _context              MX context for this request
         * @return trimmed result of the MQL execution
         * @throws MatrixException if MQL execution failed
         */
        public String exec(final Context _context)
            throws MatrixException
        {
            if (!this.cmd.toString().startsWith("escape"))  {
                throw new MatrixException("MQL Command starts not with escape!\nMQL command was:\n" + this.toString());
            }

            final MQLCommand mql = new MQLCommand();

            // search for new MQL execute method with arguments
            Method meth = null;
            boolean methodWithArgs = false;
            try {
                meth = mql.getClass().getDeclaredMethod("executeCommand", Context.class, String.class, List.class);
                methodWithArgs = true;
            } catch (final NoSuchMethodException e)  {
                try
                {
                    meth = mql.getClass().getDeclaredMethod("executeCommand", Context.class, String.class);
                } catch (final NoSuchMethodException e1)  {
                    meth = null;
                }
            }

            // new method with arguments
            if (methodWithArgs)  {
                try  {
                    meth.invoke(mql, _context, this.cmd.toString(), this.args);
                } catch (final IllegalAccessException e)  {
                    throw new MatrixException(e);
                } catch (final IllegalArgumentException e)  {
                    throw new MatrixException(e);
                } catch (final InvocationTargetException e)  {
                    if (e.getCause() instanceof MatrixException)  {
                        throw (MatrixException) e.getCause();
                    }
                    throw new MatrixException(e);
                }

            // old MX versions where method has no arguments...
            } else if (meth != null)  {
                try  {
                    meth.invoke(mql, _context, this.mqlConvertLegacy());
                } catch (final IllegalAccessException e)  {
                    throw new MatrixException(e);
                } catch (final IllegalArgumentException e)  {
                    System.err.println("Failed for " + this.toString());
                    throw new MatrixException(e);
                } catch (final InvocationTargetException e)  {
                    if (e.getCause() instanceof MatrixException)  {
                        throw (MatrixException) e.getCause();
                    }
                    throw new MatrixException(e);
                }
            }

            if ((mql.getError() != null) && !mql.getError().isEmpty())  {
                throw new MatrixException(mql.getError()
                        + "\nMQL command was:\n" + this.toString());
            }
            return mql.getResult().trim();
        }

        /**
         * Converts the defined {@link #cmd} with {@link #args} to one MQL
         * statement for legacy calls.
         *
         * @return converted MQL statement
         */
        private String mqlConvertLegacy()
        {
            final StringBuilder ret = new StringBuilder();
            final String tmpCmd = this.cmd.toString();
            final Matcher matcher = Pattern.compile(MqlBuilderUtil_mxJPO.MQL_MATCHER).matcher(this.cmd.toString());
            int startIdx = 0;
            while (matcher.find())  {
                final int argIdx = Integer.parseInt(matcher.group().substring(1));
                final String replace;
                if (this.args.size() >= argIdx)  {
                    replace = "\""
                            + this.args.get(argIdx - 1)
                            .replaceAll(MqlBuilderUtil_mxJPO.MQL_CONVERT_BACKSLASH_FROM, MqlBuilderUtil_mxJPO.MQL_CONVERT_BACKSLASH_TO)
                            .replaceAll(MqlBuilderUtil_mxJPO.MQL_CONVERT_QUOTATION_FROM, MqlBuilderUtil_mxJPO.MQL_CONVERT_QUOTATION_TO)
                            + "\"";
                } else  {
                    replace = matcher.group();
                }

                ret.append(tmpCmd.substring(startIdx, matcher.start())).append(replace);

                startIdx = matcher.end();
            }
            ret.append(tmpCmd.substring(startIdx, tmpCmd.length()));

            return ret.toString();
        }

        @Override()
        public String toString()
        {
            final StringBuilder ret = new StringBuilder(this.cmd);
            int idx = 1;
            for (final String arg : this.args)  {
                ret.append("\n    with arg[").append(idx++).append("] = \"").append(arg).append("\"");
            }
            return ret.toString();
        }
    }
}
