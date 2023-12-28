/*
 * Copyright (c) 2018 Charlie Waters
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.unethicalite.plugins.Tanner;

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
class TannerPanel extends PluginPanel
{
    @Inject
    private EventBus eventBus;
    private JLabel lable;
    private JButton btn;
    private JPanel pnl;
    private JComboBox cmbox;
    private  TannerPlugin tannerPlugin;


    	@Inject
	private TannerPanel(TannerPlugin plugin, TannerConfig config) throws IOException {
		this.tannerPlugin = plugin;
	}
    public void init() {
    	JLabel label = new JLabel();
    	label.setText("Mircoc");
        JLabel label2 = new JLabel();
        label2.setText("Tanner v13.5");
        JButton myButton = new JButton();
        myButton.setText("Running...");
        myButton.setBackground(Color.green);
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tannerPlugin.Run)
                {
                    myButton.setBackground(Color.darkGray);
                    myButton.setText("Start");
                    tannerPlugin.Run=false;
                }
                else {
                    myButton.setBackground(Color.green);
                    myButton.setText("Running...");
                    tannerPlugin.Run=true;
                }
            }
        });
        setSize(100, 500);
        add(label);
        add(myButton);
        add(label2);
        setVisible(true);
    }

    void deinit()
    {

    }

}
