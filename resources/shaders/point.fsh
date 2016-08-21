#version 330 core

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform vec3 lightColor;

in vec3 fragColor;

void main() {
	color = vec4(fragColor * 4, 1.0f);
	brightColor = vec4(fragColor * 2, 1);
}
