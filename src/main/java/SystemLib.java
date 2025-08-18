import java.io.File;

public class SystemLib {
    static {
        try {
            String projectPath = System.getProperty("user.dir");
            String dllPath = projectPath + "\\src\\main\\c++\\build\\Debug\\systemlib.dll";
            System.load(dllPath);

        } catch (UnsatisfiedLinkError e) {
            throw e;
        }
    }

    // Native method declarations
    public native void applyStealth(String processExe);
    public native void applyStealthByPid(int n);

//    this will test some of the libraries
    @SuppressWarnings("unused")
    public void test(){
        try {
            SystemLib lib = new SystemLib();
            lib.applyStealth("notepad.exe");
            lib.applyStealthByPid(31540);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        SystemLib s = new SystemLib();
        s.test();
    }
}