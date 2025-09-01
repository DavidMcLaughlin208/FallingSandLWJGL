package elements.liquid;

import util.Color;
import org.joml.Vector3f;

public class Oil extends Liquid {

    public Oil(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,-124f,0);
        inertialResistance = 0;
        mass = 75;
        frictionFactor = 1f;
        density = 4;
        dispersionRate = 4;
        flammabilityResistance = 5;
        resetFlammabilityResistance = 2;
        fireDamage = 10;
        temperature = 10;
        health = 1000;
    }
}
