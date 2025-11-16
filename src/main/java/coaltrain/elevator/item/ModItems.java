package coaltrain.elevator.item;

import coaltrain.elevator.Elevator;
import coaltrain.elevator.block.ModBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item ELEVATOR_DOOR = registerItem(
            new BlockItem(ModBlocks.ELEVATOR_DOOR_LEFT, new Item.Settings()));

    private static Item registerItem(Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Elevator.MOD_ID, "elevator_door"), item);
    }

    public static void register() {
        Elevator.LOGGER.info("Registering items for " + Elevator.MOD_ID);
    }
}