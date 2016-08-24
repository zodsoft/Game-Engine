package com.team.engine.demos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.team.engine.Engine;
import com.team.engine.Shader;

public class InputDemo extends Engine {
	
	static Socket socket;
	static ObjectOutputStream out;
	static BufferedReader in;
	static int port;
	
	public static void main(String[] args) {
		System.out.println();
		
		if (args.length != 1) {
			port = 5660;
		} else if (args[0].toString() == "help") {
			System.out.println("Usage: java RoboKalServer <port number>");
			System.exit(1);
		} else port = Integer.parseInt(args[0]);
		
		new InputDemo().initialize(false);
	}
	
	private static Controller controller;

	@Override
	public void setupGame() {
		if (Controllers.getControllerCount() > 0) {
			controller = Controllers.getController(0);
		}
		
		System.out.println("Initializing socket.");
		try (ServerSocket serverSocket = new ServerSocket(port)) { 
			socket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port + ". Is it open?");
			System.exit(-1);
		}
		System.out.println("Finished initializing socket.");
		
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
	        out.flush();
			in = new BufferedReader(
					new InputStreamReader(
							socket.getInputStream()));
		} catch (Exception e) {}
	}
	
	@Override
	public void tick() {
		
			float lookX = controller.getAxisValue(4);
			float lookY = controller.getAxisValue(5);
			
			if (lookX > 0.3f || lookX < -0.3f || lookY > 0.3f || lookY < -0.3f) {
				System.out.println("sending packet");
				
				try {
					out.writeFloat(lookX);
					out.writeFloat(lookY);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("done");
			}
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
