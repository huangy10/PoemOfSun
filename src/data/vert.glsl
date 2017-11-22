//#version 150

uniform mat4 transform;
uniform float time;

//attribute vec4 position;
//in vec4 color;
attribute vec4 ring;

varying vec4 vertColor;

float noise(vec3 loc) {
    return 1;
}

void main() {
    vec4 pos;
    float angle = ring.x;
    float radius = ring.y;
    float offset = ring.z;
    float idx = ring.w;
    float l;
    
    pos.x = cos(angle);
    pos.y = sin(angle);
    l = noise(vec3(offset + pos.x, offset + pos.y, time)) * radius * sin(time) * 5;
    pos *= l;
    
    pos.z = 0;
    pos.w = 1;
    
    gl_Position = transform * pos;
    vertColor = vec4(1.0, 0.25, 0.031, 0.5);
}


