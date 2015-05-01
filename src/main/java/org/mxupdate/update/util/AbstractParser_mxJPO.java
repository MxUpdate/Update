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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;

/**
 * The class is used to define common methods for parsers within updates.
 *
 * @author The MxUpdate Team
 * @param <TYPEIMPL>    parser is implemented for this class
 */
public abstract class AbstractParser_mxJPO<TYPEIMPL extends AbstractAdminObject_mxJPO>
{
    /**
     * Parses one complete menu definition.
     *
     * @param _paramCache   parameter cache
     * @param _typeDef      type definition of the menu (to instantiate the menu)
     * @param _mxName       MX name of the menu
     * @throws ParseException is parsing failed
     * @throws SecurityException if values can not be set
     * @throws IllegalArgumentException if values can not be set
     * @throws NoSuchMethodException if values can not be set
     * @throws InstantiationException if values can not be set
     * @throws IllegalAccessException if values can not be set
     * @throws InvocationTargetException if values can not be set
     */
    public TYPEIMPL parse(final ParameterCache_mxJPO _paramCache,
                          final TypeDef_mxJPO _typeDef,
                          final String _mxName)
        throws ParseException, SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        @SuppressWarnings("unchecked")
        final TYPEIMPL ciObj = (TYPEIMPL) _typeDef.newTypeInstance(_mxName);
        this.parse(ciObj);
        this.prepareObject(_paramCache, ciObj);
        return ciObj;
    }

    /**
     * The stream is parsed and the result is stored in given instance
     * {@code _ciObj}.
     *
     * @param _ciObj        target CI object
     * @throws ParseException is parsing failed
     * @throws SecurityException if values can not be set
     * @throws IllegalArgumentException if values can not be set
     * @throws NoSuchMethodException if values can not be set
     * @throws InstantiationException if values can not be set
     * @throws IllegalAccessException if values can not be set
     * @throws InvocationTargetException if values can not be set
     */
    abstract public void parse(TYPEIMPL _ciObj)
        throws ParseException, SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;


    /**
     * Calls for the administration object <code>_object</code> the prepare
     * method.
     *
     * @param _paramCache   parameter cache
     * @param _object       cache for which the prepare method must be called
     */
    protected void prepareObject(final ParameterCache_mxJPO _paramCache,
                                 final TYPEIMPL _object)
    {
        try {
            Method method = null;
            Class<?> clazz = _object.getClass();
            try  {
                method = clazz.getDeclaredMethod("prepare", ParameterCache_mxJPO.class);
            } catch (final NoSuchMethodException e)  {
            }
            while ((method == null) && (clazz != null))  {
                clazz = clazz.getSuperclass();
                if (clazz != null)  {
                    try  {
                        method = clazz.getDeclaredMethod("prepare", ParameterCache_mxJPO.class);
                    } catch (final NoSuchMethodException e)  {
                    }
                }
            }
            if (method == null)  {
                throw new NoSuchMethodException(_object.getClass() + ".prepare()");
            }
            try  {
                method.setAccessible(true);
                method.invoke(_object, _paramCache);
            } finally  {
                method.setAccessible(false);
            }
        } catch (final IllegalArgumentException e) {
            throw new ParseUpdateError(e);
        } catch (final IllegalAccessException e) {
            throw new ParseUpdateError(e);
        } catch (final InvocationTargetException e) {
            throw new ParseUpdateError(e);
        } catch (final SecurityException e) {
            throw new ParseUpdateError(e);
        } catch (final NoSuchMethodException e) {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final String _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Double _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_value</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _value        new value
     */
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Boolean _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                field.set(_object, _value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Sets the new <code>_values</code> for field <code>_fieldName</code> of
     * <code>_object</code>.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @param _values       new values of the field
     */
    @SuppressWarnings("unchecked")
    protected void setValue(final Object _object,
                            final String _fieldName,
                            final Collection<?> _values)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                final Collection<Object> set = (Collection<Object>) field.get(_object);
                set.addAll(_values);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Appends for field with <code>_fieldName</code> the <code>_value</code>
     * for object <code>_object</code>.
     *
     * @param _object       object
     * @param _fieldName    name of the field
     * @param _value        value to append
     */
    @SuppressWarnings("unchecked")
    protected void appendValue(final Object _object,
                               final String _fieldName,
                               final Object _value)
    {
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                final Collection<Object> collection = (Collection<Object>) field.get(_object);
                collection.add(_value);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
    }

    /**
     * Returns the new value for field {@code _fieldName} of {@code _object}.
     *
     * @param _object       object where the field must be updated
     * @param _fieldName    name of the field to update
     * @return found value
     */
    protected Object getValue(final Object _object,
                              final String _fieldName)
    {
        final Object ret;
        try  {
            final Field field = this.getField(_object, _fieldName).field;
            final boolean accessible = field.isAccessible();
            try  {
                field.setAccessible(true);
                ret = field.get(_object);
            } finally  {
                field.setAccessible(accessible);
            }
        } catch (final Exception e)  {
            throw new ParseUpdateError(e);
        }
        return ret;
    }

    /**
     * Searches for given name the field within the object.
     *
     * @param _object       object where the field is searched
     * @param _fieldNames   path of searched fields
     * @return found field with related object
     */
    protected FieldObject getField(final Object _object,
                                   final String... _fieldNames)
    {
        FieldObject ret = new FieldObject();
        ret.object = _object;
        Class<?> clazz = _object.getClass();
        try  {
            ret.field = clazz.getDeclaredField(_fieldNames[0]);
        } catch (final NoSuchFieldException e)  {
        }
        while ((ret.field == null) && (clazz != null))  {
            clazz = clazz.getSuperclass();
            if (clazz != null)  {
                try  {
                    ret.field = clazz.getDeclaredField(_fieldNames[0]);
                } catch (final NoSuchFieldException e)  {
                }
            }
        }
        if ((_fieldNames.length > 1) && (ret.field != null))  {
            final boolean accessible = ret.field.isAccessible();
            final Object object;
            try  {
                ret.field.setAccessible(true);
                 object = ret.field.get(_object);
            } catch (final IllegalAccessException e)  {
                throw new ParseUpdateError(e);
            } finally  {
                ret.field.setAccessible(accessible);
            }
            final String[] newFieldNames = new String[_fieldNames.length - 1];
            System.arraycopy(_fieldNames, 1, newFieldNames, 0, _fieldNames.length - 1);
            ret = this.getField(object, newFieldNames);
        }
        return ret;
    }

    /**
     * Extracts from the parsed string the related Java string (without quotes,
     * backslashes etc.).
     *
     * @param _token    string token
     * @return extracted string
     */
    protected String getString(final String _token)
    {
        return _token
            .replaceAll("^\"", "")
            .replaceAll("\"$", "")
            .replaceAll("\\\\\\\"", "\"")
            .replaceAll("\\\\\\{", "{")
            .replaceAll("\\\\\\}", "}");
    }

    /**
     * Extracts from the parsed single string the related Java string.
     *
     * @param _token    single string token
     * @return extracted single string
     */
    protected String getSingle(final String _token)
    {
        return _token.replaceAll("\\\\\\\"", "\"");
    }

    /**
     * The error is thrown if the object which is currently read could not
     * updated.
     */
    public static class ParseUpdateError
        extends Error
    {
        /** Defines the serialize version unique identifier. */
        private static final long serialVersionUID = -7688744873954882911L;

        /**
         * Default constructor of the parse update error with a
         * <code>_cause</code>.
         *
         * @param _cause    cause of the parse update error
         */
        public ParseUpdateError(final Throwable _cause)
        {
            super(_cause);
        }
    }

    /**
     * Class used to store depending on a field the related object.
     *
     * @see AbstractParser_mxJPO#getField(Object, String...)
     */
    protected static class FieldObject
    {
        /** Field. */
        private Field field;
        /** Object. */
        private Object object;

        /**
         * Returns the {@link #field}.
         *
         * @return field
         */
        public Field getField()
        {
            return this.field;
        }

        /**
         * Returns current value of {@link #field} within {@link #object}.
         *
         * @param <T>   type of value
         * @return current value
         */
        @SuppressWarnings("unchecked")
        public <T> T get()
        {
            final Object ret;
            try  {
                final boolean accessible = this.field.isAccessible();
                try  {
                    this.field.setAccessible(true);
                    ret = this.field.get(this.object);
                } finally  {
                    this.field.setAccessible(accessible);
                }
            } catch (final Exception e)  {
                throw new AbstractParser_mxJPO.ParseUpdateError(e);
            }
            return (T) ret;
        }

        /**
         * Defines new value for given {@link #field} on given {@link #object}.
         *
         * @param <T>       type of the value
         * @param _value    value itself
         */
        public <T> void set(final T _value)
        {
            try  {
                final boolean accessible = this.field.isAccessible();
                try  {
                    this.field.setAccessible(true);
                    this.field.set(this.object, _value);
                } finally  {
                    this.field.setAccessible(accessible);
                }
            } catch (final Exception e)  {
                throw new AbstractParser_mxJPO.ParseUpdateError(e);
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////
// generated source code from JavaCC which is imported from all parsers

    /**
     * This exception is thrown when parse errors are encountered.
     * You can explicitly create objects of this exception type by
     * calling the method generateParseException in the generated
     * parser.
     */
    public final static class ParseException
        extends Exception
    {
        /** Dummy version identifier for this Serializable class.*/
        private static final long serialVersionUID = 1L;
        /** The end of line string for this machine. */
        protected static String EOL = System.getProperty("line.separator", "\n");

        /**
         * This constructor is used by the method "generateParseException"
         * in the generated parser.    Calling this constructor generates
         * a new object of this type with the fields "currentToken",
         * "expectedTokenSequences", and "tokenImage" set.
         */
        public ParseException(final Token currentTokenVal,
                              final int[][] expectedTokenSequencesVal,
                              final String[] tokenImageVal)
        {
            super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
            this.currentToken = currentTokenVal;
            this.expectedTokenSequences = expectedTokenSequencesVal;
            this.tokenImage = tokenImageVal;
        }

        /**
         * The following constructors are for use by you for whatever
         * purpose you can think of.    Constructing the exception in this
         * manner makes the exception behave in the normal way - i.e., as
         * documented in the class "Throwable".    The fields "errorToken",
         * "expectedTokenSequences", and "tokenImage" do not contain
         * relevant information.    The JavaCC generated code does not use
         * these constructors.
         */
        public ParseException()
        {
            super();
        }

        /** Constructor with message. */
        public ParseException(final String message)
        {
            super(message);
        }

        /**
         * This is the last token that has been consumed successfully. If
         * this object has been created due to a parse error, the token
         * following this token will (therefore) be the first error token.
         */
        public Token currentToken;

        /**
         * Each entry in this array is an array of integers.    Each array
         * of integers represents a sequence of tokens (by their ordinal
         * values) that is expected at this point of the parse.
         */
        public int[][] expectedTokenSequences;

        /**
         * This is a reference to the "tokenImage" array of the generated
         * parser within which the parse error occurred.    This array is
         * defined in the generated ...Constants interface.
         */
        public String[] tokenImage;

        /**
         * It uses "currentToken" and "expectedTokenSequences" to generate a parse
         * error message and returns it.    If this object has been created
         * due to a parse error, and you do not catch it (it gets thrown
         * from the parser) the correct error message
         * gets displayed.
         */
        private static String initialise(final Token currentToken,
                                         final int[][] expectedTokenSequences,
                                         final String[] tokenImage)
        {
            final StringBuffer expected = new StringBuffer();
            int maxSize = 0;
            for (final int[] expectedTokenSequence : expectedTokenSequences)  {
                if (maxSize < expectedTokenSequence.length) {
                    maxSize = expectedTokenSequence.length;
                }
                for (final int element : expectedTokenSequence)  {
                    expected.append(tokenImage[element]).append(' ');
                }
                if (expectedTokenSequence[expectedTokenSequence.length - 1] != 0)  {
                    expected.append("...");
                }
                expected.append(ParseException.EOL).append("        ");
            }
            String retval = "Encountered \"";
            Token tok = currentToken.next;
            for (int i = 0; i < maxSize; i++)  {
                if (i != 0)  {
                    retval += " ";
                }
                if (tok.kind == 0)  {
                    retval += tokenImage[0];
                    break;
                }
                retval += " " + tokenImage[tok.kind];
                retval += " \"";
                retval += add_escapes(tok.image);
                retval += " \"";
                tok = tok.next;
            }
            retval += "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn;
            retval += "." + ParseException.EOL;

            if (expectedTokenSequences.length > 0)  {
                if (expectedTokenSequences.length == 1) {
                    retval += "Was expecting:" + ParseException.EOL + "        ";
                } else {
                    retval += "Was expecting one of:" + ParseException.EOL + "        ";
                }
                retval += expected.toString();
            }

            return retval;
        }


        /**
         * Used to convert raw characters to their escaped version
         * when these raw version cannot be used as part of an ASCII
         * string literal.
         */
        private static String add_escapes(final String str)
        {
            final StringBuffer retval = new StringBuffer();
            char ch;
            for (int i = 0; i < str.length(); i++) {
                switch (str.charAt(i))  {
                     case '\b':
                         retval.append("\\b");
                         continue;
                     case '\t':
                         retval.append("\\t");
                         continue;
                     case '\n':
                         retval.append("\\n");
                         continue;
                     case '\f':
                         retval.append("\\f");
                         continue;
                     case '\r':
                         retval.append("\\r");
                         continue;
                     case '\"':
                         retval.append("\\\"");
                         continue;
                     case '\'':
                         retval.append("\\\'");
                         continue;
                     case '\\':
                         retval.append("\\\\");
                         continue;
                     default:
                         if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                             final String s = "0000" + Integer.toString(ch, 16);
                             retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                         } else {
                             retval.append(ch);
                         }
                         continue;
                }
            }
            return retval.toString();
         }
    }

    /**
     * An implementation of interface CharStream, where the stream is assumed to
     * contain only ASCII characters (without unicode processing).
     */
    public final static class SimpleCharStream
    {
        /** Whether parser is static. */
        public static final boolean staticFlag = false;
        int bufsize;
        int available;
        int tokenBegin;
        /** Position in buffer. */
        public int bufpos = -1;
        protected int bufline[];
        protected int bufcolumn[];

        protected int column = 0;
        protected int line = 1;

        protected boolean prevCharIsCR = false;
        protected boolean prevCharIsLF = false;

        protected java.io.Reader inputStream;

        protected char[] buffer;
        protected int maxNextCharInd = 0;
        protected int inBuf = 0;
        protected int tabSize = 1;
        protected boolean trackLineColumn = true;

        public void setTabSize(final int i) { this.tabSize = i; }
        public int getTabSize() { return this.tabSize; }

        protected void ExpandBuff(final boolean wrapAround)
        {
            final char[] newbuffer = new char[this.bufsize + 2048];
            final int newbufline[] = new int[this.bufsize + 2048];
            final int newbufcolumn[] = new int[this.bufsize + 2048];

            try  {
                if (wrapAround)  {
                    System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                    System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
                    this.buffer = newbuffer;

                    System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                    System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
                    this.bufline = newbufline;

                    System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                    System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
                    this.bufcolumn = newbufcolumn;

                    this.maxNextCharInd = (this.bufpos += (this.bufsize - this.tokenBegin));
                } else  {
                    System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                    this.buffer = newbuffer;

                    System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                    this.bufline = newbufline;

                    System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                    this.bufcolumn = newbufcolumn;

                    this.maxNextCharInd = (this.bufpos -= this.tokenBegin);
                }
            }  catch (final Throwable t)  {
                throw new Error(t.getMessage());
            }

            this.bufsize += 2048;
            this.available = this.bufsize;
            this.tokenBegin = 0;
        }

        protected void FillBuff()
            throws java.io.IOException
        {
            if (this.maxNextCharInd == this.available)  {
                if (this.available == this.bufsize)  {
                    if (this.tokenBegin > 2048)  {
                        this.bufpos = this.maxNextCharInd = 0;
                        this.available = this.tokenBegin;
                    }  else if (this.tokenBegin < 0)  {
                        this.bufpos = this.maxNextCharInd = 0;
                    } else  {
                        this.ExpandBuff(false);
                    }
                }  else if (this.available > this.tokenBegin)  {
                    this.available = this.bufsize;
                } else if ((this.tokenBegin - this.available) < 2048)  {
                    this.ExpandBuff(true);
                } else {
                    this.available = this.tokenBegin;
                }
            }

            int i;
            try {
                if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1)  {
                    this.inputStream.close();
                    throw new java.io.IOException();
                } else  {
                    this.maxNextCharInd += i;
                }
                return;
            }  catch(final java.io.IOException e)  {
                --this.bufpos;
                this.backup(0);
                if (this.tokenBegin == -1)  {
                    this.tokenBegin = this.bufpos;
                }
                throw e;
            }
        }

        /** Start. */
        public char BeginToken()
            throws java.io.IOException
        {
          this.tokenBegin = -1;
          final char c = this.readChar();
          this.tokenBegin = this.bufpos;

          return c;
        }

        protected void UpdateLineColumn(final char c)
        {
            this.column++;

            if (this.prevCharIsLF)  {
                this.prevCharIsLF = false;
                this.line += (this.column = 1);
            } else if (this.prevCharIsCR)  {
                this.prevCharIsCR = false;
                if (c == '\n')  {
                    this.prevCharIsLF = true;
                } else  {
                    this.line += (this.column = 1);
                }
            }

            switch (c)  {
                case '\r' :
                    this.prevCharIsCR = true;
                    break;
                case '\n' :
                    this.prevCharIsLF = true;
                    break;
                case '\t' :
                    this.column--;
                    this.column += (this.tabSize - (this.column % this.tabSize));
                    break;
                default :
                    break;
            }

            this.bufline[this.bufpos] = this.line;
            this.bufcolumn[this.bufpos] = this.column;
        }

        /** Read a character. */
        public char readChar()
            throws java.io.IOException
        {
            if (this.inBuf > 0)  {
                --this.inBuf;

                if (++this.bufpos == this.bufsize)  {
                    this.bufpos = 0;
                }

                return this.buffer[this.bufpos];
            }

            if (++this.bufpos >= this.maxNextCharInd)  {
                this.FillBuff();
            }

            final char c = this.buffer[this.bufpos];

            this.UpdateLineColumn(c);
            return c;
        }

        /** Get token end column number. */
        public int getEndColumn()
        {
          return this.bufcolumn[this.bufpos];
        }

        /** Get token end line number. */
        public int getEndLine()
        {
           return this.bufline[this.bufpos];
        }

        /** Get token beginning column number. */
        public int getBeginColumn()
        {
          return this.bufcolumn[this.tokenBegin];
        }

        /** Get token beginning line number. */
        public int getBeginLine()
        {
          return this.bufline[this.tokenBegin];
        }

      /** Backup a number of characters. */
        public void backup(final int amount)
        {
            this.inBuf += amount;
            if ((this.bufpos -= amount) < 0)  {
                this.bufpos += this.bufsize;
            }
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.Reader dstream,
                                final int startline,
                                final int startcolumn,
                                final int buffersize)
        {
          this.inputStream = dstream;
          this.line = startline;
          this.column = startcolumn - 1;

          this.available = this.bufsize = buffersize;
          this.buffer = new char[buffersize];
          this.bufline = new int[buffersize];
          this.bufcolumn = new int[buffersize];
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.Reader dstream,
                                final int startline,
                                final int startcolumn)
        {
          this(dstream, startline, startcolumn, 4096);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.Reader dstream)
        {
          this(dstream, 1, 1, 4096);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.Reader dstream, final int startline,
        final int startcolumn, final int buffersize)
        {
          this.inputStream = dstream;
          this.line = startline;
          this.column = startcolumn - 1;

          if (this.buffer == null || buffersize != this.buffer.length)
          {
            this.available = this.bufsize = buffersize;
            this.buffer = new char[buffersize];
            this.bufline = new int[buffersize];
            this.bufcolumn = new int[buffersize];
          }
          this.prevCharIsLF = this.prevCharIsCR = false;
          this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
          this.bufpos = -1;
        }

        /** Reinitialise. */
        public void ReInit(final java.io.Reader dstream, final int startline,
                           final int startcolumn)
        {
          this.ReInit(dstream, startline, startcolumn, 4096);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.Reader dstream)
        {
          this.ReInit(dstream, 1, 1, 4096);
        }
        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream, final String encoding, final int startline,
        final int startcolumn, final int buffersize) throws java.io.UnsupportedEncodingException
        {
          this(encoding == null ? new java.io.InputStreamReader(dstream) : new java.io.InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream, final int startline,
        final int startcolumn, final int buffersize)
        {
          this(new java.io.InputStreamReader(dstream), startline, startcolumn, buffersize);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream, final String encoding, final int startline,
                                final int startcolumn) throws java.io.UnsupportedEncodingException
        {
          this(dstream, encoding, startline, startcolumn, 4096);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream, final int startline,
                                final int startcolumn)
        {
            this(dstream, startline, startcolumn, 4096);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream, final String encoding) throws java.io.UnsupportedEncodingException
        {
            this(dstream, encoding, 1, 1, 4096);
        }

        /** Constructor. */
        public SimpleCharStream(final java.io.InputStream dstream)
        {
            this(dstream, 1, 1, 4096);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream, final String encoding, final int startline,
                                final int startcolumn, final int buffersize) throws java.io.UnsupportedEncodingException
        {
            this.ReInit(encoding == null ? new java.io.InputStreamReader(dstream) : new java.io.InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream, final int startline,
                                final int startcolumn, final int buffersize)
        {
            this.ReInit(new java.io.InputStreamReader(dstream), startline, startcolumn, buffersize);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream, final String encoding) throws java.io.UnsupportedEncodingException
        {
            this.ReInit(dstream, encoding, 1, 1, 4096);
        }

        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream)
        {
            this.ReInit(dstream, 1, 1, 4096);
        }
        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream, final String encoding, final int startline,
                           final int startcolumn) throws java.io.UnsupportedEncodingException
        {
            this.ReInit(dstream, encoding, startline, startcolumn, 4096);
        }
        /** Reinitialise. */
        public void ReInit(final java.io.InputStream dstream, final int startline,
                           final int startcolumn)
        {
            this.ReInit(dstream, startline, startcolumn, 4096);
        }
        /** Get token literal value. */
        public String GetImage()
        {
            if (this.bufpos >= this.tokenBegin)  {
                return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
            } else  {
                return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) +
                                  new String(this.buffer, 0, this.bufpos + 1);
            }
        }

        /** Get the suffix. */
        public char[] GetSuffix(final int len)
        {
            final char[] ret = new char[len];
            if ((this.bufpos + 1) >= len)  {
                System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
            } else  {
                System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, len - this.bufpos - 1);
                System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
            }
            return ret;
        }

        /** Reset buffer when finished. */
        public void Done()
        {
            this.buffer = null;
            this.bufline = null;
            this.bufcolumn = null;
        }

        /**
         * Method to adjust line and column numbers for the start of a token.
         */
        public void adjustBeginLineColumn(int newLine, final int newCol)
        {
            int start = this.tokenBegin;
            int len;

            if (this.bufpos >= this.tokenBegin)  {
                len = this.bufpos - this.tokenBegin + this.inBuf + 1;
            } else  {
                len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
            }

            int i = 0, j = 0, k = 0;
            int nextColDiff = 0, columnDiff = 0;

            while (i < len && this.bufline[j = start % this.bufsize] == this.bufline[k = ++start % this.bufsize])  {
                this.bufline[j] = newLine;
                nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
                this.bufcolumn[j] = newCol + columnDiff;
                columnDiff = nextColDiff;
                i++;
            }

            if (i < len)  {
                this.bufline[j] = newLine++;
                this.bufcolumn[j] = newCol + columnDiff;

                while (i++ < len)  {
                    if (this.bufline[j = start % this.bufsize] != this.bufline[++start % this.bufsize]) {
                        this.bufline[j] = newLine++;
                    } else  {
                        this.bufline[j] = newLine;
                    }
                }
            }

            this.line = this.bufline[j];
            this.column = this.bufcolumn[j];
        }

        boolean getTrackLineColumn()
        {
            return this.trackLineColumn;
        }

        void setTrackLineColumn(final boolean tlc)
        {
            this.trackLineColumn = tlc;
        }
    }

    /**
     * Describes the input token stream.
     */
    public final static class Token
        implements java.io.Serializable
    {
        /** Dummy version identifier for this Serializable class.*/
        private static final long serialVersionUID = 1L;

        /**
         * An integer that describes the kind of this token.  This numbering
         * system is determined by JavaCCParser, and a table of these numbers is
         * stored in the file ...Constants.java.
         */
        public int kind;

        /** The line number of the first character of this Token. */
        public int beginLine;
        /** The column number of the first character of this Token. */
        public int beginColumn;
        /** The line number of the last character of this Token. */
        public int endLine;
        /** The column number of the last character of this Token. */
        public int endColumn;

        /** The string image of the token. */
        public String image;

        /**
         * A reference to the next regular (non-special) token from the input
         * stream.  If this is the last token from the input stream, or if the
         * token manager has not read tokens beyond this one, this field is
         * set to null.  This is true only if this token is also a regular
         * token.  Otherwise, see below for a description of the contents of
         * this field.
         */
        public Token next;

      /**
       * This field is used to access special tokens that occur prior to this
       * token, but after the immediately preceding regular (non-special) token.
       * If there are no such special tokens, this field is set to null.
       * When there are more than one such special token, this field refers
       * to the last of these special tokens, which in turn refers to the next
       * previous special token through its specialToken field, and so on
       * until the first special token (whose specialToken field is null).
       * The next fields of special tokens refer to other special tokens that
       * immediately follow it (without an intervening regular token).  If there
       * is no such token, this field is null.
       */
      public Token specialToken;

      /**
       * An optional attribute value of the Token.
       * Tokens which are not used as syntactic sugar will often contain
       * meaningful values that will be used later on by the compiler or
       * interpreter. This attribute value is often different from the image.
       * Any subclass of Token that actually wants to return a non-null value can
       * override this method as appropriate.
       */
      public Object getValue() {
        return null;
      }

      /**
       * No-argument constructor
       */
      public Token() {}

      /**
       * Constructs a new token for the specified Image.
       */
      public Token(final int kind)
      {
        this(kind, null);
      }

      /**
       * Constructs a new token for the specified Image and Kind.
       */
      public Token(final int kind, final String image)
      {
        this.kind = kind;
        this.image = image;
      }

      /**
       * Returns the image.
       */
      @Override()
      public String toString()
      {
        return this.image;
      }

      /**
       * Returns a new Token object, by default. However, if you want, you
       * can create and return subclass objects based on the value of ofKind.
       * Simply add the cases to the switch for all those special cases.
       * For example, if you have a subclass of Token called IDToken that
       * you want to create if ofKind is ID, simply add something like :
       *
       *    case MyParserConstants.ID : return new IDToken(ofKind, image);
       *
       * to the following switch statement. Then you can cast matchedToken
       * variable to the appropriate type and use sit in your lexical actions.
       */
      public static Token newToken(final int ofKind, final String image)
      {
        switch(ofKind)
        {
          default : return new Token(ofKind, image);
        }
      }

      public static Token newToken(final int ofKind)
      {
        return newToken(ofKind, null);
      }
    }

    /**
     * Token Manager Error.
     */
    public final static class TokenMgrError
        extends Error
    {
        /** Dummy version identifier for this Serializable class.*/
        private static final long serialVersionUID = 1L;

        // Ordinals for various reasons why an Error of this type can be thrown.

        /** Lexical error occurred. */
        public static final int LEXICAL_ERROR = 0;
        /** An attempt was made to create a second instance of a static token manager. */
        public static final int STATIC_LEXER_ERROR = 1;
        /** Tried to change to an invalid lexical state. */
        public static final int INVALID_LEXICAL_STATE = 2;
        /** Detected (and bailed out of) an infinite loop in the token manager. */
        public static final int LOOP_DETECTED = 3;

        /** Indicates the reason why the exception is thrown. It will have  one of the above 4 values.*/
        int errorCode;

        /**
         * Replaces unprintable characters by their escaped (or unicode escaped)
         * equivalents in the given string
         */
        protected static final String addEscapes(final String str)
        {
            final StringBuffer retval = new StringBuffer();
            char ch;
            for (int i = 0; i < str.length(); i++) {
              switch (str.charAt(i))
              {
                case '\b':
                  retval.append("\\b");
                  continue;
                case '\t':
                  retval.append("\\t");
                  continue;
                case '\n':
                  retval.append("\\n");
                  continue;
                case '\f':
                  retval.append("\\f");
                  continue;
                case '\r':
                  retval.append("\\r");
                  continue;
                case '\"':
                  retval.append("\\\"");
                  continue;
                case '\'':
                  retval.append("\\\'");
                  continue;
                case '\\':
                  retval.append("\\\\");
                  continue;
                default:
                  if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                    final String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                  } else {
                    retval.append(ch);
                  }
                  continue;
              }
            }
            return retval.toString();
        }

        /**
         * Returns a detailed message for the Error when it is thrown by the
         * token manager to indicate a lexical error.
         * Parameters :
         *    EOFSeen     : indicates if EOF caused the lexical error
         *    curLexState : lexical state in which this error occurred
         *    errorLine   : line number when the error occurred
         *    errorColumn : column number when the error occurred
         *    errorAfter  : prefix that was seen before this error occurred
         *    curchar     : the offending character
         * Note: You can customize the lexical error message by modifying this method.
         */
        protected static String LexicalErr(final boolean EOFSeen, final int lexState, final int errorLine, final int errorColumn, final String errorAfter, final int curChar)
        {
            final char curChar1 = (char)curChar;
            return("Lexical error at line " +
                  errorLine + ", column " +
                  errorColumn + ".  Encountered: " +
                  (EOFSeen ? "<EOF> " : ("\"" + addEscapes(String.valueOf(curChar1)) + "\"") + " (" + curChar + "), ") +
                  "after : \"" + addEscapes(errorAfter) + "\"");
        }

        /**
         * You can also modify the body of this method to customize your error messages.
         * For example, cases like LOOP_DETECTED and INVALID_LEXICAL_STATE are not
         * of end-users concern, so you can return something like :
         *
         *     "Internal Error : Please file a bug report .... "
         *
         * from this method for such cases in the release version of your parser.
         */
        @Override()
        public String getMessage()
        {
            return super.getMessage();
        }

        /** No arg constructor. */
        public TokenMgrError()
        {
        }

        /** Constructor with message and reason. */
        public TokenMgrError(final String message, final int reason)
        {
            super(message);
            this.errorCode = reason;
        }

        /** Full Constructor. */
        public TokenMgrError(final boolean EOFSeen, final int lexState, final int errorLine, final int errorColumn, final String errorAfter, final int curChar, final int reason)
        {
            this(LexicalErr(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
        }
    }
}
