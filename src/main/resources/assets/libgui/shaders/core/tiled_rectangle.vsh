#version 150

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 LibGuiPositionMatrix;

out vec2 vertexPosition;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vec4 rawPos = inverse(LibGuiPositionMatrix) * vec4(Position, 1.0);
    vertexPosition = rawPos.xy;
}
