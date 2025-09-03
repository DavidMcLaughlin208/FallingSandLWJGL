package util;

import elements.ElementType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ColorConstants {

    private static final Map<String, Color> colorCache = new ConcurrentHashMap<>();

    private static final Map<ElementType, List<Color>> elementColorMap = new HashMap<>();
    private static final Map<String, List<Color>> namedColorMap = new HashMap<>();
    private static final Map<String, List<Color>> effectsColorMap = new HashMap<>();
    private static final Random random = new Random();

    private static List<Color> fireColors = new ArrayList<>();

    static {
        fireColors.add(Color.RED);
        fireColors.add(Color.YELLOW);
        fireColors.add(Color.YELLOW);
//        fireColors.add(Color.ORANGE);
//        fireColors.add(Color.ORANGE);
//        fireColors.add(Color.ORANGE);
    }

    public static Color getRandomFireColor() {
        return fireColors.get((int) Math.floor(Math.random() * fireColors.size()));
    }

    // Movable Solids
    private static final Color SAND_1 = new Color(255, 255, 0, 255);
    private static final Color SAND_2 = new Color(178, 201, 6, 255);
    private static final Color SAND_3 = new Color(233, 252, 90, 255);

    private static final Color DIRT_1 = new Color(96, 47, 18, 255);
    private static final Color DIRT_2 = new Color(135, 70, 32, 255);
    private static final Color DIRT_3 = new Color(79, 38, 15, 255);

    private static final Color COAL_1 = new Color(53, 53, 53, 255);
    private static final Color COAL_2 = new Color(34, 35, 38, 255);
    private static final Color COAL_3 = new Color(65, 65, 65, 255);

    private static final Color EMBER = new Color(102, 59, 0, 255);

    private static final Color GUNPOWDER_1 = new Color(255, 142, 142, 255);
    private static final Color GUNPOWDER_2 = new Color(255, 91, 91, 255);
    private static final Color GUNPOWDER_3 = new Color(219, 160, 160, 255);

    private static final Color SNOW = new Color(1, 1, 1, 255);

    private static final Color PLAYERMEAT = new Color(255, 255, 0, 255);

    // Immovable Solids
    private static final Color STONE = new Color(150, 150, 150, 255);

    private static final Color BRICK_1 = new Color(188, 3, 0, 255);
    private static final Color BRICK_2 = new Color(188, 3, 0, 255);
    private static final Color BRICK_3 = new Color(188, 3, 0, 255);
    private static final Color BRICK_4 = new Color(188, 3, 0, 255);
    private static final Color BRICK_5 = new Color(206, 206, 206, 255);

    private static final Color WOOD_1 = new Color(165, 98, 36, 255);
    private static final Color WOOD_2 = new Color(61, 33, 7, 255);
    private static final Color WOOD_3 = new Color(140, 74, 12, 255);

    private static final Color TITANIUM = new Color(234, 234, 234, 255);

    private static final Color SLIME_MOLD_1 = new Color(255, 142, 243, 255);
    private static final Color SLIME_MOLD_2 = new Color(201, 58, 107, 255);
    private static final Color SLIME_MOLD_3 = new Color(234, 35, 213, 255);


    private static final Color GROUND = new Color(68, 37, 37, 255);


    // Liquids
    private static final Color WATER = new Color(28, 86, 234, 204);

    private static final Color OIL = new Color(55, 60, 73, 204);

    private static final Color ACID = new Color(0, 255, 0, 255);

    private static final Color LAVA = new Color(255, 165, 0, 255);

    private static final Color BLOOD = new Color(234, 0, 0, 204);

    private static final Color CEMENT = new Color(209, 209, 209, 255);


    // Gasses
    private static final Color SMOKE = new Color(147, 147, 147, 0.5f);

    private static final Color FLAMMABLE_GAS = new Color(0, 255, 0, 127);

    private static final Color SPARK = new Color(89, 35, 13, 255);

    private static final Color STEAM_1 = new Color(204, 204, 204, 204);
    private static final Color STEAM_2 = new Color(204, 204, 204, 25);
    private static final Color STEAM_3 = new Color(204, 204, 204, 120);

    // Effects
    private static final String FIRE_NAME = "Fire";
    private static final Color FIRE_1 = new Color(89, 35, 13, 255);
    private static final Color FIRE_2 = new Color(100, 27, 7, 255);
    private static final Color FIRE_3 = new Color(77, 10, 20, 255);

    // Others
    private static final Color PARTICLE = new Color(0, 0, 0, 0);
    private static final Color EMPTY_CELL = new Color(0, 0, 0, 0);

    private static final String GRASS = "Grass";
    private static final Color GRASS_1 = new Color(0, 216, 93, 255);
    private static final Color GRASS_2 = new Color(0, 173, 75, 255);
    private static final Color GRASS_3 = new Color(0, 239, 103, 255);

    static {
        Arrays.stream(ElementType.values()).forEach(type -> elementColorMap.put(type, new ArrayList<>()));
        elementColorMap.get(ElementType.SAND).add(SAND_1);
        elementColorMap.get(ElementType.SAND).add(SAND_2);
        elementColorMap.get(ElementType.SAND).add(SAND_3);

        elementColorMap.get(ElementType.DIRT).add(DIRT_1);
        elementColorMap.get(ElementType.DIRT).add(DIRT_2);
        elementColorMap.get(ElementType.DIRT).add(DIRT_3);

        elementColorMap.get(ElementType.COAL).add(COAL_1);
        elementColorMap.get(ElementType.COAL).add(COAL_2);
        elementColorMap.get(ElementType.COAL).add(COAL_3);

        elementColorMap.get(ElementType.GUNPOWDER).add(GUNPOWDER_1);
        elementColorMap.get(ElementType.GUNPOWDER).add(GUNPOWDER_2);
        elementColorMap.get(ElementType.GUNPOWDER).add(GUNPOWDER_3);

        elementColorMap.get(ElementType.PLAYERMEAT).add(PLAYERMEAT);

        elementColorMap.get(ElementType.EMBER).add(EMBER);

        elementColorMap.get(ElementType.SNOW).add(SNOW);

        elementColorMap.get(ElementType.STONE).add(STONE);

        elementColorMap.get(ElementType.BRICK).add(BRICK_1);
        elementColorMap.get(ElementType.BRICK).add(BRICK_2);
        elementColorMap.get(ElementType.BRICK).add(BRICK_3);
        elementColorMap.get(ElementType.BRICK).add(BRICK_4);
        elementColorMap.get(ElementType.BRICK).add(BRICK_5);


        elementColorMap.get(ElementType.WOOD).add(WOOD_1);
        elementColorMap.get(ElementType.WOOD).add(WOOD_2);
        elementColorMap.get(ElementType.WOOD).add(WOOD_3);

        elementColorMap.get(ElementType.TITANIUM).add(TITANIUM);

        elementColorMap.get(ElementType.GROUND).add(GROUND);

        elementColorMap.get(ElementType.SLIMEMOLD).add(SLIME_MOLD_1);
        elementColorMap.get(ElementType.SLIMEMOLD).add(SLIME_MOLD_2);
        elementColorMap.get(ElementType.SLIMEMOLD).add(SLIME_MOLD_3);

        elementColorMap.get(ElementType.WATER).add(WATER);

        elementColorMap.get(ElementType.OIL).add(OIL);

        elementColorMap.get(ElementType.ACID).add(ACID);

        elementColorMap.get(ElementType.LAVA).add(LAVA);

        elementColorMap.get(ElementType.BLOOD).add(BLOOD);

        elementColorMap.get(ElementType.SMOKE).add(SMOKE);

        elementColorMap.get(ElementType.CEMENT).add(CEMENT);

        elementColorMap.get(ElementType.STEAM).add(STEAM_1);
        elementColorMap.get(ElementType.STEAM).add(STEAM_2);
        elementColorMap.get(ElementType.STEAM).add(STEAM_3);

        elementColorMap.get(ElementType.FLAMMABLEGAS).add(FLAMMABLE_GAS);

        elementColorMap.get(ElementType.SPARK).add(SPARK);
//
        elementColorMap.get(ElementType.EXPLOSIONSPARK).add(Color.YELLOW);

        elementColorMap.get(ElementType.PARTICLE).add(PARTICLE);

        elementColorMap.get(ElementType.EMPTYCELL).add(EMPTY_CELL);

        effectsColorMap.put(FIRE_NAME, new ArrayList<>());
        effectsColorMap.get(FIRE_NAME).add(FIRE_1);
        effectsColorMap.get(FIRE_NAME).add(FIRE_2);
        effectsColorMap.get(FIRE_NAME).add(FIRE_3);

        namedColorMap.put(GRASS, new ArrayList<>());
        namedColorMap.get(GRASS).add(GRASS_1);
        namedColorMap.get(GRASS).add(GRASS_2);
        namedColorMap.get(GRASS).add(GRASS_3);



        List<ElementType> missingElements = Arrays.stream(ElementType.values()).filter(type -> elementColorMap.get(type).size() == 0).collect(Collectors.toList());
        if (missingElements.size() > 0) {
            throw new IllegalStateException("Elements " + missingElements.toString() + "have no assigned colors");
        }

        // Place custom textures in materialsMap
    }

    public static Color getColorByName(String name) {
        return namedColorMap.get(name).get(random.nextInt(namedColorMap.get(name).size()));
    }

    public static Color getColorForElementType(ElementType elementType, int x, int y) {
        return getColorForElementType(elementType);
//        List<Color> colorList = elementColorMap.get(elementType);
//        return elementColorMap.get(elementType).get(random.nextInt(colorList.size()));
    }

    public static Color getColorForElementType(ElementType elementType) {
        List<Color> colorList = elementColorMap.get(elementType);
        return elementColorMap.get(elementType).get(random.nextInt(colorList.size()));
    }

}
