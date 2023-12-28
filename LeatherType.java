package net.unethicalite.plugins.Tanner;

import lombok.Getter;

@Getter
public enum LeatherType
{
	GREEN("Green")
	,BLUE("Blue")
	,RED("Red")
	,BLACK("Black")
	,COW("COWHIDE");


	public final String[] namesss;

	LeatherType(String... names)
	{
		this.namesss = names;
	}

}
