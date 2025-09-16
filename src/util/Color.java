package util;

import org.joml.Vector4f;

public class Color {
    public float r, g, b, a;

    // Predefined colors
    public static final Color WHITE = new Color(1, 1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0, 1);
    public static final Color RED = new Color(255, 0, 0, 255);
    public static final Color GREEN = new Color(0, 1, 0, 1);
    public static final Color BLUE = new Color(0, 0, 1, 1);
    public static final Color YELLOW = new Color(150, 1, 0, 255);
    public static final Color CYAN = new Color(0, 1, 1, 1);
    public static final Color MAGENTA = new Color(1, 0, 1, 1);

    public Color(float r, float g, float b, float a) {
        this.r = r; this.g = g; this.b = b; this.a = a;
    }

    public Color(Color other) {
        this.r = other.r; this.g = other.g;
        this.b = other.b; this.a = other.a;
    }

    // From hex string
    public static Color fromHex(String hex) {
        hex = hex.replace("#", "");
        return new Color(
                Integer.parseInt(hex.substring(0, 2), 16) / 255f,
                Integer.parseInt(hex.substring(2, 4), 16) / 255f,
                Integer.parseInt(hex.substring(4, 6), 16) / 255f,
                hex.length() > 6 ? Integer.parseInt(hex.substring(6, 8), 16) / 255f : 1f
        );
    }

    // HSV conversion
    public static Color fromHSV(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float r, g, b;
        if (h < 60) { r = c; g = x; b = 0; }
        else if (h < 120) { r = x; g = c; b = 0; }
        else if (h < 180) { r = 0; g = c; b = x; }
        else if (h < 240) { r = 0; g = x; b = c; }
        else if (h < 300) { r = x; g = 0; b = c; }
        else { r = c; g = 0; b = x; }

        return new Color(r + m, g + m, b + m, 1);
    }

    public Vector4f toVector4f() {
        return new Vector4f(r, g, b, a);
    }

//    public void writeToBuffer(FloatBuffer buffer) {
//        buffer.put(r).put(g).put(b).put(a);
//    }

    public Color lerp(Color target, float t) {
        return new Color(
                r + (target.r - r) * t,
                g + (target.g - g) * t,
                b + (target.b - b) * t,
                a + (target.a - a) * t
        );
    }
}