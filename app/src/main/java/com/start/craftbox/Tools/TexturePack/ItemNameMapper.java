package com.start.craftbox.Tools.TexturePack;

import java.util.HashMap;
import java.util.Map;

public class ItemNameMapper {

    private static final Map<String, String> MAPPING = new HashMap<>();

    static {
        // ========== 第0行（索引0-31） ==========
        put("苹果", "textures/items/apple.png");
        put("附魔苹果", "textures/items/apple_golden.png");
        put("箭", "textures/items/arrow.png");
        put("木头斧子", "textures/items/wood_axe.png");
        put("石头斧子", "textures/items/stone_axe.png");
        put("铁斧", "textures/items/iron_axe.png");
        put("金斧", "textures/items/gold_axe.png");
        put("钻石斧", "textures/items/diamond_axe.png");
        put("床", "textures/items/bed_red.png");   // 使用红色床
        put("熟牛排", "textures/items/beef_cooked.png");
        put("生牛排", "textures/items/beef_raw.png");
        put("烈焰粉", "textures/items/blaze_powder.png");
        put("烈焰棒", "textures/items/blaze_rod.png");
        put("橡木船", "textures/items/boat_oak.png");
        put("深色橡木船", "textures/items/boat_darkoak.png");
        put("桦木船", "textures/items/boat_birch.png");
        put("丛林木船", "textures/items/boat_jungle.png");
        put("合金欢木船", "textures/items/boat_acacia.png");
        put("云杉木船", "textures/items/boat_spruce.png");
        put("骨头", "textures/items/bone.png");
        put("附魔书", "textures/items/book_enchanted.png");
        put("书", "textures/items/book_normal.png");
        put("书与笔", "textures/items/book_writable.png");
        put("成书", "textures/items/book_written.png");
        put("missing", null);
        put("皮革靴", "textures/items/leather_boots.tga");
        put("锁链靴", "textures/items/chainmail_boots.png");
        put("铁靴", "textures/items/iron_boots.png");
        put("金靴", "textures/items/gold_boots.png");
        put("钻石靴", "textures/items/diamond_boots.png");
        put("碗", "textures/items/bowl.png");
        put("拉满的弓箭", "textures/items/bow_pulling_2.png");
        put("拉弓状态半拉", "textures/items/bow_pulling_0.png");
        put("拉弓状态3/4拉", "textures/items/bow_pulling_1.png");
        put("弓", "textures/items/bow_standby.png");

        // ========== 第1行（索引32-63） ==========
        put("面包", "textures/items/bread.png");
        put("酿药台", "textures/items/brewing_stand.png");
        put("砖块", "textures/items/brick.png");
        put("空桶", "textures/items/bucket_empty.png");
        put("牛奶桶", "textures/items/bucket_milk.png");
        put("水桶", "textures/items/bucket_water.png");
        put("岩浆桶", "textures/items/bucket_lava.png");
        put("蛋糕", "textures/items/cake.png");
        put("胡萝卜", "textures/items/carrot.png");
        put("金胡萝卜", "textures/items/carrot_golden.png");
        put("胡萝卜吊杆", "textures/items/carrot_on_a_stick.png");
        put("甜菜根", "textures/items/beetroot.png");
        put("炼药锅", "textures/items/cauldron.png");
        put("木炭", "textures/items/charcoal.png");
        put("皮革胸甲", "textures/items/leather_chestplate.png");
        put("锁链甲", "textures/items/chainmail_chestplate.png");
        put("铁甲", "textures/items/iron_chestplate.png");
        put("金甲", "textures/items/gold_chestplate.png");
        put("钻石甲", "textures/items/diamond_chestplate.png");
        put("熟鸡肉", "textures/items/chicken_cooked.png");
        put("生鸡肉", "textures/items/chicken_raw.png");
        put("粘土", "textures/items/clay_ball.png");
        put("煤炭", "textures/items/coal.png");
        put("红石比较器", "textures/items/comparator.png");
        put("曲奇", "textures/items/cookie.png");
        put("钻石", "textures/items/diamond.png");
        put("钻石马凯", "textures/items/diamond_horse_armor.png");
        put("铁门", "textures/items/door_iron.png");
        put("橡木门", "textures/items/door_wood.png");
        put("云杉木门", "textures/items/door_spruce.png");
        put("桦木门", "textures/items/door_birch.png");
        put("丛林木门", "textures/items/door_jungle.png");
        put("金合欢门", "textures/items/door_acacia.png");
        put("深色橡木门", "textures/items/door_dark_oak.png");

        // ========== 第2行（索引64-95） ==========
        put("墨囊", "textures/items/dye_powder_black_new.png");
        put("红色染料", "textures/items/dye_powder_red.png");
        put("绿色染料", "textures/items/dye_powder_green.png");
        put("可可豆", "textures/items/dye_powder_brown_new.png");
        put("青金石", "textures/items/dye_powder_blue_new.png");
        put("紫色染料", "textures/items/dye_powder_purple.png");
        put("青色染料", "textures/items/dye_powder_cyan.png");
        put("浅灰色染料", "textures/items/dye_powder_silver.png");
        put("灰色染料", "textures/items/dye_powder_gray.png");
        put("粉色染料", "textures/items/dye_powder_pink.png");
        put("黄绿色染料", "textures/items/dye_powder_lime.png");
        put("黄色染料", "textures/items/dye_powder_yellow.png");
        put("浅蓝色染料", "textures/items/dye_powder_light_blue.png");
        put("品红色染料", "textures/items/dye_powder_magenta.png");
        put("橙色染料", "textures/items/dye_powder_orange.png");
        put("骨粉", "textures/items/dye_powder_white_new.png");
        put("鸡蛋", "textures/items/egg.png");
        put("绿宝石", "textures/items/emerald.png");
        put("装备界面空位 鞋子", "textures/items/empty_armor_slot_boots.png");
        put("装备空位 胸甲", "textures/items/empty_armor_slot_chestplate.png");
        put("装备空位 头盔", "textures/items/empty_armor_slot_helmet.png");
        put("装备空位 裤子", "textures/items/empty_armor_slot_leggings.png");
        put("末影之眼", "textures/items/ender_eye.png");
        put("末影珍珠", "textures/items/ender_pearl.png");
        put("经验之瓶", "textures/items/experience_bottle.png");
        put("羽毛", "textures/items/feather.png");
        put("烈焰弹", "textures/items/fireball.png");
        put("烟花", "textures/items/fireworks.png");
        put("烟火之星 底部", "textures/items/firework_star.png");
        put("烟火之星 颜色", "textures/items/firework_star.png");
        put("钓鱼竿", "textures/items/fishing_rod_uncast.png");
        put("正在钓的钓鱼竿", "textures/items/fishing_rod_cast.png");


        put("生鱼", "textures/items/fish_raw.png");
        put("生鲑鱼", "textures/items/fish_salmon_raw.png");
        put("小丑鱼", "textures/items/fish_clownfish_raw.png");
        put("河豚", "textures/items/fish_pufferfish_raw.png");
        put("熟鱼", "textures/items/fish_cooked.png");
        put("熟鲑鱼", "textures/items/fish_salmon_cooked.png");
        put("燧石", "textures/items/flint.png");
        put("打火石", "textures/items/flint_and_steel.png");
        put("花盆", "textures/items/flower_pot.png");
        put("恶魂之泪", "textures/items/ghast_tear.png");
        put("萤石粉", "textures/items/glowstone_dust.png");
        put("皮革马鞍", "textures/items/leather_horse_armor.tga");
        put("金锭", "textures/items/gold_ingot.png");
        put("金粒", "textures/items/gold_nugget.png");
        put("火药", "textures/items/gunpowder.png");
        put("皮帽子", "textures/items/leather_helmet.tga");
        put("锁链帽子", "textures/items/chainmail_helmet.png");
        put("铁帽子", "textures/items/iron_helmet.png");
        put("金帽子", "textures/items/gold_helmet.png");
        put("钻石帽子", "textures/items/diamond_helmet.png");
        put("木锄头", "textures/items/wood_hoe.png");
        put("石锄头", "textures/items/stone_hoe.png");
        put("铁锄头", "textures/items/iron_hoe.png");
        put("金锄头", "textures/items/gold_hoe.png");
        put("钻石锄头", "textures/items/diamond_hoe.png");
        put("漏斗", "textures/items/hopper.png");
        put("铁马凯", "textures/items/iron_horse_armor.png");
        put("铁锭", "textures/items/iron_ingot.png");
        put("物品展示框", "textures/items/item_frame.png");
        put("栓绳", "textures/items/lead.png");
        put("皮革", "textures/items/leather.png");
        put("皮革裤子", "textures/items/leather_leggings.tga");
        put("锁链裤子", "textures/items/chainmail_leggings.png");
        put("铁裤子", "textures/items/iron_leggings.png");
        put("金裤子", "textures/items/gold_leggings.png");
        put("钻石裤子", "textures/items/diamond_leggings.png");
        put("岩浆膏", "textures/items/magma_cream.png");
        put("空地图", "textures/items/map_empty.png");
        put("地图", "textures/items/map_filled.png");
        put("西瓜", "textures/items/melon.png");
        put("金西瓜", "textures/items/melon_speckled.png");
        put("箱子矿车", "textures/items/minecart_chest.png");
        put("熔炉矿车", "textures/items/minecart_furnace.png");
        put("漏斗矿车", "textures/items/minecart_hopper.png");
        put("空矿车", "textures/items/minecart_normal.png");
        put("TNT矿车", "textures/items/minecart_tnt.png");
        put("蘑菇煲", "textures/items/mushroom_stew.png");
        put("甜菜汤", "textures/items/beetroot_soup.png");
        put("命名牌", "textures/items/name_tag.png");
        put("下界砖块", "textures/items/netherbrick.png");
        put("下界之心", "textures/items/nether_star.png");
        put("地狱疣", "textures/items/nether_wart.png");
        put("画", "textures/items/painting.png");
        put("纸", "textures/items/paper.png");

        // ========== 第4行（索引128-159） ==========
        put("木镐", "textures/items/wood_pickaxe.png");
        put("石镐", "textures/items/stone_pickaxe.png");
        put("铁镐", "textures/items/iron_pickaxe.png");
        put("金镐", "textures/items/gold_pickaxe.png");
        put("钻石镐", "textures/items/diamond_pickaxe.png");
        put("熟猪排", "textures/items/porkchop_cooked.png");
        put("生猪排", "textures/items/porkchop_raw.png");
        put("马铃薯", "textures/items/potato.png");
        put("熟马铃薯", "textures/items/potato_baked.png");
        put("毒马铃薯", "textures/items/potato_poisonous.png");
        put("水瓶", "textures/items/potion_bottle_drinkable.png");
        put("迅捷药水", "textures/items/potion_bottle_moveSpeed.png");
        put("迟缓药水", "textures/items/potion_bottle_moveSlowdown.png");
        put("missing", null);
        put("missing", null);
        put("力量药水", "textures/items/potion_bottle_damageBoost.png");
        put("瞬间治疗药水", "textures/items/potion_bottle_heal.png");
        put("瞬间伤害药水", "textures/items/potion_bottle_harm.png");
        put("missing", null);
        put("missing", null);
        put("再生药水", "textures/items/potion_bottle_regeneration.png");
        put("missing", null);
        put("抗火药水", "textures/items/potion_bottle_fireResistance.png");
        put("跳跃提升药水", "textures/items/potion_bottle_jump.png");
        put("水肺药水", "textures/items/potion_bottle_waterBreathing.png");
        put("隐身药水", "textures/items/potion_bottle_invisibility.png");
        put("missing", null);
        put("夜视药水", "textures/items/potion_bottle_nightVision.png");
        put("missing", null);
        put("虚弱药水", "textures/items/potion_bottle_weakness.png");
        put("剧毒药水", "textures/items/potion_bottle_poison.png");
        put("missing", null);
        put("missing", null);
        put("missing", null);
        put("missing", null);
        put("空瓶子", "textures/items/potion_bottle_empty.png");
        put("喷溅药水", "textures/items/potion_bottle_splash.png");
        put("missing", null);
        put("missing", null);
        put("南瓜派", "textures/items/pumpkin_pie.png");
        put("石英", "textures/items/quartz.png");
        put("missing", null);
        put("missing", null);
        // 喷溅药水映射（根据普通药水类型）
        put("喷溅水瓶", "textures/items/potion_bottle_splash.png");              // 默认
        put("喷溅迅捷药水", "textures/items/potion_bottle_splash_moveSpeed.png");
        put("喷溅迟缓药水", "textures/items/potion_bottle_splash_moveSlowdown.png");
        put("喷溅力量药水", "textures/items/potion_bottle_splash_damageBoost.png");
        put("喷溅瞬间治疗药水", "textures/items/potion_bottle_splash_heal.png");
        put("喷溅瞬间伤害药水", "textures/items/potion_bottle_splash_harm.png");
        put("喷溅再生药水", "textures/items/potion_bottle_splash_regeneration.png");
        put("喷溅抗火药水", "textures/items/potion_bottle_splash_fireResistance.png");
        put("喷溅跳跃提升药水", "textures/items/potion_bottle_splash_jump.png");
        put("喷溅水肺药水", "textures/items/potion_bottle_splash_waterBreathing.png");
        put("喷溅隐身药水", "textures/items/potion_bottle_splash_invisibility.png");
        put("喷溅夜视药水", "textures/items/potion_bottle_splash_nightVision.png");
        put("喷溅虚弱药水", "textures/items/potion_bottle_splash_weakness.png");
        put("喷溅剧毒药水", "textures/items/potion_bottle_splash_poison.png");
        // 默认喷溅药水（用于缺失或空瓶子位置）
        put("喷溅药水", "textures/items/potion_bottle_splash.png");
        for (int i = 0; i < 12; i++) {
            put("唱片", null);
        }
        put("missing", null);
        put("missing", null);
        put("missing", null);
        put("红石粉", "textures/items/redstone_dust.png");
        put("甘蔗", "textures/items/reeds.png");
        put("红石中继器", "textures/items/repeater.png");
        put("腐肉", "textures/items/rotten_flesh.png");
        put("马鞍", "textures/items/saddle.png");
        put("西瓜种子", "textures/items/seeds_melon.png");
        put("南瓜种子", "textures/items/seeds_pumpkin.png");
        put("小麦种子", "textures/items/seeds_wheat.png");
        put("甜菜种子", "textures/items/seeds_beetroot.png");
        put("剪刀", "textures/items/shears.png");
        put("木铲子", "textures/items/wood_shovel.png");
        put("石铲子", "textures/items/stone_shovel.png");
        put("铁铲子", "textures/items/iron_shovel.png");
        put("金铲子", "textures/items/gold_shovel.png");
        put("钻石铲子", "textures/items/diamond_shovel.png");
        put("告示牌", "textures/items/sign.png");
        put("爬行者头颅", "assets:creeper_skull.png");
        put("骷髅头颅", "assets:skeleton_skull.png");
        put("史蒂夫头颅", "assets:steve_skull.png");
        put("下界骷髅头颅", "assets:winter_skeleton_skull.png");
        put("僵尸头颅", "assets:zombie_skull.png");
        put("粘液球", "textures/items/slimeball.png");
        put("雪球", "textures/items/snowball.png");
        for (int i = 0; i < 27; i++) {
            put("生物蛋", "spawn_egg");
        }
        put("蜘蛛眼", "textures/items/spider_eye.png");
        put("发酵蜘蛛眼", "textures/items/spider_eye_fermented.png");
        put("木棍", "textures/items/stick.png");
        put("线", "textures/items/string.png");
        put("糖", "textures/items/sugar.png");
        put("木剑", "textures/items/wood_sword.png");
        put("石剑", "textures/items/stone_sword.png");
        put("铁剑", "textures/items/iron_sword.png");
        put("金剑", "textures/items/gold_sword.png");
        put("钻石剑", "textures/items/diamond_sword.png");
        put("小麦", "textures/items/wheat.png");
        put("钟", "textures/items/clock_item.png");
        put("指南针", "textures/items/compass_item.png");
        put("熟兔肉", "textures/items/rabbit_cooked.png");
        put("兔子脚", "textures/items/rabbit_foot.png");
        put("兔子皮", "textures/items/rabbit_hide.png");
        put("生兔肉", "textures/items/rabbit_raw.png");
        put("兔肉煲", "textures/items/rabbit_stew.png");
        put("拉杆", "textures/items/lever.png");
        put("相机", null);
    }

    private static void put(String chinese, String path) {
        MAPPING.put(chinese, path);
    }

    public static String getPath(String chineseName) {
        return MAPPING.get(chineseName);
    }
}
