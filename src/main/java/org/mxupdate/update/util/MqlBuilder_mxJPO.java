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

package org.mxupdate.update.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 * Builder used to defined MQL commands.
 *
 * @author The MxUpdate Team
 */
public final class MqlBuilder_mxJPO
{
    // hint: to avoid challenges with backslashes, they are defined directly via character
    /** Regular expression to replace backslashes. */
    private final static String MQL_CONVERT_BACKSLASH_FROM  = "" + ((char) 0x005c) + ((char) 0x005c);
    /** String which will be the target for backslashes. */
    private final static String MQL_CONVERT_BACKSLASH_TO    = "" + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c);
    /** Regular expression to replace quotations. */
    private final static String MQL_CONVERT_QUOTATION_FROM  = "" + ((char) 0x005c) + ((char) 0x0022);
    /** String which will be the target for quotations. */
    private final static String MQL_CONVERT_QUOTATION_TO    = "" + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x005c) + ((char) 0x0022);

    /**
     * Private constructor to avoid external initialization.
     */
    private MqlBuilder_mxJPO()
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
     * Initializes new MQL builder without prefix.
     *
     * @return new MQL builder instance
     */
    public static MultiLineMqlBuilder multiLineMql()
    {
        return new MultiLineMqlBuilder("");
    }

    /**
     * Initializes new MQL builder.
     *
     * @param _prefix   MQL command for the prefix
     * @param _args     arguments for the MQL command prefix
     * @return new MQL builder instance
     */
    public static MultiLineMqlBuilder multiLine(final CharSequence _prefix,
                                                final String... _args)
    {
        return new MultiLineMqlBuilder(_prefix, _args);
    }

    /**
     * MQL builder for multi-line MQL statements.
     */
    public final static class MultiLineMqlBuilder
    {
        /** Prefix line for all executed lines. */
        private final Stack<MqlBuilder> prefix = new Stack<MqlBuilder>();
        /** All lines. */
        private final List<MqlBuilder> lines = new ArrayList<MqlBuilder>();
        /** Latest appended line. */
        private MqlBuilder lastLine;

        /**
         * Initializes the multi-line MQL builder.
         *
         * @param _prefix   MQL command for the prefix
         * @param _args     arguments for the MQL command prefix
         */
        private MultiLineMqlBuilder(final CharSequence _prefix,
                                 final String... _args)
        {
            this.prefix.push(new MqlBuilder());
            this.prefix.peek().cmd.append(_prefix.toString());
            this.prefix.peek().args.addAll(Arrays.asList(_args));
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
            this.cmd.append(_cmd);
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
                String tmpCmd = this.cmd.toString();
                for (int idx = this.args.size(); idx > 0; idx --)
                {
                     tmpCmd = tmpCmd.replaceAll(
                            ((char) 0x005c) + "$" + idx,
                            "\""
                                    + this.args.get(idx - 1)
                                            .replaceAll(MqlBuilder_mxJPO.MQL_CONVERT_BACKSLASH_FROM, MqlBuilder_mxJPO.MQL_CONVERT_BACKSLASH_TO)
                                            .replaceAll(MqlBuilder_mxJPO.MQL_CONVERT_QUOTATION_FROM, MqlBuilder_mxJPO.MQL_CONVERT_QUOTATION_TO)
                                    + "\"");
                }

                try  {
                    meth.invoke(mql, _context, this.cmd);
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
            }

            if ((mql.getError() != null) && !mql.getError().isEmpty())  {
                throw new MatrixException(mql.getError()
                        + "\nMQL command was:\n" + this.toString());
            }
            return mql.getResult().trim();
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
