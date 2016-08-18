package com.team.engine.demos;

import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

/**
 * A demo showing off 3D rendering with openGL and lighting shaders.
 */
public class ModelLoaderDemo extends Engine {
	private static PointLight lights[] = {
		new PointLight(new Vec3(-1, 4.7, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(-1, 4.7, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f),
		new PointLight(new Vec3(1, 4.7, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f)
	};
	
	private Mesh cubeMesh;
	private Mesh objMesh;
	private Mesh objMesh2;
	private Mesh objMesh3;
	
	public static void main(String[] args) {
		new ModelLoaderDemo().initialize(false);
	}

	@Override
	public void setupGame() {
		loadTexture("HallwayFloorAlbedo.png");
		loadTexture("HallwayFloorSpecularGloss.png");
		
		loadTexture("HallwayWallsAlbedo.png");
		loadTexture("HallwayWallsSpecularGloss.png");
		
		loadTexture("HallwayRoofAlbedo.png");
		loadTexture("HallwayRoofSpecularGloss.png");
		
		loadShader("standard");
		loadShader("light");
		
		this.setFramebuffer(new Shader("hdr"));
		
		//Create the cube mesh object from the primitive.
		cubeMesh = new Mesh(Primitives.cube(1.0f));
		
		objMesh = ObjLoader.loadFile("resources/hallway_floor.obj");
		objMesh2 = ObjLoader.loadFile("resources/hallway_walls.obj");
		objMesh3 = ObjLoader.loadFile("resources/hallway_roof.obj");
		
		this.background = new Vec3(0, 0, 0);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		//Bind our shader.
		Shader s = getShader("standard");
		s.bind();
		
		//Send material parameters and the global ambient as well.
		s.uniformFloat("ambient", 0.2f);
		s.uniformInt("material.diffuse", 0);
		s.uniformBool("material.diffuseTextured", true);
		
		s.uniformInt("material.specular", 1);
		s.uniformBool("material.specularTextured", true);
		s.uniformVec3("material.specularColor", new Vec3(0.5,0.5,0.5));
		s.uniformFloat("material.shininess", 64.0f);
		s.uniformInt("skybox", 2);
		s.uniformFloat("exposure", 1.0f);
		
		s.uniformInt("pointLightCount", lights.length);
		for (int i = 0; i < lights.length; i++) {
			s.uniformPointLight("pointLights[" + i + "]", lights[i]);
		}
		
		//Draw the falling one.
		for (int i = 0; i < 10; i++) {
			s.uniformMat4("model", new Mat4().translate(new Vec3(0, 0, i * 8)));
			
			getTexture("HallwayFloorAlbedo.png").bind(0);
			getTexture("HallwayFloorSpecularGloss.png").bind(1);
			objMesh.draw();
			
			getTexture("HallwayWallsAlbedo.png").bind(0);
			getTexture("HallwayWallsSpecularGloss.png").bind(1);
			objMesh2.draw();
			
			getTexture("HallwayRoofAlbedo.png").bind(0);
			getTexture("HallwayRoofSpecularGloss.png").bind(1);
			objMesh3.draw();
		}
		
		//Now we switch over to our light shader so we can draw each light. Notice we still don't need to unbind the cubemesh.
		Shader s2 = getShader("light");
		s2.bind();
		
		for (PointLight light : lights) {
			s2.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s2.uniformVec3("lightColor", light.color);
			cubeMesh.draw();
		}
	}

	@Override
	public void postRenderUniforms(Shader shader) {
		//Send our exposure uniform to the post processing shader.
		shader.uniformFloat("exposure", 1.0f);
	}

	@Override
	public void kill() {
		
	}
}
