package jackanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Stack;

enum Kind {
    STATIC, FIELD, ARG, VAR, UNDEFINED
}

public class CompilationEngine {
    private JackTokenizer jt;
    private BufferedWriter bw;
    private SymbolTable st;
    private VMWriter vmw;
    private String fn;
    private Stack<Integer> whileStack;
    private Stack<Integer> ifStack;
    private int whilenums = 0;
    private int ifnums = 0;

    CompilationEngine(BufferedReader br, BufferedWriter bw, String fn) {
        this.vmw = new VMWriter(bw);
        this.jt = new JackTokenizer(br);
        this.bw = bw;
        this.fn = fn;
    }

    public void CompileClass() throws IOException {
        whileStack = new Stack<Integer>();
        ifStack = new Stack<Integer>();
        whileStack.push(0);
        ifStack.push(0);
        int subroutinenums = 0;
        st = new SymbolTable();
        jt.advance();
        jt.advance();
        jt.advance();
        jt.advance();
        if (jt.tokenType().equals(TokenType.SYMBOL)) {
            bw.write(jt.symbol());
        } else {
            while (jt.tokenType().equals(TokenType.KEYWORD)) {
                if (jt.keyWord().equals(KeyWord.FIELD) || jt.keyWord().equals(KeyWord.STATIC))
                    CompileClassVarDec();
                else if (jt.keyWord().equals(KeyWord.CONSTRUCTOR) || jt.keyWord().equals(KeyWord.FUNCTION)
                        || jt.keyWord().equals(KeyWord.METHOD)) {
                    CompileSubroutine(subroutinenums);
                    subroutinenums++;
                }
            }
        }
    }

    public void CompileClassVarDec() throws IOException {
        String name;
        String type;
        Kind kind;
        if (jt.keyWord().equals(KeyWord.FIELD)) {
            kind = Kind.FIELD;
        } else {
            kind = Kind.STATIC;
        }
        jt.advance();
        if (jt.tokenType().equals(TokenType.KEYWORD))
            switch (jt.keyWord()) {
                case INT:
                    type = "int";
                    break;
                case BOOLEAN:
                    type = "boolean";
                    break;
                case CHAR:
                    type = "char";
                    break;
                default:
                    type = "";
                    break;
            }
        else {
            type = jt.identifier();
        }
        jt.advance();
        name = jt.identifier();
        jt.advance();
        st.Define(name, type, kind);
        while (jt.symbol() != ';') {
            jt.advance();
            name = jt.identifier();
            jt.advance();
            st.Define(name, type, kind);
        }
        jt.advance();
    }

    public void CompileSubroutine(int subroutinenums) throws IOException {
        String func = null;
        int nLocals;
        st.startSubroutine();
        switch (jt.keyWord()) {
            case CONSTRUCTOR:
                bw.write("//constructor\n");
                jt.advance();
                jt.advance();
                func = jt.identifier();
                jt.advance();
                jt.advance();
                CompileParameterList();
                jt.advance();
                jt.advance();
                while (jt.keyWord().equals(KeyWord.VAR)) {
                    CompileVarDec();
                }
                vmw.writeFunction(fn + "." + func, st.VarCount(Kind.VAR));
                vmw.writePush(st.VarCount(Kind.FIELD), Segment.CONST);
                vmw.writeCall("Memory.alloc", 1);
                vmw.writePop(0, Segment.POINTER);
                CompileStatements(subroutinenums);
                jt.advance();
                break;
            case FUNCTION:
                jt.advance();
                jt.advance();
                func = jt.identifier();
                jt.advance();
                jt.advance();
                nLocals = CompileParameterList();
                jt.advance();// ')'结束
                jt.advance();
                while (jt.keyWord().equals(KeyWord.VAR)) {
                    CompileVarDec();
                }
                vmw.writeFunction(fn + "." + func, st.VarCount(Kind.VAR));
                CompileStatements(subroutinenums);
                jt.advance();
                break;
            case METHOD:
                jt.advance();
                jt.advance();
                func = jt.identifier();
                jt.advance();
                jt.advance();
                st.Define("this", fn, Kind.ARG);
                nLocals = CompileParameterList();
                jt.advance();
                jt.advance();
                while (jt.keyWord().equals(KeyWord.VAR)) {
                    CompileVarDec();
                }
                vmw.writeFunction(fn + "." + func, st.VarCount(Kind.VAR));
                vmw.writePush(0, Segment.ARG);
                vmw.writePop(0, Segment.POINTER);
                CompileStatements(subroutinenums);
                jt.advance();
                break;
            default:
                System.out.println("error");
                System.exit(-1);
        }
    }

