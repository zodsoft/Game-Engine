package com.team.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import com.team.engine.vecmath.Vec2;

/**
 * This is the main class for receiving any sort of input.
 * 
 * Needs a few more hooks, but for now all you'll really be
 * using are mousePos and isKeydown.
 * 
 */
public class Input {
	public static boolean[] keys = new boolean[1024];
	public static Vec2 mousePos = new Vec2(0, 0);
	public static boolean firstMouse = true;
	public static boolean mouseGrabbed = false;
	public static double scrollingAmount = 0;
	
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	protected static void keyEvent(long window, int key, int action) {
		if(action == GLFW_PRESS)
			keys[key] = true;
		else if(action == GLFW_RELEASE)
			keys[key] = false;  
		
		if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
		
		if(key == GLFW_KEY_M && action == GLFW_PRESS) {
			if (!Engine.instance.wireframe) {
				Engine.instance.wireframe = true;
			}
			else {
				Engine.instance.wireframe = false;
			}
		}
	}
	
	protected static void cursorEvent(long window, double xpos, double ypos) {
		mousePos = new Vec2((float)xpos, (float)ypos);
	}
	
	protected static void mouseEvent(long window, int button, int action, int mods) {
		if (button == GLFW_MOUSE_BUTTON_1) {
			if (action == GLFW_PRESS) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				firstMouse = true;
				mouseGrabbed = true;
			}
			if (action == GLFW_RELEASE) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				mouseGrabbed = false;
			}
		}
	}
	
	protected static void scrollEvent(long window, double scrollAmount) {
		scrollingAmount += scrollAmount;
	}
}

class KeyCallback extends GLFWKeyCallback {
	@Override
	public void invoke(long window, int key, int scancode, int action, int mode) {
		Input.keyEvent(window, key, action);
	}
}

class CursorCallback extends GLFWCursorPosCallback {
	@Override
	public void invoke(long window, double xpos, double ypos) {
		Input.cursorEvent(window, xpos, ypos);
	}
}

class MouseCallback extends GLFWMouseButtonCallback {
	@Override
	public void invoke(long window, int button, int action, int mods) {
		Input.mouseEvent(window, button, action, mods);
	}
}

class ScrollCallback extends GLFWScrollCallback {

	@Override
	public void invoke(long window, double arg1, double scrollAmount) {
		Input.scrollEvent(window, scrollAmount);
	}
	
}