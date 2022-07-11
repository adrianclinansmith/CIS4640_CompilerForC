/*
  Created By: Fei Song, Adrian Clinansmith
  File Name: ScannerGenerator.flex
*/
   
/* --------------------------Usercode Section------------------------ */
   
import java_cup.runtime.*;
      
%%
   
/* -----------------Options and Declarations Section----------------- */
   
/* 
   The name of the class JFlex will create will be Lexer.
   Will write the code to the file Lexer.java. 
*/
%class Lexer

%eofval{
  return null;
%eofval};

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column
    
/* 
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup

%state IN_COMMENT
   
/*
  Declarations
   
  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.  
*/
%{   
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this
       case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%} 

/*
  Macro Declarations
  
  These declarations are regular expressions that will be used
  in the Lexical Rules Section.  
*/
   
/* White space is a line terminator, space, tab, or form feed. */
WhiteSpace = [\r\n\t\f" "]+
   
/* A literal integer is is a number beginning with a number between
   one and nine followed by zero or more numbers between zero and nine
   or just a zero.  */
number = [0-9]+
   
/* A identifier integer is a word beginning a letter between A and
   Z, a and z, or an underscore followed by zero or more letters
   between A and Z, a and z, zero and nine, or an underscore. */
identifier = [a-zA-Z][_a-zA-Z0-9]*
   
%%
/* ------------------------Lexical Rules Section---------------------- */
   
/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. 
*/

// Initial / default state
<YYINITIAL> {
   // C- Keywords
   "/*"              { yybegin(IN_COMMENT); } 
   "if"              { return symbol(sym.IF); }
   "else"            { return symbol(sym.ELSE); }
   "int"             { return symbol(sym.INT); }
   "return"          { return symbol(sym.RETURN); }
   "void"            { return symbol(sym.VOID); }
   "while"           { return symbol(sym.WHILE); }
   // C- Special Symbols
   "+"         { return symbol(sym.PLUS); }
   "-"         { return symbol(sym.MINUS); }
   "*"         { return symbol(sym.TIMES); }
   "/"         { return symbol(sym.DIVIDE); }
   "<"         { return symbol(sym.LESS); }
   "<="        { return symbol(sym.LESS_EQ); }
   ">"         { return symbol(sym.GREATER); }
   "=>"        { return symbol(sym.GREATER_EQ); }
   "=="        { return symbol(sym.EQUAL); }
   "!="        { return symbol(sym.NOT_EQUAL); }
   "="         { return symbol(sym.ASSIGN); }
   ";"         { return symbol(sym.SEMI); }
   ","         { return symbol(sym.COMMA); }
   "("         { return symbol(sym.OPEN_PAREN); }
   ")"         { return symbol(sym.CLOSE_PAREN); }
   "["         { return symbol(sym.OPEN_BRACKET); }
   "]"         { return symbol(sym.CLOSE_BRACKET); }
   "{"         { return symbol(sym.OPEN_BRACE); }
   "}"         { return symbol(sym.CLOSE_BRACE); }
   // Other Tokens
   {number}           { return symbol(sym.NUM, yytext()); }
   {identifier}       { return symbol(sym.ID, yytext()); }
   {WhiteSpace}      { /* skip whitespace */ }   
   .                  { return symbol(sym.ERROR); }
}

// Inside C- comment 
<IN_COMMENT> {
   "*/"    { yybegin(YYINITIAL); }
   [^*]+   { /* eat comment in chunks */ }
   "*"     { /* eat the lone star */ }
}
