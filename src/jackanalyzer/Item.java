package jackanalyzer;

public class Item {
    private String name;
    private String type;
    private Kind kind;
    private int index;
    public Item(String name, String type, Kind kind, int index) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.index = index;
    }
    public Kind getKind(){
        switch (kind) {
            case STATIC:
                return Kind.STATIC;
            case FIELD:
                return Kind.FIELD;
            case ARG:
                return Kind.ARG;
            case VAR:
                return Kind.VAR;
            default:
                return Kind.STATIC;
        }
    }
    public String getType()
    {
        return type;
    }
    public int getIndex()
    {
        return index;
    }
}
