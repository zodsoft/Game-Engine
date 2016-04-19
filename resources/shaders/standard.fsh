#version 330 core

struct Material {
	sampler2D diffuse;
	sampler2D specular;
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

out vec4 color;

uniform Material material;
uniform DirectionalLight dirLight;
uniform PointLight[2] pointLights;
uniform vec3 ambient;

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
    return result;
}

void main() {
	vec3 diffuseTex = texture(material.diffuse, texCoord).rgb;
	vec3 specularTex = texture(material.specular, texCoord).rgb;

	vec3 ambientColor = ambient * diffuseTex;
	
    color = vec4(ambientColor + calcPointLight(pointLights[0], diffuseTex, specularTex) + calcPointLight(pointLights[1], diffuseTex, specularTex), 1.0f);
}