package main;

import proto.KDialog;
import proto.KFontFactory;
import proto.KLabel;
import proto.KPanel;

import javax.swing.*;
import java.awt.*;

public class Preview extends KDialog {


    public Preview(Component root){
        setDefaultCloseOperation(KDialog.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        final KPanel kPanel = new KPanel(475, 200);
        kPanel.setBackground(Color.WHITE);
        kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.Y_AXIS));
        kPanel.addAll(new KPanel(new KLabel(new ImageIcon(App.getIconURL("splash.gif")))),
                new KPanel(new FlowLayout(FlowLayout.LEFT), new KLabel("Dashboard is starting... Please wait",
                        KFontFactory.createPlainFont(15))));
        setContentPane(kPanel);
        pack();
        setLocationRelativeTo(root);
    }

}
