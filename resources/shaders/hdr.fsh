#version 330 core
in vec2 TexCoords;
in vec2 screenPos;
out vec4 color;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture;
uniform float exposure;
uniform bool doBloom;

vec3 toUncharted2(vec3 _x)
{
    float a = 0.22;
    float b = 0.30;
    float c = 0.10;
    float d = 0.20;
    float e = 0.01;
    float f = 0.30;
    float w = 11.2;
    return ((_x*(a*_x+c*b)+d*e)/(_x*(a*_x+b)+d*f))-e/f;
}

void main()
{
    vec4 hdrColor = texture(screenTexture, TexCoords);
    vec4 combined;
    vec4 bloomColor = vec4(0);
    if (doBloom) {
        vec4 bloomColor = texture(bloomTexture, TexCoords);

        combined = hdrColor + bloomColor; // additive blending
    }
    else {
        combined = hdrColor;
    }

    //vignette
    float vignette = clamp(1-((length(screenPos) - 1) * 1), 0, 1);

    color = vec4(toUncharted2(combined.rgb * vignette), hdrColor.a);
    //color = vec4(texture(hdrTexture, TexCoords).rgb, 1.0f);
}
