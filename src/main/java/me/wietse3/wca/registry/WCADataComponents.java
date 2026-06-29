package me.wietse3.wca.registry;

import me.wietse3.wca.WietseCreateAdditions;
import me.wietse3.wca.content.brass_link.controller.BrassControllerBind;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.UnaryOperator;

public class WCADataComponents {
    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, WietseCreateAdditions.MODID);

    public static final DataComponentType<List<BrassControllerBind>> BRASS_LINKED_CONTROLLER_LINKS = register(
            "brass_linked_controller_links", builder -> builder
                    .persistent(BrassControllerBind.CODEC.listOf())
                    .networkSynchronized(ByteBufCodecs.fromCodec(BrassControllerBind.CODEC.listOf())));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
