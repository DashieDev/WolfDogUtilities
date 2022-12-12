package wolfdogutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.synchronization.brigadier.*;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.*;
import wolfdogutilities.storage.WolfTameCountStorage;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.*;

import java.util.UUID;

public class WolfCountGetCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register( literal("wolfcount")
            .requires(s -> s.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("owner", EntityArgument.entity())
                    .then(Commands.argument("count", IntegerArgumentType.integer())
                        .executes(c -> setCount(c) ))))
        );
    }

    public static int setCount(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity x = EntityArgument.getEntity(ctx, "owner");
        int count = ctx.getArgument("count", Integer.class).intValue(); 
        if (x instanceof LivingEntity) 
        WolfTameCountStorage.get(ctx.getSource().getLevel()).setWolfCountFor( (LivingEntity) x, count);

        var s = Component.literal("Set tame count for " + ctx.getSource().getEntity().getName().getString() + " to ");
        var s1 = Component.literal(Integer.toString(count));
        s.setStyle(Style.EMPTY);
        s1.setStyle(Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(0x33ccff))
        );
        s.append(s1);

        ctx.getSource().getEntity().sendSystemMessage(s);
        return 1;
    }
}
