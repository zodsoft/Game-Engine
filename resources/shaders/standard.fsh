#version 330 core

#define MAX_POINT_LIGHTS 100

struct Material {
	sampler2D diffuseTex;
	vec3 diffuse;
	bool diffuseTextured;

	sampler2D normalTex;
	bool normalTextured;

	sampler2D roughnessTex;
	float roughness;
	bool roughnessTextured;

	sampler2D metallicTex;
	float metallic;
	bool metallicTextured;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;

	bool hasShadowMap;
	sampler2DShadow shadowMap;
};

struct PointLight {
    vec3 position;
    vec3 color;

    float linear;
    float quadratic;
};


in vec2 texCoord;
in vec3 fragNormal;
in vec3 fragPos;
in vec3 viewPos;
in vec4 fragPosLightSpace;
in vec3 T;
in vec3 B;
in vec3 N;
in vec3 fragTangent;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;
layout (location = 2) out vec4 hdrColor;

uniform Material material;
uniform DirectionalLight dirLight;
uniform int pointLightCount;
uniform PointLight[MAX_POINT_LIGHTS] pointLights;
uniform float ambient;
uniform vec3 cameraPos;
uniform samplerCube skybox;
uniform bool hasSkybox;

uniform samplerCube irradiance;
uniform bool hasIrradiance;

uniform float exposure;
uniform vec3 eyePos;

const float kPi = 3.14159265;

vec2 poissonDisk[16] = vec2[](
   vec2( -0.94201624, -0.39906216 ),
   vec2( 0.94558609, -0.76890725 ),
   vec2( -0.094184101, -0.92938870 ),
   vec2( 0.34495938, 0.29387760 ),
   vec2( -0.91588581, 0.45771432 ),
   vec2( -0.81544232, -0.87912464 ),
   vec2( -0.38277543, 0.27676845 ),
   vec2( 0.97484398, 0.75648379 ),
   vec2( 0.44323325, -0.97511554 ),
   vec2( 0.53742981, -0.47373420 ),
   vec2( -0.26496911, -0.41893023 ),
   vec2( 0.79197514, 0.19090188 ),
   vec2( -0.24188840, 0.99706507 ),
   vec2( -0.81409955, 0.91437590 ),
   vec2( 0.19984126, 0.78641367 ),
   vec2( 0.14383161, -0.14100790 )
);

float ShadowCalculation(vec4 fragPosLightSpace)
{
	if (dirLight.hasShadowMap) {
		float visibility = 1.0;
		float bias = 0.002;

		for (int i=0;i<4;i++){
			int index = i;

			visibility -= 0.2*(1.0-texture( dirLight.shadowMap, vec3(fragPosLightSpace.xy + poissonDisk[index]/3000.0, (fragPosLightSpace.z)/fragPosLightSpace.w-bias) ));
		}

		return visibility;
	}
	else {
		return 1.0;
	}
}

vec3 calcDirectionalLight(DirectionalLight light, vec3 diffuse, float roughness, vec3 normal) {
	float shadow = ShadowCalculation(fragPosLightSpace);

	//diffuse
	vec3 norm = normalize(normal);
	vec3 lightDir = normalize(-light.direction);
	vec3 viewDir = normalize(viewPos - fragPos);
	float diff = max(dot(norm, lightDir), 0.0);

	float shininess = (clamp((1-roughness), 0, 1) * 64) + 1;

	//blinn phong
	vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);

    vec3 result = (light.color * diff * shadow + spec * light.color * diff * shadow);
    return result;
}

vec3 calcPointLight(PointLight light, vec3 diffuse, float roughness, vec3 normal) {
	float distance = length(light.position - fragPos);
	float attenuation = 1.0f / (1.0f + light.linear * distance + light.quadratic * (distance * distance));

	//diffuse
	vec3 norm = normalize(normal);
	vec3 lightDir = normalize(light.position - fragPos);
	float diff = max(dot(norm, lightDir), 0.0f);
	vec3 viewDir = normalize(viewPos - fragPos);
	float shininess = (clamp((1-roughness), 0, 1) * 64) + 1;

	//blinn phong
	vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);

    vec3 result = (light.color * diff * 1.0 + spec * light.color) * attenuation;
    return result;
}

float luma(vec3 color) {
	    return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
}

const float blurSizeH = 1.0 / 300.0;
const float blurSizeV = 1.0 / 200.0;

void main() {
	vec3 diffuse = vec3(0, 0, 0);
	float roughness = 0;
	float metallic = 0;
	vec3 normal = vec3(0, 0, 0);

	if (material.diffuseTextured) {
		diffuse = texture(material.diffuseTex, texCoord).rgb;
	} else {
		diffuse = material.diffuse;
	}

	if (material.roughnessTextured) {
		roughness = length(texture(material.roughnessTex, texCoord).rgb);
	} else {
		roughness = material.roughness;
	}

	if (material.metallicTextured) {
		metallic = length(texture(material.metallicTex, texCoord).rgb);
	} else {
		metallic = material.metallic;
	}

	if (material.normalTextured) {
		//normalTex = normalize(texture(material.normal, texCoord).rbg * 2.0 - 1.0);

		mat3 TBN = mat3(T, B, N);

		normal = texture(material.normalTex, texCoord).rgb;
		normal = normalize(normal * 2.0 - 1.0);
		normal = normalize(TBN * normal);
	} else {
		normal = fragNormal;
	}

	vec3 lightCombined = vec3(0, 0, 0);

	for (int i = 0; i < pointLightCount; i++) {
		if (i < MAX_POINT_LIGHTS) {
			lightCombined += calcPointLight(pointLights[i], diffuse, roughness, normal);
		}
	}

	float shadow = ShadowCalculation(fragPosLightSpace);
	lightCombined += calcDirectionalLight(dirLight, diffuse, roughness, normal);

	lightCombined = clamp(lightCombined, ambient, 100.0f);


	//color = vec4(reflectcolor, 1.0f);

	vec3 reflectColor;
	float fresnel = 0;
	if (hasSkybox) {
	  vec3 I = normalize(fragPos - eyePos);
	  vec3 R = reflect(I, normalize(normal));
	  fresnel = dot(I, R);
	  reflectColor = textureLod(skybox, R, roughness * 6).rgb;

	  diffuse = mix(diffuse, reflectColor * diffuse, clamp(metallic, 0, 1));
	}

	color = vec4(lightCombined * diffuse, 1.0f);
	//color = vec4(vec3(metallic), 1.0f);

  	// Check whether fragment output is higher than threshold, if so output as brightness color
  	float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
  	if(brightness > 1.5 / exposure)
  		brightColor = vec4(color.rgb, 1.0);
  	else
		brightColor = vec4(0, 0, 0, 1.0);

	float lumaThresh = 0.9;
	brightColor = vec4(color.rgb * clamp( luma(color.rgb) - lumaThresh, 0.0, 1.0 ) * (1.0 / (1.0 - lumaThresh)), 1.0);

	hdrColor = vec4(0, 0, 0, 1.0);
}
