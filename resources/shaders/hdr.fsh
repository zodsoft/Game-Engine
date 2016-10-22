#version 330 core
in vec2 TexCoords;
in vec2 screenPos;
out vec4 color;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture1;
uniform sampler2D bloomTexture2;
uniform sampler2D bloomTexture3;
uniform float exposure;
uniform bool doBloom;

float A = 0.2;
float B = 0.3;
float C = 0.09;
float D = 0.3;
float E = 0.024;
float F = 0.4;

vec3 Uncharted2Tonemap(vec3 x) {

	/*--------------------------------*/
	return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}

float vignette() {
    float dist = distance(TexCoords, vec2(0.5)) * 2.0;
    dist /= 1.5142;

    dist = pow(dist, 1.1);

    return 1.0 - dist;
}

void main()
{
    vec4 hdrColor = texture(screenTexture, TexCoords);
    vec4 combined;
    vec4 bloomColor = vec4(0);
    if (doBloom) {
        vec4 bloomColor = texture(bloomTexture1, TexCoords) + texture(bloomTexture2, TexCoords)+ texture(bloomTexture3, TexCoords);

        combined = hdrColor + bloomColor; // additive blending
    }
    else {
        combined = hdrColor;
    }

    color = vec4(Uncharted2Tonemap(combined.rgb) * vignette(), 1.0);
}
