package com.team.engine;

public interface GameObject {
	/**
	 * Called when the object is added to the scene.
	 */
	public void init(Scene scene);
	
	public void update();
	
	public void render(Scene scene, Camera cam);
	
	public void renderShadow(Shader s);
}
