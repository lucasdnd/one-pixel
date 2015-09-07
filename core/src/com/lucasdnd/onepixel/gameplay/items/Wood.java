package com.lucasdnd.onepixel.gameplay.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.lucasdnd.onepixel.ui.SideBar;

public class Wood extends Item {
	
	final float offsetY = -32f;
	
	public Wood() {
		super();
		this.setName("Wood");
		this.setColor(Color.BROWN);
	}
	
	@Override
	public void render(ShapeRenderer sr, float x, float y) {
		sr.begin(ShapeType.Filled);
		sr.setColor(this.getColor());
		sr.rect(x + SideBar.lineWeight,
				y - SideBar.lineWeight + offsetY,
				SideBar.INVENTORY_BOX_SIZE - SideBar.lineWeight,
				SideBar.INVENTORY_BOX_SIZE - SideBar.lineWeight);
		sr.end();
	}

}
