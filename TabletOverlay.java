package net.unethicalite.plugins.Tablet;

import com.google.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.unethicalite.api.input.Mouse;

import javax.inject.Inject;
import javax.management.timer.Timer;
import java.awt.*;

@Singleton
class TabletOverlay extends Overlay
{
    Timer timer=new Timer();


    private final Client client;

    private final TabletPlugin plugin;

    @Inject
    private TabletOverlay(Client client, TabletPlugin plugin )
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
            g.drawString("Value: " +plugin.Valueacc/1000+"K", 30,30);
            g.drawString("Status: "+ plugin.status,30,50);
            g.drawString("GETime: " + plugin.GETime/1000+"S",30,70);
            g.drawString("x",Mouse.getPosition().x-5,Mouse.getPosition().y-5);
        }
        return null;
    }
}