    public int CompileParameterList() throws IOException {
        int nLocals = 0;
        String type;
        String name;
        Kind kind = Kind.ARG;
        if (jt.tokenType().equals(TokenType.SYMBOL)) {

        } else {
            if (jt.tokenType().equals(TokenType.KEYWORD))
                switch (jt.keyWord()) {
                    case INT:
                        type = "int";
                        jt.advance();
                        break;
                    case BOOLEAN:
                        type = "boolean";
                        jt.advance();
                        break;
                    case CHAR:
                        type = "char";
                        jt.advance();
                        break;
                    default:
                        type = "";
                        System.out.println("error!");
                        System.exit(-1);
                        break;
                }
            else {
                type = jt.identifier();
                jt.advance();
            }
            nLocals++;
            name = jt.identifier();
            jt.advance();
            st.Define(name, type, kind);
            while (jt.symbol() != ')') {
                nLocals++;
                jt.advance();
                if (jt.tokenType().equals(TokenType.KEYWORD))
                    switch (jt.keyWord()) {
                        case INT:
                            type = "int";
                            jt.advance();
                            break;
                        case BOOLEAN:
                            type = "boolean";
                            jt.advance();
                            break;
                        case CHAR:
                            type = "char";
                            jt.advance();
                            break;
                        default:
                            type = "";
                            System.out.println("error!");
                            System.exit(-1);
                            break;
                    }
                else {
                    type = jt.identifier();
                    jt.advance();
                }
                name = jt.identifier();
                jt.advance();
                st.Define(name, type, kind);
            }
        }
        return nLocals;
    }

    public void CompileVarDec() throws IOException {
        String type;
        String name;
        Kind kind = Kind.VAR;
        jt.advance();
        if (jt.tokenType().equals(TokenType.KEYWORD)) {
            switch (jt.keyWord()) {
                case INT:
                    type = "int";
                    break;
                case BOOLEAN:
                    type = "boolean";
                    break;
                case CHAR:
                    type = "char";
                    break;
                default:
                    type = "";
                    System.out.println("error!");
                    System.exit(-1);
                    break;
            }
            jt.advance();
        } else {
            type = jt.identifier();
            jt.advance();
        }
        name = jt.identifier();
        jt.advance();
        st.Define(name, type, kind);
        while (jt.symbol() != ';') {
            jt.advance();
            name = jt.identifier();
            jt.advance();
            st.Define(name, type, kind);
        }
        jt.advance();
    }

    public void CompileStatements(int subroutinenums) throws IOException {
        int whileParallel = 0;
        int ifParallel = 0;
        while (jt.tokenType().equals(TokenType.KEYWORD)) {
            switch (jt.keyWord()) {
                case LET:
                    CompileLet();
                    break;
                case IF:
                    ifnums++;
                    CompileIf(ifParallel, subroutinenums);
                    break;
                case WHILE:
                    whilenums++;
                    CompileWhile(whileParallel, subroutinenums);
                    break;
                case DO:
                    CompileDo();
                    break;
                case RETURN:
                    CompileReturn();
                    break;
                default:
            }
        }
    }

    public void CompileDo() throws IOException {
        String prefix;
        String suffix;
        jt.advance();
        prefix = jt.identifier();
        jt.advance();
        if (jt.symbol() == '(') {
            jt.advance();
            vmw.writePush(0, Segment.POINTER);
            vmw.writeCall(fn + "." + prefix, CompileExpressionList() + 1);
            jt.advance();
        } else {
            jt.advance();
            suffix = jt.identifier();
            jt.advance();
            jt.advance();
            if (st.TypeOf(prefix) == "undefined")
                vmw.writeCall(prefix + "." + suffix, CompileExpressionList());
            else {
                if (st.KindOf(prefix) == Kind.STATIC)
                    vmw.writePush(st.IndexOf(prefix), Segment.STATIC);
                else if (st.KindOf(prefix) == Kind.VAR)
                    vmw.writePush(st.IndexOf(prefix), Segment.LOCAL);
                else if (st.KindOf(prefix) == Kind.FIELD)
                    vmw.writePush(st.IndexOf(prefix), Segment.THIS);
                else
                    vmw.writePush(st.IndexOf(prefix), Segment.ARG);
                vmw.writeCall(st.TypeOf(prefix) + "." + suffix, CompileExpressionList() + 1);
            }
            jt.advance();
        }
        vmw.writePop(0, Segment.TEMP);
        jt.advance();
    }

