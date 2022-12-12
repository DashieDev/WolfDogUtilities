package wolfdogutilities.listeners;

import net.minecraft.Util;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.AnimalMakeLove;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wolfdogutilities.ChopinLogger;
import wolfdogutilities.config.ConfigHandler;
import wolfdogutilities.storage.WolfTameCountStorage;

public class WolfDogListener {
    
    //private final int CHAIN_INTERVAL
    
    private int lastTameTick;


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWolfTame(AnimalTameEvent ev) {
        if (ev.getAnimal().level.isClientSide) return;
        if (ev.getAnimal() instanceof Wolf) {
            if (isAtTameLimit(ev.getTamer())) {
                sendWolfTameLimitExceededNotificationTo(ev.getTamer());
                ev.setCanceled(true);
                return;
            }
            IncWolfCount(ev.getTamer(), (Wolf) ev.getAnimal());
            var w = (Wolf)ev.getAnimal();
            LivingEntity owner = ev.getTamer();
            if (w.hasEffect(MobEffects.WITHER)) {
                w.removeAllEffects();
                var s = Component.literal("[" + owner.getName().getString() + "] Wolf Saved! Congrats! ");
                s.setStyle(Style.EMPTY
                    .withBold(true)
                    .withColor(TextColor.fromRgb(0x33ccff))
                );
                owner.sendSystemMessage(s);
            }

            
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            int cw = ws.getWolfCountFor(owner); 
            var s = Component.literal("[" + owner.getName().getString() + "] New Wolf! Current wolf count: ");
            var s1 = Component.literal(Integer.toString(cw));
            s.setStyle(Style.EMPTY);
            s1.setStyle(Style.EMPTY
                .withBold(true)
                .withColor(TextColor.fromRgb(0x33ccff))
            );
            s.append(s1);
            owner.sendSystemMessage(s);
        }
    }

