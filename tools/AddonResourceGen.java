import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates every addon resource for the independent moremoremooncake mod: 66 two-filling
 * combos x 8 states slice textures + item models + lang entries + survival recipes (slice craft,
 * wax, oxidize).
 * <p>
 * Run:  java AddonResourceGen.java <projectRoot>
 */
public class AddonResourceGen {
    static final String MOD = "moremoremooncake";

    // flavor: registry, zh, en, crafting ingredient item, color csv
    static final String[][] FLAVORS = {
            {"lianrong", "莲蓉", "Lotus Paste", "minecraft:lily_pad", "235,216,172"},
            {"dousha", "豆沙", "Red Bean", "minecraft:cocoa_beans", "122,42,42"},
            {"zaoni", "枣泥", "Date Paste", "minecraft:sweet_berries", "138,56,36"},
            {"wuren", "五仁", "Five Kernel", "minecraft:pumpkin_seeds", "240,230,206"},
            {"yerong", "椰蓉", "Coconut", "minecraft:sugar", "250,250,250"},
            {"baiguo", "百果", "Assorted Fruit", "minecraft:apple", "210,170,120"},
            {"heizhima", "黑芝麻", "Black Sesame", "minecraft:ink_sac", "70,60,70"},
            {"banli", "板栗", "Chestnut", "minecraft:baked_potato", "150,96,50"},
            {"zishu", "紫薯", "Purple Potato", "minecraft:beetroot", "140,78,150"},
            {"yuni", "芋泥", "Taro", "minecraft:carrot", "160,130,190"},
            {"shuiguo", "水果", "Fruit", "minecraft:melon_slice", "230,130,120"},
            {"lvdousha", "绿豆沙", "Mung Bean", "minecraft:kelp", "110,150,80"},
    };

    static final String[] STATES = {"", "rusted", "weathered", "oxidized", "waxed", "waxed_rusted", "waxed_weathered", "waxed_oxidized"};
    static final String[] STATE_EN_PRE = {"", "Rusted ", "Weathered ", "Oxidized ", "Waxed ", "Waxed Rusted ", "Waxed Weathered ", "Waxed Oxidized "};
    static final String[] STATE_ZH_PRE = {"", "锈蚀的", "斑驳的", "氧化的", "涂蜡的", "涂蜡的锈蚀的", "涂蜡的斑驳的", "涂蜡的氧化的"};