    public void CompileLet() throws IOException {
        Segment segment = Segment.LOCAL;
        int index;
        jt.advance();
        if (st.KindOf(jt.identifier()) == Kind.VAR)
            segment = Segment.LOCAL;
        else if (st.KindOf(jt.identifier()) == Kind.STATIC)
            segment = Segment.STATIC;
        else if (st.KindOf(jt.identifier()) == Kind.ARG)
            segment = Segment.ARG;
        else
            segment = Segment.THIS;// field关于object还并不会怎么处理
        index = st.IndexOf(jt.identifier());
        jt.advance();
        if (jt.symbol() == '[') {// 等式左值为数组
            vmw.writePush(index, segment);
            jt.advance();
            CompileExpression();
            jt.advance();
            vmw.writeArithmetic(Command.ADD);
            jt.advance();
            CompileExpression();
            vmw.writePop(0, Segment.TEMP);
            vmw.writePop(1, Segment.POINTER);
            vmw.writePush(0, Segment.TEMP);
            vmw.writePop(0, Segment.THAT);
            jt.advance();
        } else {// 等式左值非数组
            jt.advance();
            CompileExpression();
            vmw.writePop(index, segment);
            jt.advance();
        }
    }

    public void CompileWhile(int wp, int sn) throws IOException {
        whileStack.push(whilenums);
        vmw.writeLabel("WHILE" + whileStack.peek() + "F");
        jt.advance();
        jt.advance();
        CompileExpression();
        vmw.writeArithmetic(Command.NOT);
        vmw.writeIf("WHILE" + whileStack.peek() + "S");
        jt.advance();
        jt.advance();
        CompileStatements(sn);
        int currentnums = whileStack.pop();
        vmw.writeGoto("WHILE" + currentnums + "F");
        vmw.writeLabel("WHILE" + currentnums + "S");
        jt.advance();
    }

    public void CompileReturn() throws IOException {
        jt.advance();
        if (jt.tokenType().equals(TokenType.SYMBOL)) {
            vmw.writePush(0, Segment.CONST);
            jt.advance();
        } else {
            CompileExpression();
            jt.advance();
        }
        vmw.writeReturn();
    }

    public void CompileIf(int ip, int sn) throws IOException {
        ifStack.push(ifnums);
        jt.advance();
        jt.advance();
        CompileExpression();
        vmw.writeArithmetic(Command.NOT);
        vmw.writeIf("COND" + ifStack.peek() + "F");
        jt.advance();
        jt.advance();
        CompileStatements(sn);
        int currentnums=ifStack.pop();
        vmw.writeGoto("COND" + currentnums + "S");
        vmw.writeLabel("COND" + currentnums + "F");
        jt.advance();
        if (jt.tokenType().equals(TokenType.KEYWORD))
            if (jt.keyWord().equals(KeyWord.ELSE)) {
                jt.advance();
                jt.advance();
                CompileStatements(sn);
                jt.advance();
            }
        vmw.writeLabel("COND" + currentnums + "S");
    }

    public void CompileExpression() throws IOException {
        char symbol;
        Command command;
        CompileTerm();
        while (jt.symbol() == '+' || jt.symbol() == '-' || jt.symbol() == '*' || jt.symbol() == '/'
                || jt.symbol() == '&' || jt.symbol() == '|' || jt.symbol() == '<' || jt.symbol() == '>'
                || jt.symbol() == '=') {
            symbol = jt.symbol();
            jt.advance();
            CompileTerm();
            switch (symbol) {
                case '+':
                    command = Command.ADD;
                    vmw.writeArithmetic(command);
                    break;
                case '-':
                    command = Command.SUB;
                    vmw.writeArithmetic(command);
                    break;
                case '*':
                    vmw.writeCall("Math.multiply", 2);
                    break;
                case '&':
                    command = Command.AND;
                    vmw.writeArithmetic(command);
                    break;
                case '|':
                    command = Command.OR;
                    vmw.writeArithmetic(command);
                    break;
                case '<':
                    command = Command.LT;
                    vmw.writeArithmetic(command);
                    break;
                case '>':
                    command = Command.GT;
                    vmw.writeArithmetic(command);
                    break;
                case '=':
                    command = Command.EQ;
                    vmw.writeArithmetic(command);
                    break;
                case '/':
                    vmw.writeCall("Math.divide", 2);
                default:
                    break;
            }
        }
    }

