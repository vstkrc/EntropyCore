package us.rockhopper.entropy.entities;

import us.rockhopper.entropy.utility.Part;

public class Shield extends Part {

	public Shield(int gridX, int gridY, int height, int width, float density,
			String sprite) {
		super(gridX, gridY, height, width, density, sprite);
	}

	@Override
	public int[] getKeys() {
		return null;
	}

	@Override
	public void trigger(int key) {
	}

	@Override
	public void unTrigger(int key) {
	}
}
