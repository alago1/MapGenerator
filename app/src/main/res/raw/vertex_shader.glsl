attribute vec4 position;
attribute vec4 color;

uniform mat4 matrix;
varying vec4 interpolated_color;

void main() {
    gl_Position = matrix * position;
    interpolated_color = color;
}