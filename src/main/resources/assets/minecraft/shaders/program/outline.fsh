#version 150

uniform sampler2D DiffuseSampler;
in vec2 texCoord;
uniform vec2 resolution;
uniform vec4 color;
out vec4 fragColor;
uniform int lineWidth;

void main() {
    vec2 texelSize = vec2(1.0 / resolution.x * 0.5, 1.0 / resolution.y * 0.5);
    bool isOutline = false;
    bool isFill = false;

    for (int x = -lineWidth; x <= lineWidth; x++) {
        for (int y = -lineWidth; y <= lineWidth; y++) {
            vec4 sample1 = texture(DiffuseSampler, texCoord + vec2(texelSize.x * x, texelSize.y * y));
            if (sample1.a > 0.0) {
                if (x == 0 && y == 0) {
                    isFill = true;
                } else {
                    isOutline = true;
                }
            }
        }
    }

    if (isFill) {
        fragColor = color;
    } else if (isOutline) {
        fragColor = vec4(color.r, color.g, color.b, 1.0);
    } else {
        fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}