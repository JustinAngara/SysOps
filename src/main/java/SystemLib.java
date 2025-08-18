import java.io.File;

public class SystemLib {
    static {
        try {
            String projectPath = System.getProperty("user.dir");
            String dllPath = projectPath + "\\src\\main\\c++\\build\\Debug\\systemlib.dll";
        } catch (UnsatisfiedLinkError e) {
            throw e;
        }
    }

    // Native method declarations
    public native void sayHello();
    public native int add(int a, int b);
    public native String reverse(String s);


    @SuppressWarnings("unused")
    public void test(){
        try {
            SystemLib lib = new SystemLib();
            lib.sayHello();
            System.out.println("2 + 40 = " + lib.add(2, 40));
            System.out.println("'JNI' reversed = '" + lib.reverse("JNI") + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}