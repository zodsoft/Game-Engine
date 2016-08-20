#version 300 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;

void main()
{
    color = vec4(texture(screenTexture, TexCoords.st));
}  

