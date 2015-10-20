package fromD2l;

public class ClassD
{
    public ClassD() {}
    public ClassD(int i) { val3=i; }

    public String toString() { return "ClassD"; }
    
    public int getVal3() { return val3; }

    private ClassA val = new ClassA(17);
    private static ClassA val2;
    private int val3=34;
    private ClassA[] vallarray = new ClassA[10];
}
