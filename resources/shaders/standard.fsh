#version 330 core

#define MAX_POINT_LIGHTS 100

struct Material {
	sampler2D diffuse;
	vec3 diffuseColor;
	bool diffuseTextured;

	sampler2D specular;
	vec3 specularColor;
	bool specularTextured;

	float shininess;
};

struct DirectionalLight {
    vec3 direction;
    vec3 color;
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

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 brightColor;

uniform Material material;
uniform DirectionalLight dirLight;
uniform int pointLightCount;
uniform PointLight[MAX_POINT_LIGHTS] pointLights;
uniform vec3 ambient;
uniform vec3 cameraPos;
uniform samplerCube skybox;

vec3 calcDirectionalLight(DirectionalLight light, vec3 diffuseTex, vec3 specularTex) {
	//diffuse
	vec3 norm = normalize(fragNormal);
	vec3 lightDir = normalize(-light.direction);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = light.color * (diff * diffuseTex);
	
	//specular
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = spec * light.color * specularTex * diff;  
	
    vec3 result = (diffuse + specular);
    return result;
}

vec3 calcPointLight(PointLight light, vec3 diffuseTex, vec3 specularTex) {
	float distance = length(light.position - fragPos);
	float attenuation = 1.0f / (1.0f + light.linear * distance + light.quadratic * (distance * distance));    
	
	//diffuse
	vec3 norm = normalize(fragNormal);
	vec3 lightDir = normalize(light.position - fragPos);
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = light.color * (diff * diffuseTex);
	
	//specular
	vec3 viewDir = normalize(viewPos - fragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	vec3 specular = spec * light.color * specularTex * diff; 
	
    vec3 result = (diffuse + specular) * attenuation;
    return light.color * diff * attenuation;
}

void main() {
	vec3 diffuseTex = vec3(0, 0, 0);
	vec3 specularTex = vec3(0, 0, 0);

	if (material.diffuseTextured) {
		diffuseTex = texture(material.diffuse, texCoord).rgb;
	} else {
		diffuseTex = material.diffuseColor;
	}

	if (material.specularTextured) {
		specularTex = texture(material.specular, texCoord).rgb;
	} else {
		specularTex = material.specularColor;
	}
	

	//diffuseTex = vec3(0.5, 0.7, 0.3);
	//specularTex = vec3(0.5, 0.5, 0.5);

	vec3 ambientColor = ambient * diffuseTex;

	//vec3 lightCombined = calcPointLight(pointLights[0], diffuseTex, specularTex);
	vec3 lightCombined = vec3(0, 0, 0);

	for (int i = 0; i < pointLightCount; i++) {
		if (i < MAX_POINT_LIGHTS) {
			lightCombined += calcPointLight(pointLights[i], diffuseTex, specularTex);
		}
	}

    //color = vec4(reflectcolor, 1.0f);

    vec3 I = normalize(fragPos - cameraPos);
    vec3 R = reflect(I, normalize(fragNormal));
    vec3 reflectColor = specularTex * lightCombined ;
	
    color = vec4(ambientColor + (lightCombined * diffuseTex), 1.0f);

    // Check whether fragment output is higher than threshold, if so output as brightness color
    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    if(brightness > 1.5)
        brightColor = vec4(color.rgb, 1.0);
    else
    	brightColor = vec4(0, 0, 0, 1.0);
}