package net.unethicalite.plugins.Tanner;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Tanner")
public interface TannerConfig extends Config
{
	@ConfigItem(
			keyName = "usePotion",
			name = "Use Stamina Potion",
			description = "",
			position = 1
	)
	default boolean usePortion()
	{
		return false;
	}
	@ConfigItem(
			keyName = "antiban",
			name = "Anti Ban",
			description = "",
			position = 1
	)
	default boolean antiban()
	{
		return false;
	}
	@ConfigItem(
			keyName = "leathertype",
			name = "leather type",
			description = "Choose your Type of leather",
			position = 2
	)
	default LeatherType leathertype()
	{
		return LeatherType.RED;
	}
	@ConfigItem(keyName = "Start", name = "Start/Stop", description = "Start/Stop button", position = 3)
	default Button startStopButton()
	{
		return new Button();
	}
}


