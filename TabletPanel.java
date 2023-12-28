
package net.unethicalite.plugins.Tablet;

import java.awt.event.*;
import javax.inject.Singleton;
import javax.swing.*;

import com.google.inject.Inject;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.PluginPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.io.IOException;


@Singleton
public
class TabletPanel extends PluginPanel
{
    @Inject
    private EventBus eventBus;
    private JLabel lable;
    private JButton btn;
    private JPanel pnl;
    private JComboBox cmbox;
    private TabletPlugin TabletPlugin;


    @Inject
    private TabletPanel(TabletPlugin plugin, TabletConfig config) throws IOException {
        this.TabletPlugin = plugin;
    }
    public void init() {
        JLabel lbl_crator = new JLabel();
        JLabel lbl_version = new JLabel();
        JLabel lbl_value = new JLabel();
        JButton myButton = new JButton();
        lbl_value.setText(String.valueOf(TabletPlugin.Valueacc));
        lbl_crator.setText("Mircoc");
        lbl_version.setText("0 Tablet v 1.7");
        myButton.setText("Running...");
        myButton.setBackground(Color.green);
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TabletPlugin.Run)
                {
                    myButton.setBackground(Color.darkGray);
                    myButton.setText("Start");
                    TabletPlugin.Run=false;
                }
                else {
                    myButton.setBackground(Color.green);
                    myButton.setText("Running...");
                    TabletPlugin.Run=true;
                }
            }
        });
        //setSize(100, 500);
        add(myButton);
        add(lbl_value);
        add(lbl_crator);
        add(lbl_version);
        setVisible(true);
    }

    void deinit()
    {

    }

}
