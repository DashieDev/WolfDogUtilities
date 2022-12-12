package wolfdogutilities.othermods.doggytalents;

import com.electronwill.nightconfig.core.Config;

import doggytalents.DoggyItems;
import doggytalents.common.entity.Dog;
import doggytalents.common.item.TreatItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wolfdogutilities.config.ConfigHandler;
import wolfdogutilities.storage.WolfTameCountStorage;

public class DoggyTalentsEventHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGH) //TODO : ??? Okay ??? Because the event where the wolf changes to a dog prioriy is NORMAL...
    public void onDogInteract(PlayerInteractEvent.EntityInteract ev ) {
        if (ev.getSide().isClient()) return;
        if (ev.getTarget() instanceof Wolf && ((Wolf)ev.getTarget()).getOwner() == ev.getEntity()) {
            if (ev.getEntity().getMainHandItem().getItem() instanceof TreatItem) {
                if (isAtDogTrainLimit(ev.getEntity())) {
                    sendDogTrainLimitExceededNotificationTo(ev.getEntity());
                    ev.setCancellationResult(InteractionResult.FAIL);
                    ev.setCanceled(true);
                }
                IncDogTrainCount(ev.getEntity());
            }
        } else if (ev.getTarget() instanceof Dog) {
            if (ev.getEntity().getMainHandItem().getItem() == Items.BONE) {
                sendDogTrainCountTo(ev.getEntity());
            }
        }
    }

    private static void sendDogTrainCountTo(LivingEntity e) {
        var ws = WolfTameCountStorage.get(e.level);
        int cw = ws.getDogTrainCountFor(e); 
        
        var s = Component.literal("Number of wolves you have trained : ");
        var s1 = Component.literal(Integer.toString(cw));
        s.setStyle(Style.EMPTY);
        s1.setStyle(Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(0xff6f00))
        );
        s.append(s1);
        e.sendSystemMessage(s);
    }
    
    private static void sendDogTrainLimitExceededNotificationTo(LivingEntity e) {
        var s = Component.literal("Dog training limit exceeded!");
        s.setStyle(Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(0xe00000))
        );
        e.sendSystemMessage(s);
    }

    private static void IncDogTrainCount(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
            ws.incDogTrainCountFor(owner);
        }
    }

    private static boolean isAtDogTrainLimit(LivingEntity owner) {
        if (!ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.DOG_TRAIN_LIMIT)) return false;
        WolfTameCountStorage ws = WolfTameCountStorage.get(owner.level);
        int tc = ws.getDogTrainCountFor(owner);
        if (tc >= ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.DOG_TRAIN_LIMIT_VALUE)) {
            return true;
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onDogBreed(BabyEntitySpawnEvent ev) {
        if (ev.getChild().level.isClientSide) return;
        if (!(ev.getChild() instanceof Dog)) {
            return;
        }
        var w = (Dog) ev.getChild();

        if (isAtDogBreedLimit(w.getOwner())) {
            ev.setCanceled(true);
            return;
        }

        IncDogTrainCount(w.getOwner());
    }

    private static boolean isAtDogBreedLimit(LivingEntity owner) {
        if (ConfigHandler.ServerConfig.getConfig(ConfigHandler.SERVER.CAN_DOG_BREED_OVER_LIMIT)) return false;
        return isAtDogTrainLimit(owner);
    }

    public static void registerSelf() {
        MinecraftForge.EVENT_BUS.register(new DoggyTalentsEventHandler());
    }
}
