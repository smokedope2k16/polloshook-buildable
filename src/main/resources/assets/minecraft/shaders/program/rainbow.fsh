#version 150

uniform sampler2D DiffuseSampler;
in vec2 texCoord;
uniform vec2 resolution;
uniform vec4 color;
uniform float fillrainbow;
uniform float time;
uniform float saturation;
uniform float lightness;
uniform float factor;
uniform int lineWidth;
out vec4 fragColor;

vec3 hslToRgb(float h, float s, float l) {
    float c = (1.0 - abs(2.0 * l - 1.0)) * s;
    float x = c * (1.0 - abs(mod(h * 6.0, 2.0) - 1.0));
    float m = l - c / 2.0;
    vec3 rgb;

    if (h < 1.0 / 6.0) {
        rgb = vec3(c, x, 0.0);
    } else if (h < 2.0 / 6.0) {
        rgb = vec3(x, c, 0.0);
    } else if (h < 3.0 / 6.0) {
        rgb = vec3(0.0, c, x);
    } else if (h < 4.0 / 6.0) {
        rgb = vec3(0.0, x, c);
    } else if (h < 5.0 / 6.0) {
        rgb = vec3(x, 0.0, c);
    } else {
        rgb = vec3(c, 0.0, x);
    }

    return rgb + vec3(m);
}

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

    float hue = mod((texCoord.x + texCoord.y) * factor + time * 0.5, 1.0);
    vec3 rgbColor = hslToRgb(hue, saturation, lightness);

    if (isFill) {
        if (fillrainbow == 0.0) {
            fragColor = vec4(color);
        } else {
            fragColor = vec4(rgbColor, color.a);
        }
    } else if (isOutline) {
        fragColor = vec4(rgbColor, 1.0);
    } else {
        fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
