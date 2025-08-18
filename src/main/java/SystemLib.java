import java.io.File;

/*
* This file will bridge C++ and Java via JNI
* Implement required method declarations
* */
public class SystemLib {
    static {
        try {
            String projectPath = System.getProperty("user.dir");
            String dllPath = projectPath + "\\src\\main\\c++\\target\\native\\systemlib.dll";
            System.load(dllPath);

        } catch (UnsatisfiedLinkError e) {
            throw e;
        }
    }

    // Native method declarations
    public native void applyStealth(String processExe);
    public native void applyStealthByPid(int n);
    public native void memoryDumpByProcessName(String processExe);
//    this will test some of the libraries
    @SuppressWarnings("unused")
    public void test(){
        try {
            SystemLib lib = new SystemLib();
//            lib.applyStealth("notepad.exe");
//            lib.applyStealthByPid(31540);
            lib.memoryDumpByProcessName("notepad.exe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        SystemLib s = new SystemLib();
        s.test();
    }
}