varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;
uniform sampler2D u_texture;
uniform vec2 u_resolution;

uniform float u_offsetX;
uniform float u_offsetY;
uniform vec4 u_outlineColor;

void main() {
    vec2 pixel = 1.0f / u_resolution;
    vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
    if(color.a == 0.0f){
        float alpha = texture2D(u_sampler2D, vec2(v_texCoord0.x+u_offsetX, v_texCoord0.y)).a +
        texture2D(u_sampler2D, vec2(v_texCoord0.x-u_offsetX, v_texCoord0.y)).a +
        texture2D(u_sampler2D, vec2(v_texCoord0.x, v_texCoord0.y+u_offsetY)).a +
        texture2D(u_sampler2D, vec2(v_texCoord0.x, v_texCoord0.y-u_offsetY)).a;
        if(alpha > 0.0f)color = u_outlineColor;
    }
    gl_FragColor = color;
}
