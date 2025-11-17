package coaltrain.elevator.item;

import coaltrain.elevator.Elevator;
import coaltrain.elevator.block.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item ELEVATOR_DOOR;

    public static void register() {
        Elevator.LOGGER.info("Registering items for " + Elevator.MOD_ID);

        ELEVATOR_DOOR = Registry.register(
                Registries.ITEM,
                Identifier.of(Elevator.MOD_ID, "elevator_door"),
                new BlockItem(ModBlocks.ELEVATOR_DOOR_LEFT, new Item.Settings()
                        .registryKey(RegistryKey.of(Registries.ITEM.getKey(), Identifier.of(Elevator.MOD_ID, "elevator_door"))))
        );
    }
}