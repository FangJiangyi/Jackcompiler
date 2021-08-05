package jackanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

enum TokenType {
    KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
}

enum KeyWord {
    CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE,
    RETURN, TRUE, FALSE, NULL, THIS
}

public class JackAnalyzer {
    private String filename_receive=null;
    public JackAnalyzer(String filename_receive) {
		this.filename_receive = filename_receive;
	}
	public static void main(String[] args) throws IOException {
        JackAnalyzer ja=new JackAnalyzer(args[0]);
        String filename_afterconverted = null;
        String[] filename = null;
        filename = ja.filename_receive.split("\\.");
        filename_afterconverted = filename[0].concat(".vm");
        File f_unknown = new File(ja.filename_receive); // for reading
        if (f_unknown.isDirectory()) {
            File[] files = f_unknown.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    if (files[i].getName().endsWith(".jack")) {
                        Reader fr = new FileReader(files[i]);
                        BufferedReader br = new BufferedReader(fr);
                        filename = files[i].getAbsolutePath().split("\\.");
                        filename_afterconverted = filename[0].concat(".vm");
                        File f2 = new File(filename_afterconverted); // for writing
                        Writer fo = new FileWriter(f2);
                        BufferedWriter bw = new BufferedWriter(fo);
                        CompilationEngine ce = new CompilationEngine(br, bw,files[i].getName().split("\\.")[0]);
                        ce.CompileClass();
                        br.close();
                        fr.close();
                        bw.close();
                        fo.close();
                    }
                }
            }
            
        } else {
            Reader fr = new FileReader(f_unknown);
            BufferedReader br = new BufferedReader(fr);
            File f2 = new File(filename_afterconverted); // for writing
            Writer fo = new FileWriter(f2);
            BufferedWriter bw = new BufferedWriter(fo);
            CompilationEngine ce = new CompilationEngine(br, bw,f_unknown.getName().split("\\.")[0]);
            ce.CompileClass();
            br.close();
            bw.close();
            fr.close();
            fo.close();
        }
    }
}