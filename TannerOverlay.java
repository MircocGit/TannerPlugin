package net.unethicalite.plugins.Tanner;

import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.unethicalite.api.input.Mouse;
import net.unethicalite.plugins.Tanner.TannerPlugin;

import javax.inject.Inject;
import javax.management.timer.Timer;
import java.awt.*;

@Singleton
class TannerOverlay extends Overlay
{
    Timer timer=new Timer();


    private final Client client;

    private final TannerPlugin plugin;
    @Inject
    private TannerConfig config;
    @Inject
    private TannerOverlay(Client client, TannerPlugin plugin )
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }
    @Override
    public Dimension render(Graphics2D g)
    {
        if (plugin.Run) {
            g.setColor(Color.orange);
            g.drawString("Tanner v13.5", 30,25);
            g.drawString("Value: " +plugin.Valueacc/1000+"K", 30,45);
            g.drawString("Status: "+ plugin.status ,30,65);
            g.drawString("AntibanStatus: " + plugin.antistatus,30,85);
            if (config.antiban()) {
                g.drawString("To Anti ban: " + (plugin.rndtime - (System.currentTimeMillis() - plugin.timecurrent)), 30, 90);
            }
            else {
                g.setColor(Color.RED);
                g.drawString("Antiban is off",30,90);
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("X",Mouse.getPosition().x-5,Mouse.getPosition().y-5);
        }
        return null;
    }
}