    public void CompileTerm() throws IOException {
        int index;
        Segment segment;
        char symbol;
        String prefix;
        String suffix;
        switch (jt.tokenType()) {
            case INT_CONST:
                vmw.writePush(jt.intVal(), Segment.CONST);
                jt.advance();
                break;
            case STRING_CONST:
                vmw.writePush(jt.stringVal().length(), Segment.CONST);
                vmw.writeCall("String.new", 1);
                for (int i = 0; i < jt.stringVal().length(); i++) {
                    vmw.writePush(jt.stringVal().toCharArray()[i], Segment.CONST);
                    vmw.writeCall("String.appendChar", 2);
                }
                jt.advance();
                break;
            case KEYWORD:
                switch (jt.keyWord()) {
                    case TRUE:
                        vmw.writePush(1, Segment.CONST);
                        vmw.writeArithmetic(Command.NEG);
                        break;
                    case FALSE:
                        vmw.writePush(0, Segment.CONST);
                        break;
                    case THIS:
                        vmw.writePush(0, Segment.POINTER);
                        break;
                    case NULL:// ?
                        vmw.writePush(0, Segment.CONST);
                        break;
                    default:
                        break;
                }
                jt.advance();
                break;
            case IDENTIFIER:
                prefix = jt.identifier();
                if (!st.TypeOf(jt.identifier()).equals("undefined")) {
                    index = st.IndexOf(jt.identifier());
                    switch (st.KindOf(jt.identifier())) {
                        case VAR:
                            segment = Segment.LOCAL;
                            break;
                        case STATIC:
                            segment = Segment.STATIC;
                            break;
                        case ARG:
                            segment = Segment.ARG;
                            break;
                        case FIELD:
                            segment = Segment.THIS;
                            break;
                        default:
                            segment = Segment.LOCAL;
                            break;
                    }
                    vmw.writePush(index, segment);
                }
                jt.advance();
                switch (jt.symbol()) {
                    // 对于object如何处理？
                    case '.':
                        jt.advance();
                        suffix = jt.identifier();
                        jt.advance();
                        jt.advance();
                        if (st.TypeOf(prefix) == "undefined")
                            vmw.writeCall(prefix + "." + suffix, CompileExpressionList());// compileExpressionList需要用返回参数个数的功能
                        else
                            vmw.writeCall(st.TypeOf(prefix) + "." + suffix, CompileExpressionList() + 1);
                        jt.advance();
                        break;
                    case '(':
                        jt.advance();
                        vmw.writePush(0, Segment.POINTER);
                        vmw.writeCall(fn + "." + prefix, CompileExpressionList() + 1);
                        jt.advance();
                        break;
                    case '[':
                        // 对于数组如何处理？
                        jt.advance();
                        CompileExpression();
                        vmw.writeArithmetic(Command.ADD);
                        jt.advance();
                        vmw.writePop(1, Segment.POINTER);
                        vmw.writePush(0, Segment.THAT);
                        break;
                    default:
                        break;
                }
                break;
            case SYMBOL:
                if (jt.symbol() == '(') {
                    jt.advance();
                    CompileExpression();
                    jt.advance();
                } else {
                    symbol = jt.symbol();
                    jt.advance();
                    CompileTerm();
                    if (symbol == '-') {
                        vmw.writeArithmetic(Command.NEG);
                    } else {
                        vmw.writeArithmetic(Command.NOT);
                    }
                }
                break;
        }
    }

    public int CompileExpressionList() throws IOException {
        int nLocals = 0;
        if (!jt.tokenType().equals(TokenType.SYMBOL)) {
            CompileExpression();
            nLocals++;
        } else {
            if (jt.symbol() == '(' || jt.symbol() == '~' || jt.symbol() == '-') {
                CompileExpression();
                nLocals++;
            }
        }
        while (jt.symbol() != ')') {
            jt.advance();
            CompileExpression();
            nLocals++;
        }
        return nLocals;
    }

    public static void main(String[] args) throws Exception {
        File f = new File("F:/fjy/code/coding/java/Jackcompiler/src/jackanalyzer/Main.jack");
        Reader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        File f1 = new File("F:/fjy/code/coding/java/Jackcompiler/src/jackanalyzer/testout.txt");
        Writer fw = new FileWriter(f1);
        BufferedWriter bw = new BufferedWriter(fw);
        CompilationEngine ce = new CompilationEngine(br, bw, "Main");
        ce.CompileClass();
        br.close();
        fr.close();
        bw.close();
        fw.close();
    }
}