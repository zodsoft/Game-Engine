package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.PointLight;
import com.team.engine.rendering.Primitives;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Vec3;
import com.team.engine.AbstractGame;

/**
 * A demo utilizing sprite rendering, Grid2D's and dyn4j physics.
 */
public class Template extends AbstractGame {
	private Material planeMaterial = new Material(new Vec3(0.5f, 0.5f, 0.5f), 0.1f, 16f);
	private Mesh planeMesh;
	
	public static void main(String[] args) {
		Engine.start(false, false, new Template());
	}

	@Override
	public void init() {
		
	}
	
	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		
	}

	@Override
	public void kill() {

	}

	@Override
	public void renderShadow(Shader s) {
		
	}
}
