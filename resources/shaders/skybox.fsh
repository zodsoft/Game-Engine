#version 330 core
in vec3 TexCoords;

uniform samplerCube skybox;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

float luma(vec3 color) {
	return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
}

void main()
{
    color = vec4(textureLod(skybox, TexCoords, 0).rgb, 1);

    float lumaThresh = 0.98;
	brightColor = vec4(color.rgb * clamp( luma(color.rgb) - lumaThresh, 0.0, 1.0 ) * (1.0 / (1.0 - lumaThresh)), 1.0);
}
