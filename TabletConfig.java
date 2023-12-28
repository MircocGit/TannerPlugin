package net.unethicalite.plugins.Tablet;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Tabletgroup")
public interface TabletConfig extends Config
{

	@ConfigItem(
			keyName = "type",
			name = "type",
			description = "Choose",
			position = 0
	)
	default Types TabletTypes()
	{
		return Types.VARROCK;
	}
	@ConfigItem(keyName = "Start", name = "Start/Stop", description = "Start/Stop button", position = 1)
	default Button startStopButton()
	{
		return new Button();
	}
}

