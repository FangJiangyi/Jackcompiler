package jackanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JackTokenizer {
    private BufferedReader br;
    private char[] currenttokens;
    private int nextchar;
    private String type;
    private static final HashMap<String, KeyWord> Keyword = new HashMap<String, KeyWord>() {
        {
            put("class", KeyWord.CLASS);
            put("method", KeyWord.METHOD);
            put("function", KeyWord.FUNCTION);
            put("constructor", KeyWord.CONSTRUCTOR);
            put("int", KeyWord.INT);
            put("boolean", KeyWord.BOOLEAN);
            put("char", KeyWord.CHAR);
            put("void", KeyWord.VOID);
            put("var", KeyWord.VAR);
            put("static", KeyWord.STATIC);
            put("field", KeyWord.FIELD);
            put("let", KeyWord.LET);
            put("do", KeyWord.DO);
            put("if", KeyWord.IF);
            put("else", KeyWord.ELSE);
            put("while", KeyWord.WHILE);
            put("return", KeyWord.RETURN);
            put("true", KeyWord.TRUE);
            put("false", KeyWord.FALSE);
            put("null", KeyWord.NULL);
            put("this", KeyWord.THIS);
        }
    };
    private static final HashSet<Character> symbol = new HashSet<Character>() {
        {
            add('[');
            add(']');
            add('{');
            add('}');
            add('(');
            add(')');
            add('.');
            add(',');
            add(';');
            add('+');
            add('-');
            add('*');
            add('/');
            add('&');
            add('|');
            add('<');
            add('>');
            add('=');
            add('~');
        }
    };
    private static final HashSet<String> keyword = new HashSet<String>() {
        {
            add("class");
            add("constructor");
            add("function");
            add("method");
            add("field");
            add("static");
            add("var");
            add("int");
            add("char");
            add("boolean");
            add("void");
            add("true");
            add("false");
            add("null");
            add("this");
            add("let");
            add("do");
            add("if");
            add("else");
            add("while");
            add("return");
        }
    };

    JackTokenizer(BufferedReader br) {
        this.br = br;
    }

    public boolean hasMoreTokens() throws IOException {
        br.mark(2);
        nextchar = br.read();
        while (nextchar > 0) {
            if (nextchar == '/') {
                nextchar = br.read();
                if (nextchar != '/' && nextchar != '*' && nextchar != -1) {
                    br.reset();
                    return true;
                } else {
                    if (nextchar == '/') {
                        while (nextchar >= 32) {
                            nextchar = br.read();
                            if (nextchar == -1) {
                                return false;
                            }
                        }
                    } else if (nextchar == '*') {
                        boolean iscommentend = false;
                        while (iscommentend == false) {
                            nextchar = br.read();
                            if (nextchar == -1) {
                                return false;
                            }
                            while (nextchar != '*') {
                                nextchar = br.read();
                                if (nextchar == -1) {
                                    return false;
                                }
                            }
                            br.mark(1);
                            nextchar = br.read();
                            if (nextchar == -1) {
                                return false;
                            }
                            if (nextchar == '/') {
                                iscommentend = true;
                                nextchar = br.read();
                                if (nextchar == -1) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }
            } else if (nextchar <= 32) {
                br.mark(1);
                nextchar = br.read();
                if (nextchar == -1) {
                    return false;
                }
                while (nextchar <= 32) {
                    br.mark(1);
                    nextchar = br.read();
                    if (nextchar == -1) {
                        return false;
                    }
                }
                if (nextchar == -1) {
                    return false;
                } else {
                    br.reset();
                    nextchar = br.read();
                    if (nextchar == -1) {
                        return false;
                    }
                }
            } else {
                br.reset();
                return true;
            }
        }
        return false;
    }

    public void advance() throws IOException {
        ArrayList<Character> currenttoken = new ArrayList<Character>();
        type="";
        if (hasMoreTokens()) {
            // integerConstant
            nextchar = br.read();
            if (nextchar >= '0' && nextchar <= '9') {
                while (nextchar >= '0' && nextchar <= '9') {
                    currenttoken.add((char) nextchar);
                    br.mark(1);
                    nextchar = br.read();
                }
                br.reset();
                currenttokens = new char[currenttoken.size()];
                for (int i = 0; i < currenttoken.size(); i++) {
                    currenttokens[i] = currenttoken.get(i);
                }
            }
            // StringConstant
            else if (nextchar == '"') {
                nextchar = br.read();
                do {
                    currenttoken.add((char) nextchar);
                    nextchar = br.read();
                } while (nextchar != '"');
                currenttokens=new char[currenttoken.size()];
                for (int i = 0; i < currenttoken.size(); i++) {
                    currenttokens[i] = currenttoken.get(i);
                }
                type="StrConst";
            }
            // symbol
            else if (symbol.contains((char) nextchar)) {
                currenttoken.add((char) nextchar);
                currenttokens = new char[currenttoken.size()];
                for (int i = 0; i < currenttoken.size(); i++) {
                    currenttokens[i] = currenttoken.get(i);
                }
            } else {
                currenttoken.add((char) nextchar);
                br.mark(1);
                nextchar = br.read();
                while (Character.isAlphabetic(nextchar) || Character.isDigit(nextchar) || nextchar == '_') {
                    currenttoken.add((char) nextchar);
                    br.mark(1);
                    nextchar = br.read();
                }
                br.reset();
                currenttokens = new char[currenttoken.size()];
                for (int i = 0; i < currenttoken.size(); i++) {
                    currenttokens[i] = currenttoken.get(i);
                }
            }
        }
    }

    public TokenType tokenType() {
        if (type == "StrConst")//所以tokenType这块降低耦合度的措施应该是给内部加个type
            return TokenType.STRING_CONST;
        else if (symbol.contains(currenttokens[0]))
            return TokenType.SYMBOL;
        else if (currenttokens[0] >= '0' && currenttokens[0] <= '9')
            return TokenType.INT_CONST;
        else if (keyword.contains(String.valueOf(currenttokens)))
            return TokenType.KEYWORD;
        else
            return TokenType.IDENTIFIER;
    }

    public KeyWord keyWord() {
        return Keyword.get(String.valueOf(currenttokens));
    }

    public char symbol() {
        if (tokenType().equals(TokenType.SYMBOL))
            return currenttokens[0];
        else
            return 0;
    }

    public String identifier() {
        if (tokenType().equals(TokenType.IDENTIFIER))
            return String.valueOf(currenttokens);
        else
            return "";
    }

    public int intVal() {
        if (tokenType().equals(TokenType.INT_CONST))
            return Integer.parseInt(String.valueOf(currenttokens));
        else
            return -11111111;
    }

    public String stringVal() {
        if (tokenType().equals(TokenType.STRING_CONST))
            return String.valueOf(currenttokens);
        else
            return "";
    }

    public static void main(String[] args) throws Exception {
        File f = new File("F:/fjy/code/coding/java/Jackcompiler/src/jackanalyzer/test.txt");
        Reader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        JackTokenizer jt = new JackTokenizer(br);
        while (jt.hasMoreTokens()) {
            jt.advance();
            System.out.print(String.valueOf(jt.currenttokens) + ":");
            System.out.println(jt.tokenType().toString());
        }
        // JackTokenizer jt=new JackTokenizer(System.in);
        br.close();
        fr.close();

    }
}
