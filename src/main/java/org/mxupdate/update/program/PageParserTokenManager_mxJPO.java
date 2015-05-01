/* PageParserTokenManager.java */
/* Generated By:JavaCC: Do not edit this line. PageParserTokenManager.java */
package org.mxupdate.update.program;
import java.lang.reflect.InvocationTargetException;
import org.mxupdate.update.util.AbstractParser_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.AbstractParser_mxJPO.SimpleCharStream;
import org.mxupdate.update.util.AbstractParser_mxJPO.Token;
import org.mxupdate.update.util.AbstractParser_mxJPO.TokenMgrError;
import org.mxupdate.update.util.AdminPropertyList_mxJPO.AdminProperty;

/** Token Manager. */
@SuppressWarnings("unused")class PageParserTokenManager_mxJPO implements PageParserConstants_mxJPO {

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_3(int pos, long active0){
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_3(int pos, long active0){
   return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_3(){
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa1_3(0x80L);
      case 99:
         return jjMoveStringLiteralDfa1_3(0x200L);
      case 100:
         return jjMoveStringLiteralDfa1_3(0x20L);
      case 104:
         return jjMoveStringLiteralDfa1_3(0x40L);
      case 109:
         return jjMoveStringLiteralDfa1_3(0x100L);
      case 112:
         return jjMoveStringLiteralDfa1_3(0x4000L);
      case 116:
         return jjMoveStringLiteralDfa1_3(0x8000L);
      case 118:
         return jjMoveStringLiteralDfa1_3(0x10000L);
      default :
         return jjMoveNfa_3(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_3(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa2_3(active0, 0x10000L);
      case 101:
         return jjMoveStringLiteralDfa2_3(active0, 0x20L);
      case 104:
         return jjMoveStringLiteralDfa2_3(active0, 0x80L);
      case 105:
         return jjMoveStringLiteralDfa2_3(active0, 0x140L);
      case 111:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(1, 15);
         return jjMoveStringLiteralDfa2_3(active0, 0x200L);
      case 114:
         return jjMoveStringLiteralDfa2_3(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_3(0, active0);
}
private int jjMoveStringLiteralDfa2_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa3_3(active0, 0x40L);
      case 105:
         return jjMoveStringLiteralDfa3_3(active0, 0x80L);
      case 108:
         return jjMoveStringLiteralDfa3_3(active0, 0x10000L);
      case 109:
         return jjMoveStringLiteralDfa3_3(active0, 0x100L);
      case 110:
         return jjMoveStringLiteralDfa3_3(active0, 0x200L);
      case 111:
         return jjMoveStringLiteralDfa3_3(active0, 0x4000L);
      case 115:
         return jjMoveStringLiteralDfa3_3(active0, 0x20L);
      default :
         break;
   }
   return jjStartNfa_3(1, active0);
}
private int jjMoveStringLiteralDfa3_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa4_3(active0, 0x20L);
      case 100:
         return jjMoveStringLiteralDfa4_3(active0, 0xc0L);
      case 101:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(3, 8);
         break;
      case 112:
         return jjMoveStringLiteralDfa4_3(active0, 0x4000L);
      case 116:
         return jjMoveStringLiteralDfa4_3(active0, 0x200L);
      case 117:
         return jjMoveStringLiteralDfa4_3(active0, 0x10000L);
      default :
         break;
   }
   return jjStartNfa_3(2, active0);
}
private int jjMoveStringLiteralDfa4_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa5_3(active0, 0x80L);
      case 101:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(4, 16);
         return jjMoveStringLiteralDfa5_3(active0, 0x4240L);
      case 114:
         return jjMoveStringLiteralDfa5_3(active0, 0x20L);
      default :
         break;
   }
   return jjStartNfa_3(3, active0);
}
private int jjMoveStringLiteralDfa5_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa6_3(active0, 0x80L);
      case 105:
         return jjMoveStringLiteralDfa6_3(active0, 0x20L);
      case 110:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(5, 6);
         return jjMoveStringLiteralDfa6_3(active0, 0x200L);
      case 114:
         return jjMoveStringLiteralDfa6_3(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_3(4, active0);
}
private int jjMoveStringLiteralDfa6_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 110:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(6, 7);
         break;
      case 112:
         return jjMoveStringLiteralDfa7_3(active0, 0x20L);
      case 116:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(6, 9);
         return jjMoveStringLiteralDfa7_3(active0, 0x4000L);
      default :
         break;
   }
   return jjStartNfa_3(5, active0);
}
private int jjMoveStringLiteralDfa7_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 116:
         return jjMoveStringLiteralDfa8_3(active0, 0x20L);
      case 121:
         if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(7, 14);
         break;
      default :
         break;
   }
   return jjStartNfa_3(6, active0);
}
private int jjMoveStringLiteralDfa8_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa9_3(active0, 0x20L);
      default :
         break;
   }
   return jjStartNfa_3(7, active0);
}
private int jjMoveStringLiteralDfa9_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa10_3(active0, 0x20L);
      default :
         break;
   }
   return jjStartNfa_3(8, active0);
}
private int jjMoveStringLiteralDfa10_3(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_3(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 110:
         if ((active0 & 0x20L) != 0L)
            return jjStopAtPos(10, 5);
         break;
      default :
         break;
   }
   return jjStartNfa_3(9, active0);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_3(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 3;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (curChar == 35)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 1:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 2:
                  if (curChar == 10)
                     kind = 4;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  { jjAddStates(0, 1); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 1); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_0(int pos, long active0){
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0){
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjMoveStringLiteralDfa0_0(){
   switch(curChar)
   {
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 12;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xfffffffa00000000L & l) != 0L)
                  {
                     if (kind > 18)
                        kind = 18;
                     { jjCheckNAdd(11); }
                  }
                  else if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  if (curChar == 35)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 1:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 2:
                  if (curChar == 10)
                     kind = 4;
                  break;
               case 3:
               case 7:
                  if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 4:
                  if ((0xfffffffb00000000L & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 5:
                  if (curChar == 34 && kind > 17)
                     kind = 17;
                  break;
               case 11:
                  if ((0xfffffffa00000000L & l) == 0L)
                     break;
                  if (kind > 18)
                     kind = 18;
                  { jjCheckNAdd(11); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 11:
                  if ((0xd7ffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 18)
                     kind = 18;
                  { jjCheckNAdd(11); }
                  break;
               case 1:
                  { jjAddStates(0, 1); }
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 6:
                  if (curChar == 92)
                     { jjAddStates(5, 8); }
                  break;
               case 8:
                  if (curChar == 92)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 9:
                  if (curChar == 123)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 10:
                  if (curChar == 125)
                     { jjCheckNAddStates(2, 4); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 11:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 18)
                     kind = 18;
                  { jjCheckNAdd(11); }
                  break;
               case 1:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 1); }
                  break;
               case 4:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     { jjAddStates(2, 4); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 12 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_1(int pos, long active0){
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_1(int pos, long active0){
   return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
}
private int jjMoveStringLiteralDfa0_1(){
   switch(curChar)
   {
      default :
         return jjMoveNfa_1(0, 0);
   }
}
private int jjMoveNfa_1(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 14;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xfffffffa00000000L & l) != 0L)
                  {
                     if (kind > 13)
                        kind = 13;
                     { jjCheckNAdd(13); }
                  }
                  else if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  if (curChar == 35)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 1:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 2:
                  if (curChar == 10 && kind > 4)
                     kind = 4;
                  break;
               case 3:
               case 7:
                  if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 4:
                  if ((0xfffffffb00000600L & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 5:
                  if (curChar == 34 && kind > 12)
                     kind = 12;
                  break;
               case 13:
                  if ((0xfffffffa00000000L & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  { jjCheckNAdd(13); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 13:
                  if ((0xd7ffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  { jjCheckNAdd(13); }
                  break;
               case 1:
                  { jjAddStates(0, 1); }
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 6:
                  if (curChar == 92)
                     { jjAddStates(9, 14); }
                  break;
               case 8:
                  if (curChar == 92)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 9:
                  if (curChar == 123)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 10:
                  if (curChar == 125)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 11:
                  if (curChar == 110)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 12:
                  if (curChar == 116)
                     { jjCheckNAddStates(2, 4); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 13:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 13)
                     kind = 13;
                  { jjCheckNAdd(13); }
                  break;
               case 1:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 1); }
                  break;
               case 4:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     { jjAddStates(2, 4); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 14 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_2(int pos, long active0){
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_2(int pos, long active0){
   return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
}
private int jjMoveStringLiteralDfa0_2(){
   switch(curChar)
   {
      default :
         return jjMoveNfa_2(0, 0);
   }
}
private int jjMoveNfa_2(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 12;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xfffffffa00000000L & l) != 0L)
                  {
                     if (kind > 11)
                        kind = 11;
                     { jjCheckNAdd(11); }
                  }
                  else if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  if (curChar == 35)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 1:
                  if ((0xfffffffffffffbffL & l) != 0L)
                     { jjCheckNAddTwoStates(1, 2); }
                  break;
               case 2:
                  if (curChar == 10)
                     kind = 4;
                  break;
               case 3:
               case 7:
                  if (curChar == 34)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 4:
                  if ((0xfffffffb00000000L & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 5:
                  if (curChar == 34 && kind > 10)
                     kind = 10;
                  break;
               case 11:
                  if ((0xfffffffa00000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  { jjCheckNAdd(11); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 11:
                  if ((0xd7ffffffefffffffL & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  { jjCheckNAdd(11); }
                  break;
               case 1:
                  { jjAddStates(0, 1); }
                  break;
               case 4:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 6:
                  if (curChar == 92)
                     { jjAddStates(5, 8); }
                  break;
               case 8:
                  if (curChar == 92)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 9:
                  if (curChar == 123)
                     { jjCheckNAddStates(2, 4); }
                  break;
               case 10:
                  if (curChar == 125)
                     { jjCheckNAddStates(2, 4); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 11:
                  if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 11)
                     kind = 11;
                  { jjCheckNAdd(11); }
                  break;
               case 1:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 1); }
                  break;
               case 4:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                     { jjAddStates(2, 4); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 12 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   1, 2, 4, 5, 6, 7, 8, 9, 10, 7, 8, 9, 10, 11, 12, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         return false;
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, "\144\145\163\143\162\151\160\164\151\157\156", 
"\150\151\144\144\145\156", "\41\150\151\144\144\145\156", "\155\151\155\145", 
"\143\157\156\164\145\156\164", null, null, null, null, "\160\162\157\160\145\162\164\171", "\164\157", 
"\166\141\154\165\145", null, null, };
protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 3;
int defaultLexState = 3;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(Exception e)
   {
      jjmatchedKind = 0;
      jjmatchedPos = -1;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   switch(curLexState)
   {
     case 0:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
             curChar = input_stream.BeginToken();
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_0();
       break;
     case 1:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
             curChar = input_stream.BeginToken();
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_1();
       break;
     case 2:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
             curChar = input_stream.BeginToken();
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_2();
       break;
     case 3:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
             curChar = input_stream.BeginToken();
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_3();
       break;
   }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else
        {
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

    /** Constructor. */
    public PageParserTokenManager_mxJPO(SimpleCharStream stream){

      if (SimpleCharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");

    input_stream = stream;
  }

  /** Constructor. */
  public PageParserTokenManager_mxJPO (SimpleCharStream stream, int lexState){
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Reinitialise parser. */
  public void ReInit(SimpleCharStream stream)
  {
	
    jjmatchedPos = jjnewStateCnt = 0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    int i;
    jjround = 0x80000001;
    for (i = 14; i-- > 0;)
      jjrounds[i] = 0x80000000;
  }

  /** Reinitialise parser. */
  public void ReInit( SimpleCharStream stream, int lexState)
  {
  
    ReInit( stream);
    SwitchTo(lexState);
  }

  /** Switch to specified lex state. */
  public void SwitchTo(int lexState)
  {
    if (lexState >= 4 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
    else
      curLexState = lexState;
  }

/** Lexer state names. */
public static final String[] lexStateNames = {
   "ADMINREF_EXPECTED",
   "MULTILINESTRING_EXPECTED",
   "STRING_EXPECTED",
   "DEFAULT",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, 1, -1, -1, 2, 1, 3, 3, 3, 3, 2, 0, 2, 2, 2, 
};
static final long[] jjtoToken = {
   0x7ffe1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
    protected SimpleCharStream  input_stream;

    private final int[] jjrounds = new int[14];
    private final int[] jjstateSet = new int[2 * 14];

    
    protected int curChar;
}
