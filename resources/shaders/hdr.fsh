#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;
uniform float exposure;

void main()
{
    vec3 hdrColor = texture(screenTexture, TexCoords).rgb;

    // reinhard
    // vec3 result = hdrColor / (hdrColor + vec3(1.0));
    // exposure
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    color = vec4(result, 1.0f);
}  

