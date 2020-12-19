package main;

import proto.KDialog;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;

public class Preview extends KDialog {


    /**
     * Creates a new preview dialog on the given root.
     * This dialog may be shown while waiting on some tasks to finish.
     * As of this implementation, the preview appears only at start up.
     */
    public Preview(Component root){
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        final KPanel panel = new KPanel(475, 200);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.addAll(new KPanel(new KLabel(new ImageIcon(App.getIconURL("splash.gif")))),
                new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Dashboard is starting... Please wait",
                        KFontFactory.createPlainFont(15))));
        setContentPane(panel);
        pack();
        setLocationRelativeTo(root);
    }

}
