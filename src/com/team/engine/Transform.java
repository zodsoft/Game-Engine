package com.team.engine;

import com.team.engine.vecmath.Vec3;

public class Transform {
	public Vec3 position;
	public Vec3 scale;
	public Vec3 rotation;
	
	public Transform() {
		this.position = new Vec3();
		this.scale = new Vec3(1, 1, 1);
		this.rotation = new Vec3(0, 0, 0);
	}
}
