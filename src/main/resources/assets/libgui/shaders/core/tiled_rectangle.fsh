#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;

uniform vec2 LibGuiRectanglePos;
uniform vec2 LibGuiTileDimensions;
uniform vec4 LibGuiTileUvs;

in vec2 vertexPosition;
out vec4 fragColor;

float lerp(float time, float a, float b) {
    return (1 - time) * a + time * b;
}

vec2 getTextureCoords(vec2 pos) {
    float deltaX = mod(pos.x - LibGuiRectanglePos.x, LibGuiTileDimensions.x) / LibGuiTileDimensions.x;
    float deltaY = mod(pos.y - LibGuiRectanglePos.y, LibGuiTileDimensions.y) / LibGuiTileDimensions.y;
    return vec2(
            lerp(deltaX, LibGuiTileUvs.x, LibGuiTileUvs.z),
            lerp(deltaY, LibGuiTileUvs.y, LibGuiTileUvs.w)
    );
}

void main() {
    vec4 color = texture(Sampler0, getTextureCoords(vertexPosition));
    if (color.a < 0.1) {
        discard;
    }
    fragColor = color * ColorModulator;
}
