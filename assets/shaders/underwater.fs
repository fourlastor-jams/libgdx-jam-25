#ifdef GL_ES
    #define PRECISION mediump
    precision PRECISION float;
    precision PRECISION int;
#else
    #define PRECISION
#endif

varying vec2 v_texCoords;
uniform float u_time;
uniform sampler2D u_texture;

float GodRay(float scale,float threshold,float speed,float angle, vec2 uv){
	float value = pow(sin((uv.x+uv.y*-angle+(u_time / 5.0)*speed)*scale*5.0),6.0);
    value+=float(threshold/15.0<value);
    return clamp(value,0.0,1.0);
}

void main()
{
	vec2 uv = vec2(v_texCoords.s, 1.0 - v_texCoords.t);
	vec4 color = texture2D(u_texture, v_texCoords);
    float light = GodRay(22.0,0.5,-0.003,0.2, uv)*	0.3;
    light+=GodRay(47.0,	0.99,	0.02,	0.2, uv)*	0.1;
    light+=GodRay(25.0,0.9,		-0.01,	0.2, uv)*	0.2;
    light+=GodRay(52.0,	0.4,	0.0001,	0.2, uv)*	0.1;
    light+=GodRay(49.0,	0.4,	0.0003,	0.2, uv)*	0.1;
    light+=GodRay(57.0,	0.4,	-0.0001,0.2, uv)*	0.1;
    light+=GodRay(200.0,0.8,	-0.0001,0.2, uv)*	0.05;
    light-=pow((1.0-uv.y)*0.7,0.8);
    light=max(light,0.0);
    vec3 LightColor = vec3(1.0,0.95,0.85);
    vec3 WaterTop = vec3(0.15,0.6,0.7);
    vec3 WaterBot = vec3(0.08,0.12,0.3);
    vec3 WaterColor = WaterBot+uv.y*(WaterTop-WaterBot);
    vec3 Color = WaterColor+light*(LightColor-WaterColor);
	gl_FragColor = vec4(Color, color.a) * 0.3 + color * 0.7;
}
