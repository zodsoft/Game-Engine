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
    vec3 combined;
    if (doBloom) {
        vec3 bloomColor = texture(bloomTexture, TexCoords).rgb;
        combined = hdrColor.rgb + bloomColor; // additive blending
    }
    else {
        combined = hdrColor.rgb;
    }

    //vignette
    float vignette = clamp(1-((length(screenPos) - 1) * 1), 0, 1);

    // reinhard
    // vec3 result = hdrColor / (hdrColor + vec3(1.0));
    // exposure
    vec3 result = vec3(1.0) - exp(-combined * exposure);

    color = vec4(combined * vignette, hdrColor.a);
    //color = vec4(texture(hdrTexture, TexCoords).rgb, 1.0f);
}
