package jackanalyzer;

import java.util.ArrayList;
import java.util.Hashtable;


public class SymbolTable {
    /** private variable需要哪些呢？ 选取什么样的数据结构存储数据呢，怎样做到数据，表现，控制的低耦合？ */
    private ArrayList<Hashtable<String, Item>> al;

    public SymbolTable() {
        al = new ArrayList<Hashtable<String, Item>>();
        al.add(new Hashtable<String, Item>());
    }

    public void startSubroutine() {
        if (al.size() == 2) {
            al.remove(1);
            al.add(new Hashtable<String, Item>());
        } else {
            al.add(new Hashtable<String, Item>());
        }
    }

    public void Define(String name, String type, Kind kind) {
        Item item = new Item(name, type, kind, VarCount(kind));
        al.get(al.size() - 1).put(name, item);
    }

    public int VarCount(Kind kind) {
        int varcount = 0;
        if (al.size() == 2) {
            for (String s : al.get(1).keySet()) {
                if (al.get(1).get(s).getKind().equals(kind))
                    varcount++;
            }
            for (String s : al.get(0).keySet()) {
                if (al.get(0).get(s).getKind().equals(kind))
                    varcount++;
            }
        } else
            for (String s : al.get(0).keySet()) {
                if (al.get(0).get(s).getKind().equals(kind))
                    varcount++;
            }
        return varcount;
    }

    public Kind KindOf(String name) {
        if (al.get(1).get(name) != null)
            return al.get(1).get(name).getKind();
        else if(al.get(0).get(name) != null)
            return al.get(0).get(name).getKind();
        else
            return Kind.UNDEFINED;
    }

    public String TypeOf(String name) {
        if (al.get(1).get(name) != null)
            return al.get(1).get(name).getType();
        else if(al.get(0).get(name)!=null)
            return al.get(0).get(name).getType();
        else
            return "undefined";
    }

    public int IndexOf(String name) {
        if (al.get(1).get(name) != null)
            return al.get(1).get(name).getIndex();
        else if(al.get(0).get(name) != null)
            return al.get(0).get(name).getIndex();
        else
            return -1;
    }

    public static void main(String[] args) {
        SymbolTable st = new SymbolTable();
        // System.out.println(st.al.size());
        st.Define("x", "int", Kind.STATIC);
        st.Define("y", "char", Kind.STATIC);
        st.Define("z", "int", Kind.STATIC);
        st.Define("g", "int", Kind.FIELD);
        st.Define("h", "int", Kind.FIELD);
        st.startSubroutine();
        st.Define("x", "int", Kind.VAR);
        st.Define("u", "int", Kind.VAR);
        st.Define("i", "int", Kind.VAR);
        st.Define("j", "int", Kind.VAR);
        st.Define("sum", "int", Kind.ARG);
        st.Define("num", "int", Kind.ARG);
        st.Define("fuck", "int", Kind.ARG);
        // System.out.println(st.KindOf("k"));
        // System.out.println(st.TypeOf("x"));
        // System.out.println(st.KindOf("x"));
        // System.out.println(st.IndexOf("y"));
        // System.out.println(st.KindOf("z"));
        System.out.println(st.VarCount(Kind.FIELD));
    }

}
