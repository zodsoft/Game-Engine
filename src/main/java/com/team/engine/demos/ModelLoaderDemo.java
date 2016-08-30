package com.team.engine.demos;

import javax.vecmath.Quat4f;

import com.team.engine.AbstractGame;
import com.team.engine.Engine;
import com.team.engine.Mesh;
import com.team.engine.MeshObject;
import com.team.engine.ObjLoader;
import com.team.engine.PointLight;
import com.team.engine.Primitives;
import com.team.engine.Shader;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;
import com.team.engine.Material;

/**
 * A demo showing off 3D model loading and texture mapping
 */
public class ModelLoaderDemo extends AbstractGame {
	private Mesh objMesh1;
	private Mesh objMesh2;
	private Mesh objMesh3;
	
	private Material mat1 = new Material("HallwayFloorAlbedo.png", "HallwayFloorSpecularGloss.png", "HallwayFloorNormals.png", 256.0f);
	private Material mat2 = new Material("HallwayWallsAlbedo.png", "HallwayWallsSpecularGloss.png", "HallwayWallsNormals.png", 256.0f);
	private Material mat3 = new Material("HallwayRoofAlbedo.png", "HallwayRoofSpecularGloss.png", "HallwayRoofNormals.png", 256.0f);
	
	public static void main(String[] args) {
		Engine.start(false, new ModelLoaderDemo());
	}

	@Override
	public void init() {
		Engine.loadTexture("HallwayFloorAlbedo.png");
		Engine.loadTexture("HallwayFloorSpecularGloss.png");
		Engine.loadTexture("HallwayFloorNormals.png");
		
		Engine.loadTexture("HallwayWallsAlbedo.png");
		Engine.loadTexture("HallwayWallsSpecularGloss.png");
		Engine.loadTexture("HallwayWallsNormals.png");
		
		Engine.loadTexture("HallwayRoofAlbedo.png");
		Engine.loadTexture("HallwayRoofSpecularGloss.png");
		Engine.loadTexture("HallwayRoofNormals.png");
		
		Engine.loadShader("standard");
		
		objMesh1 = ObjLoader.loadFile("hallway_floor.obj");
		objMesh2 = ObjLoader.loadFile("hallway_walls.obj");
		objMesh3 = ObjLoader.loadFile("hallway_roof.obj");
		
		Engine.scene.add(new PointLight(new Vec3(-1, 4.7, -3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f));
		Engine.scene.add(new PointLight(new Vec3(1, 4.7, -3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f));
		//Engine.scene.add(new PointLight(new Vec3(-1, 4.7, 3), new Vec3(1f, 0.8f, 0.9f), 0.09f, 0.032f));
		//Engine.scene.add(new PointLight(new Vec3(1, 4.7, 3), new Vec3(0.7f, 0.7f, 0.2f), 0.09f, 0.032f));
		
		Engine.scene.sun.color = new Vec3(0, 0, 0);
		
		for (int i = 0; i < 10; i++) {
			Engine.scene.add(new MeshObject(new Vec3(0, 0, i * 8), new Quat4f(), null, 0, objMesh1, 1, mat1));
			Engine.scene.add(new MeshObject(new Vec3(0, 0, i * 8), new Quat4f(), null, 0, objMesh2, 1, mat2));
			Engine.scene.add(new MeshObject(new Vec3(0, 0, i * 8), new Quat4f(), null, 0, objMesh3, 1, mat3));
		}
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
		// TODO Auto-generated method stub
		
	}
}
