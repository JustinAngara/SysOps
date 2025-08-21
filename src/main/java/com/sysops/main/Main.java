package com.sysops.main;

import com.sysops.functions.SystemLib;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static GUI g;
    public static SystemLib lib;
    public static void main(String[] args){
        lib = new SystemLib();
        // starts up com.sysops.main.GUI frame
        SwingUtilities.invokeLater(() -> {
            g = new GUI();
            g.setOutputColor(new Color(255,255,255));
            g.setInputText("Enter something...");
        });

    }
}
