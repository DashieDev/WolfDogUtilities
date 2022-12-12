package wolfdogutilities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import wolfdogutilities.commands.WolfCountGetCommand;
import wolfdogutilities.config.ConfigHandler;
import wolfdogutilities.dogconstants.DogConstants;
import wolfdogutilities.listeners.WolfDogListener;
import wolfdogutilities.othermods.doggytalents.DoggyTalentsEventHandler;

@Mod(DogConstants.MODID)
public class WolfDogUtilities {
    public WolfDogUtilities() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::interModProcess);

        MinecraftForge.EVENT_BUS.register(new WolfDogListener());
        ChopinLogger.l("Hello there !");
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        ModLoadingContext.get().registerExtensionPoint( DisplayTest.class,
              () -> new IExtensionPoint.DisplayTest(
                      () -> NetworkConstants.IGNORESERVERONLY,
                      (remoteVersion, isFromServer) -> true
              )
      );
      ConfigHandler.init(modEventBus);
    }

    public void registerCommands(final RegisterCommandsEvent event) {
        WolfCountGetCommand.register(event.getDispatcher());
    }

    
    protected void interModProcess(final InterModProcessEvent event) {
        if (ModList.get().isLoaded("doggytalents")) {
            DoggyTalentsEventHandler.registerSelf();
        }
    }
}