    private static boolean isAtTameLimit(LivingEntity owner) {
        if (!ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.WOLF_TAME_LIMIT)) return false;
        WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
        int tc = ws.getWolfCountFor(owner);
        if (tc >= ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.WOLF_TAME_LIMIT_VALUE)) {
            return true;
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWolfBreed(BabyEntitySpawnEvent ev) {
        if (ev.getChild().level.isClientSide) return;
        if (!(ev.getChild() instanceof Wolf)) {
            return;
        }
        var w = (Wolf) ev.getChild();

        if (isAtWolfBreedLimit(w.getOwner())) {
            sendWolfTameLimitExceededNotificationTo(w.getOwner());
            ev.setCanceled(true);
            return;
        }

        incWolfBreedCount(w.getOwner(), w);
    }

    private static boolean isAtWolfBreedLimit(LivingEntity owner) {
        if (ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.CAN_WOLF_BREED_OVER_LIMIT)) return false;
        return isAtTameLimit(owner);
    }

    
    private static void sendWolfTameLimitExceededNotificationTo(LivingEntity e) {
        var s = Component.literal("Wolf taming limit exceeded!");
        s.setStyle(Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(0xe00000))
        );
        e.sendSystemMessage(s);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWolfDeath(LivingDeathEvent ev) {
        var e = ev.getEntity();
        if (!(e instanceof Wolf)) return; 
        if (e.level.isClientSide) return;
        
        var w = (Wolf) e;
        if (!w.isTame() && !e.hasEffect(MobEffects.GLOWING) 
            && !ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.SAVE_WILD_WOLF)) {
            ev.setCanceled(true);
            w.setHealth(1.0f);
            //Give the wolf a second chance which last 3 or 5 minutes and notify the player 
            boolean five = w.getRandom().nextInt() % 2 == 0;
            int min = five ? 6000 : 3600;
            w.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,min, 255, false, false, false));
            w.addEffect(new MobEffectInstance(MobEffects.GLOWING, min+200, 1, false, false, false));
            w.addEffect(new MobEffectInstance(MobEffects.WITHER, min+200, 2, false, false, false));

            var txt = Component.literal("A Wolf is injured !!! ");
            txt.setStyle(
                Style.EMPTY
                    .withBold(true)
                    .withColor(TextColor.fromRgb(0xffe00000))
            );
            
            var hovertxt = Component.literal("[Show Cause] ");
            var deathtxt = ev.getSource().getLocalizedDeathMessage(w);
            hovertxt.setStyle(
                Style.EMPTY
                    .withBold(false)
                    .withColor(TextColor.fromRgb(0xffffcc00))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, deathtxt))
            );
            txt.append(hovertxt);

            var postxt = Component.literal("[To The Rescue!!!]");
            var post = Component.literal("[ "+ (five? "5": "3") + "min " + w.getBlockX() + " , " + w.getBlockY() + " , " + w.getBlockZ() + " ]");
            postxt.setStyle(
                Style.EMPTY
                    .withBold(false)
                    .withColor(TextColor.fromRgb(0xff8e8aff))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, post))
            );
            txt.append(postxt);

            var p = w.level.players();
            for (var i : p) {
                i.sendSystemMessage(txt);
            }
        } else {
            IncWolfDeathCount(w);
            var txt = Component.literal("A Wolf Died !!! ");
            txt.setStyle(
                Style.EMPTY
                    .withBold(true)
                    .withColor(TextColor.fromRgb(0xffe00000))
            );
            
            var hovertxt = Component.literal("[Show Cause] ");
            var deathtxt = ev.getSource().getLocalizedDeathMessage(w);
            hovertxt.setStyle(
                Style.EMPTY
                    .withBold(false)
                    .withColor(TextColor.fromRgb(0xffffcc00))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, deathtxt))
            );
            txt.append(hovertxt);
    
            var postxt = Component.literal("[Show Pos]");
            var post = Component.literal("[ " + w.getBlockX() + " , " + w.getBlockY() + " , " + w.getBlockZ() + " ]");
            postxt.setStyle(
                Style.EMPTY
                    .withBold(false)
                    .withColor(TextColor.fromRgb(0xff8e8aff))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, post))
            );
            txt.append(postxt);
    
            var p = w.level.players();
            for (var i : p) {
                i.sendSystemMessage(txt);
            }
        }
            
    }

    private static void IncWolfCount(LivingEntity owner, Wolf w) {
        if (!owner.level.isClientSide) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            ws.incWolfCountFor(owner);
        }
    }

    private static void incWolfBreedCount(LivingEntity owner, Wolf w) {
        if (!owner.level.isClientSide) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            ws.incWolfBreedCountFor(owner);
        }
    }

    private static void IncWolfDeathCount(Wolf w) {
        if (!w.level.isClientSide) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(w.level);
            ws.incWolfDeath(w);
        }
    }

    @SubscribeEvent
    public void onWolfRightClick(PlayerInteractEvent.EntityInteract ev ) {
        if (ev.getSide().isClient()) return;
        if (ev.getTarget() instanceof Wolf) {
            LivingEntity owner = ev.getEntity();
            if (!owner.isShiftKeyDown()) return;
            if (checkAndShowTameCount(owner)) {
                ev.setCancellationResult(InteractionResult.SUCCESS);
                ev.setCanceled(true);
            }
            if (checkAndShowBreedCount(owner)) {
                ev.setCancellationResult(InteractionResult.SUCCESS);
                ev.setCanceled(true);
            }
            if (checkAndShowDeathCount(owner)) {
                ev.setCancellationResult(InteractionResult.SUCCESS);
                ev.setCanceled(true);
            }
        }
    }

    private static boolean checkAndShowTameCount(LivingEntity owner) {
        if (!ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.SHOW_WOLF_TAME_COUNT))
            return false;
        if (owner.getMainHandItem().getItem() == Items.BONE && owner.isShiftKeyDown()) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            int cw = ws.getWolfCountFor(owner); 
            var s = Component.literal("Number of wolves you have tamed : ");
            var s1 = Component.literal(Integer.toString(cw));
            s.setStyle(Style.EMPTY);
            s1.setStyle(Style.EMPTY
                .withBold(true)
                .withColor(TextColor.fromRgb(0x33ccff))
            );
            s.append(s1);
            owner.sendSystemMessage(s);

            return true;
            
            
        }
        return false;
    }

    private static boolean checkAndShowDeathCount(LivingEntity owner) {
        if (!ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.SHOW_WOLF_DEATH_COUNT))
            return false;
        if (owner.getMainHandItem().getItem() == Items.STONE_AXE && owner.isShiftKeyDown()) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            int x = ws.getWolfDeath(); 
            var s = Component.literal("Number of wolves have died : ");
            var s1 = Component.literal(Integer.toString(x));
            s.setStyle(Style.EMPTY);
            s1.setStyle(Style.EMPTY
                .withBold(true)
                .withColor(TextColor.fromRgb(0xe00000))
            );
            s.append(s1);
            owner.sendSystemMessage(s);
            
            return true;
        }
        return false;
    }

    private static boolean checkAndShowBreedCount(LivingEntity owner) {
        if (!ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.SHOW_WOLF_BREED_COUNT))
            return false;
        if (owner.getMainHandItem().getItem() == Items.DANDELION && owner.isShiftKeyDown()) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            int x = ws.getWolfBreedCountFor(owner); 
            var s = Component.literal("Number of wolves you have bred : ");
            var s1 = Component.literal(Integer.toString(x));
            s.setStyle(Style.EMPTY);
            s1.setStyle(Style.EMPTY
                .withBold(true)
                .withColor(TextColor.fromRgb(0xff00d4))
            );
            s.append(s1);
            owner.sendSystemMessage(s);
            return true;
        }
        return false;
    }
}
