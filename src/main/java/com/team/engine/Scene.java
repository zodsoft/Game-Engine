package com.team.engine;

import java.util.ArrayList;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.team.engine.vecmath.Mat4;
import com.team.engine.vecmath.Vec3;

import static org.lwjgl.opengl.GL11.*;

import javax.vecmath.Vector3f;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vector2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Scene is meant to make life easier by taking care of all the object handling, lights, and rendering on it's own.
 * Currently this is only designed for 3D. You MUST call setupPhysics if you want there to be physics. If you have a game object
 * that uses bullet physics and setupPhysics was not called you will likely crash.
 */
public class Scene implements ContactListener {
	public ArrayList<PointLight> lights = new ArrayList<>();
	public ArrayList<GameObject> objects = new ArrayList<>();
	public ArrayList<GameObject2D> objects2D = new ArrayList<>();
	
	public DirectionalLight sun;
	public DiscreteDynamicsWorld world;
	
	public World world2D;
	
	public Vec3 ambient = new Vec3(0.3f, 0.3f, 0.3f);
	/** The skybox that will be automatically rendered in the background. 
	 * Can be null to use background color instead */
	public Cubemap skybox = null;
	public Cubemap irradiance = null;
	/** Background color if skybox is null */
	public Vec3 skyColor = new Vec3(0.0f, 0.0f, 0.0f);
	
	private Mesh squareOutline;
	
	public Scene() {
		sun = new DirectionalLight(new Vec3(-1.0f, -1.0f, 0.2f), new Vec3(2.0f, 2.0f, 2.0f), Graphics.ENABLE_SHADOWS, 30, Graphics.SHADOW_RESOLUTION);
		
		Engine.loadShader("color");
		
		ModelBuilder b = new ModelBuilder();
		
		b.vertex(-0.5f, -0.5f, 0.0f);
		b.vertex(-0.5f, 0.5f, 0.0f);
		
		b.vertex(-0.5f, 0.5f, 0.0f);
		b.vertex(0.5f, 0.5f, 0.0f);
		
		b.vertex(0.5f, 0.5f, 0.0f);
		b.vertex(0.5f, -0.5f, 0.0f);
		
		b.vertex(0.5f, -0.5f, 0.0f);
		b.vertex(-0.5f, -0.5f, 0.0f);
		
		squareOutline = b.toMesh();
	}

	/**
	 * Initializes the dynamicsWorld for this scene
	 */
	public void setupPhysics() {
		//setup bullet
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		world = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		// set the gravity of our world
		world.setGravity(new Vector3f(0, -10, 0));
		
		//setup jbox2D
		world2D = new World(new Vector2(0, -9.8f));	
		world2D.setContactListener(this);
	}
	
	public void render(Camera cam) {
		for (GameObject obj: objects) {
			obj.render(this, cam);
		}
		for (GameObject2D obj: objects2D) {
			obj.render(this, cam);
		}
		
		Shader s = Engine.getShader("light");
		s.bind();
		
		for (PointLight light : lights) {
			s.uniformMat4("model", new Mat4().translate(light.position).scale(0.2f));
			s.uniformVec3("lightColor", light.color);
			Engine.cubeMesh.draw();
		}
		//debugRender();
	}
	
	private void debugRender() {
		Shader s = Engine.getShader("color");
		s.bind();
		
		for (GameObject2D obj : objects2D) {
			glLineWidth(2);
			s.uniformVec3("color", new Vec3(0, 1, 0));
			s.uniformMat4("model", new Mat4().translate(new Vec3(obj.body.getPosition(), 10)).scale(0.1f));
			
			Fixture f = obj.body.getFixtureList();
			
			while (f != null) {
				if (f.getShape().getType() == ShapeType.POLYGON) {
					PolygonShape pshape = (PolygonShape)f.getShape();
					
					
					Vector2[] vertices = pshape.getVertices();
					ModelBuilder mb = new ModelBuilder();
					for (Vector2 i : vertices) {
						mb.vertex(i.x, i.y, 0);
					}
					mb.toMesh().draw(GL_LINES);
				}
				f.getShape();
				
				f = f.getNext();
			}
		}
		
		squareOutline.draw(GL_LINES);
	}
	
	public void update() {
		for (GameObject obj: objects) {
			obj.update();
		}
		
		for (GameObject2D obj: objects2D) {
			obj.update();
		}
		
		world2D.step(Engine.deltaTime, 4, 4);
		world.stepSimulation(Engine.deltaTime, 10);
	}
	
	public void renderShadow(Shader s) {
		for (GameObject obj: objects) {
			obj.renderShadow(s);
		}
	}

	public void add(GameObject o) {
		o.init(this);
		objects.add(o);
	}
	
	public void add(GameObject2D o) {
		o.init(this);
		objects2D.add(o);
	}

	public void add(PointLight light) {
		lights.add(light);
	}

	@Override
	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		if (bodyA.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyA.getUserData()).onContact(contact.getFixtureA(), (GameObject2D)contact.getFixtureB().getBody().getUserData());
		}
		
		Body bodyB = contact.getFixtureB().getBody();
		if (bodyB.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyB.getUserData()).onContact(contact.getFixtureB(), (GameObject2D)contact.getFixtureA().getBody().getUserData());
		}
	}

	@Override
	public void endContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		if (bodyA.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyA.getUserData()).endContact(contact.getFixtureA(), (GameObject2D)contact.getFixtureB().getBody().getUserData());
		}
		
		Body bodyB = contact.getFixtureB().getBody();
		if (bodyB.getUserData() instanceof GameObject2D) {
			((GameObject2D)bodyB.getUserData()).endContact(contact.getFixtureB(), (GameObject2D)contact.getFixtureA().getBody().getUserData());
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}
