import javax.swing.*;

public class Main {

    static GUI g;
    static SystemLib lib;
    public static void main(String[] args){
        lib = new SystemLib();
        // starts up GUI frame
        SwingUtilities.invokeLater(() -> {
            g = new GUI();
            g.setInputText("Enter something...");
        });

    }
}