    static Path root;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        root = Paths.get(args[0]).toAbsolutePath();
        List<double[]> combos = combos();
        genTextures(combos);
        genModels(combos);
        genLang(combos);
        genRecipes(combos);
        System.out.println("AddonResourceGen done -> " + root);
    }

    static List<double[]> combos() {
        List<double[]> out = new ArrayList<>();
        for (int i = 0; i < FLAVORS.length; i++) {
            for (int j = i + 1; j < FLAVORS.length; j++) {
                out.add(new double[]{i, j});
            }
        }
        return out;
    }

    static String itemId(int fi, int fj, String state) {
        return "mooncake_" + FLAVORS[fi][0] + "_" + FLAVORS[fj][0]
                + (state.isEmpty() ? "" : "_" + state);
    }

    static int[] color(String csv) {
        String[] p = csv.split(",");
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])};
    }

    // ---------------- textures ----------------

    static void genTextures(List<double[]> combos) throws IOException {
        Path dir = root.resolve("common/src/main/resources/assets/" + MOD + "/textures/item");
        Files.createDirectories(dir);
        int n = 0;
        for (double[] c : combos) {
            int fi = (int) c[0], fj = (int) c[1];
            int[] a = color(FLAVORS[fi][4]);
            int[] b = color(FLAVORS[fj][4]);
            for (String state : STATES) {
                String id = itemId(fi, fj, state);
                ImageIO.write(drawTexture(a, b, state, id), "png", dir.resolve(id + ".png").toFile());
                n++;
            }
        }
        System.out.println("textures: " + n);
    }

    static BufferedImage drawTexture(int[] a, int[] b, String state, String seed) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Random rnd = new Random(seed.hashCode());
        boolean waxed = state.startsWith("waxed");
        String base = waxed ? state.substring(5) : state;
        int[][] pal = palette(base);
        int[] crust = pal[0], edge = pal[1];
        int[] fillA = mix(a, crust, 0.18);
        int[] fillB = mix(b, crust, 0.18);

        int[][] patinaSpots = null;
        int[] patina = null;
        if (base.equals("weathered")) {
            patina = new int[]{111, 140, 78};
            patinaSpots = new int[][]{{4, 5}, {11, 4}, {13, 11}, {5, 13}};
        } else if (base.equals("oxidized")) {
            patina = new int[]{50, 112, 100};
            patinaSpots = new int[][]{{3, 4}, {12, 3}, {14, 12}, {5, 14}, {10, 11}, {7, 7}};
        }
        int[][] rustSpots = null;
        if (base.equals("rusted")) {
            rustSpots = new int[5][];
            for (int i = 0; i < rustSpots.length; i++) rustSpots[i] = new int[]{7 + rnd.nextInt(8), 5 + rnd.nextInt(10)};
        }
        boolean speckled = base.isEmpty() && (seed.matches(".*_heizhima_.*|.*_wuren_.*|.*_baiguo_.*|.*_banli_.*"));
        int[][] seeds = {{9, 8}, {10, 8}, {12, 8}, {11, 7}, {11, 9}};

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                double dx = x + 0.5 - 8, dy = y + 0.5 - 8;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist > 7.5) continue;
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < -22.5 || angle > 22.5) continue;
                int[] col = crust;
                if (dist <= 3.6) {
                    col = (x < 8) ? fillA.clone() : fillB.clone();
                    if (speckled) {
                        for (int[] s : seeds) {
                            if (x >= s[0] - 1 && x <= s[0] && y >= s[1] - 1 && y <= s[1]) {
                                col = (x == s[0] - 1 || y == s[1] - 1)
                                        ? new int[]{91, 58, 30} : new int[]{240, 230, 206};
                                break;
                            }
                        }
                    }
                }
                if (dist <= 3.6 && x == 8) {
                    col = mix(col, new int[]{80, 50, 25}, 0.25);
                }
                if (dist > 6.2) col = edge;
                if (rustSpots != null) {
                    for (int[] sp : rustSpots) if (x == sp[0] && y == sp[1]) col = new int[]{124, 58, 23};
                }
                if (patinaSpots != null) {
                    for (int[] p : patinaSpots) {
                        double pd = Math.sqrt((x + 0.5 - p[0]) * (x + 0.5 - p[0]) + (y + 0.5 - p[1]) * (y + 0.5 - p[1]));
                        if (pd <= (base.equals("oxidized") ? 2.0 : 1.7)) {
                            col = patina;
                            break;
                        }
                    }
                }
                if (waxed) {
                    if (x + y >= 9 && x + y <= 14) col = mix(col, new int[]{255, 255, 255}, 0.35);
                    if (x <= 4 && y <= 3) col = mix(col, new int[]{255, 255, 255}, 0.25);
                }
                if (Math.abs(Math.abs(angle) - 22.5) < 4.0) {
                    col = mix(col, new int[]{80, 50, 25}, 0.3);
                }
                img.setRGB(x, y, rgba(col));
            }
        }
        return img;
    }

    static int[][] palette(String base) {
        switch (base) {
            case "rusted": return new int[][]{{194, 112, 59}, {143, 77, 36}};
            case "weathered": return new int[][]{{169, 166, 90}, {124, 122, 60}};
            case "oxidized": return new int[][]{{95, 175, 158}, {62, 133, 120}};
            default: return new int[][]{{224, 164, 94}, {185, 127, 62}};
        }
    }

    static int[] mix(int[] a, int[] b, double t) {
        return new int[]{(int) (a[0] + (b[0] - a[0]) * t), (int) (a[1] + (b[1] - a[1]) * t), (int) (a[2] + (b[2] - a[2]) * t)};
    }

    static int rgba(int[] c) {
        return 0xFF000000 | (c[0] << 16) | (c[1] << 8) | c[2];
    }

    // ---------------- models ----------------

    static void genModels(List<double[]> combos) throws IOException {
        Path dir = root.resolve("common/src/main/resources/assets/" + MOD + "/models/item");
        Files.createDirectories(dir);
        int n = 0;
        for (double[] c : combos) {
            int fi = (int) c[0], fj = (int) c[1];
            for (String state : STATES) {
                String id = itemId(fi, fj, state);
                String json = "{\n  \"parent\": \"minecraft:item/generated\",\n  \"textures\": {\n"
                        + "    \"layer0\": \"" + MOD + ":item/" + id + "\"\n  }\n}\n";
                Files.write(dir.resolve(id + ".json"), json.getBytes(StandardCharsets.UTF_8));
                n++;
            }
        }
        System.out.println("models: " + n);
    }

    // ---------------- lang ----------------

    static void genLang(List<double[]> combos) throws IOException {
        Map<String, String> en = new LinkedHashMap<>();
        Map<String, String> zh = new LinkedHashMap<>();
        en.put("itemGroup." + MOD, "MoreMore Mooncake (Double)");
        zh.put("itemGroup." + MOD, "更多月饼·双馅");
        en.put("jei." + MOD + ".double_filling", "Double-Filling Combos");
        zh.put("jei." + MOD + ".double_filling", "双馅组合");
        int nEn = 0, nZh = 0;
        for (double[] c : combos) {
            int fi = (int) c[0], fj = (int) c[1];
            String comboEn = FLAVORS[fi][2] + " + " + FLAVORS[fj][2];
            String comboZh = FLAVORS[fi][1] + FLAVORS[fj][1];
            for (int s = 0; s < STATES.length; s++) {
                String id = itemId(fi, fj, STATES[s]);
                en.put("item." + MOD + "." + id, STATE_EN_PRE[s] + comboEn + " Mooncake Slice");
                zh.put("item." + MOD + "." + id, STATE_ZH_PRE[s] + comboZh + "月饼块");
                nEn++;
                nZh++;
            }
        }
        writeLang(en, "en_us");
        writeLang(zh, "zh_cn");
        System.out.println("lang: " + nEn + " en, " + nZh + " zh");
    }

    static void writeLang(Map<String, String> map, String name) throws IOException {
        Path file = root.resolve("common/src/main/resources/assets/" + MOD + "/lang/" + name + ".json");
        Files.createDirectories(file.getParent());
        StringBuilder sb = new StringBuilder("{\n");
        int i = 0;
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append("  \"").append(escape(e.getKey())).append("\": \"").append(escape(e.getValue())).append("\"");
            if (++i < map.size()) sb.append(",");
            sb.append("\n");
        }
        sb.append("}\n");
        Files.write(file, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ---------------- recipes ----------------

    static void genRecipes(List<double[]> combos) throws IOException {
        Path dir = root.resolve("common/src/main/resources/data/" + MOD + "/recipe");
        Files.createDirectories(dir);
        int n = 0;
        String[][] oxidizeSteps = {
                {"", "rusted", "minecraft:iron_nugget"},
                {"rusted", "weathered", "minecraft:copper_ingot"},
                {"weathered", "oxidized", "minecraft:copper_block"},
        };
        for (double[] c : combos) {
            int fi = (int) c[0], fj = (int) c[1];
            String baseId = itemId(fi, fj, "");
            String ingA = FLAVORS[fi][3];
            String ingB = FLAVORS[fj][3];
            writeShapeless(dir.resolve("slice_" + baseId + ".json"),
                    List.of("{\"item\": \"minecraft:wheat\"}", "{\"item\": \"" + ingA + "\"}", "{\"item\": \"" + ingB + "\"}"),
                    baseId, 2, MOD + ":slice");
            n++;
            String[] unwaxed = {"", "rusted", "weathered", "oxidized"};
            String[] waxed = {"waxed", "waxed_rusted", "waxed_weathered", "waxed_oxidized"};
            for (int i = 0; i < 4; i++) {
                String from = itemId(fi, fj, unwaxed[i]);
                String to = itemId(fi, fj, waxed[i]);
                writeShapeless(dir.resolve("wax_" + from + ".json"),
                        List.of("{\"item\": \"" + MOD + ":" + from + "\"}", "{\"item\": \"minecraft:honeycomb\"}"),
                        to, 1, MOD + ":wax");
                n++;
            }
            for (String[] step : oxidizeSteps) {
                String from = itemId(fi, fj, step[0]);
                String to = itemId(fi, fj, step[1]);
                writeShapeless(dir.resolve("oxidize_" + from + ".json"),
                        List.of("{\"item\": \"" + MOD + ":" + from + "\"}", "{\"item\": \"" + step[2] + "\"}"),
                        to, 1, MOD + ":oxidize");
                n++;
            }
        }
        System.out.println("recipes: " + n);
    }

    static void writeShapeless(Path file, List<String> ingredientObjects, String resultId, int count, String group) throws IOException {
        String json = "{\n  \"type\": \"minecraft:crafting_shapeless\",\n  \"category\": \"misc\",\n"
                + "  \"group\": \"" + group + "\",\n  \"ingredients\": [\n"
                + "    " + String.join(",\n    ", ingredientObjects) + "\n  ],\n"
                + "  \"result\": {\n    \"id\": \"" + MOD + ":" + resultId + "\",\n    \"count\": " + count + "\n  }\n}\n";
        Files.write(file, json.getBytes(StandardCharsets.UTF_8));
    }
}