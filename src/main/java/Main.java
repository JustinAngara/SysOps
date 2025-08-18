import javax.swing.*;

public class Main {

    static GUI g;
    public static void main(String[] args){
        // starts up GUI frame
        SwingUtilities.invokeLater(() -> {
            g = new GUI();
            g.setInputText("Enter something...");
        });

    }
}
