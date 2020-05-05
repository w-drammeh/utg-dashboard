package main;

import customs.KDialog;
import customs.KFontFactory;
import customs.KLabel;
import customs.KPanel;

import javax.swing.*;
import java.awt.*;

public class Preview extends KDialog {

    public Preview(Component root){
        this.setDefaultCloseOperation(KDialog.DO_NOTHING_ON_CLOSE);
        this.setUndecorated(true);

        final KPanel kPanel = new KPanel(new Dimension(475, 200));
        kPanel.setBackground(Color.BLACK);
        kPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        kPanel.setLayout(new BoxLayout(kPanel, BoxLayout.Y_AXIS));
        kPanel.addAll(KPanel.wantDirectAddition(new KLabel(new ImageIcon(App.getIconURL("splash.gif")))),
                KPanel.wantDirectAddition(new FlowLayout(FlowLayout.LEFT), null, new KLabel("Dashboard is starting... Please wait",
                        KFontFactory.createPlainFont(15), Color.WHITE)));

        this.setContentPane(kPanel);
        this.pack();
        this.setLocationRelativeTo(root);
    }

}
