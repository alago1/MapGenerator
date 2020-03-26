attribute vec4 position;
attribute vec4 color;

uniform mat4 matrix;
varying vec4 interpolated_color;

void main() {
    interpolated_color = color;
    gl_Position = matrix * position;
}