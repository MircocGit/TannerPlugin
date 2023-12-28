package net.unethicalite.plugins.Tablet;

import lombok.Getter;

@Getter
public enum Types
{
	VARROCK("AirBattleStaff")
	,HOUSE("MagicLongBow")
	,FALAOR("OnyxBolt");


	public final String[] namesss;

	Types(String... names)
	{
		this.namesss = names;
	}

}
