package com.team.engine;

public abstract class GameObject {
	public Mesh mesh = null;
	public Shader shader;
	
	public abstract void update();
	
	public abstract void render(Scene scene, Camera cam);
}
