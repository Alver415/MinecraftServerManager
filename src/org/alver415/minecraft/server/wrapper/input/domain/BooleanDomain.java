package org.alver415.minecraft.server.wrapper.input.domain;

public class BooleanDomain extends AbstractDomain<Boolean> {

	{
		type = Boolean.class;
		options.add(true);
		options.add(false);
		defaultValue = false;
	}

}