package jackanalyzer;

import java.io.BufferedWriter;
import java.io.IOException;

enum Segment {
    CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP,
}

enum Command {
    ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
}

public class VMWriter {
    private BufferedWriter bw;

    public VMWriter(BufferedWriter bw) {
        this.bw = bw;
    }

    public void writePush(int index, Segment segment) throws IOException {
        switch (segment) {
            case CONST:
                bw.write("push constant " + index + "\n");
                break;
            case ARG:
                bw.write("push argument " + index + "\n");
                break;
            case LOCAL:
                bw.write("push local " + index + "\n");
                break;
            case STATIC:
                bw.write("push static " + index + "\n");
                break;
            case THIS:
                bw.write("push this " + index + "\n");
                break;
            case THAT:
                bw.write("push that " + index + "\n");
                break;
            case POINTER:
                bw.write("push pointer " + index + "\n");
                break;
            case TEMP:
                bw.write("push temp " + index + "\n");
                break;
            default:
                break;
        }
    }

    public void writePop(int index, Segment segment) throws IOException {
        switch (segment) {
            case CONST:
                bw.write("pop constant " + index + "\n");
                break;
            case ARG:
                bw.write("pop argument " + index + "\n");
                break;
            case LOCAL:
                bw.write("pop local " + index + "\n");
                break;
            case STATIC:
                bw.write("pop static " + index + "\n");
                break;
            case THIS:
                bw.write("pop this " + index + "\n");
                break;
            case THAT:
                bw.write("pop that " + index + "\n");
                break;
            case POINTER:
                bw.write("pop pointer " + index + "\n");
                break;
            case TEMP:
                bw.write("pop temp " + index + "\n");
                break;
            default:
                break;
        }
    }

    public void writeArithmetic(Command command) throws IOException {
        switch (command) {
            case ADD:
                bw.write("add\n");
                break;
            case SUB:
                bw.write("sub\n");
                break;
            case NEG:
                bw.write("neg\n");
                break;
            case EQ:
                bw.write("eq\n");
                break;
            case GT:
                bw.write("gt\n");
                break;
            case LT:
                bw.write("lt\n");
                break;
            case AND:
                bw.write("and\n");
                break;
            case OR:
                bw.write("or\n");
                break;
            case NOT:
                bw.write("not\n");
                break;
            default:
                break;
        }
    }

    public void writeLabel(String label) throws IOException {
        bw.write("label "+label+"\n");
    }

    public void writeGoto(String label) throws IOException {
        bw.write("goto "+label+"\n");
    }

    public void writeIf(String label) throws IOException {
        bw.write("if-goto "+label+"\n");
    }

    public void writeCall(String name, int nArgs) throws IOException {
        bw.write("call "+name+" "+nArgs+"\n");
    }

    public void writeFunction(String name, int nLocals) throws IOException {
        bw.write("function "+name+" "+nLocals+"\n");
    }

    public void writeReturn() throws IOException {
        bw.write("return\n");
    }

    public void close() throws IOException {
        bw.close();
    }
}
