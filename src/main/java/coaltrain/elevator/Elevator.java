package coaltrain.elevator;

import coaltrain.elevator.block.ModBlocks;
import coaltrain.elevator.block.entity.ModBlockEntities;
import coaltrain.elevator.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elevator implements ModInitializer {
    public static final String MOD_ID = "elevator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Elevator Mod");

        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();

        // Add elevator door to functional blocks tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(ModItems.ELEVATOR_DOOR);
        });

        LOGGER.info("Elevator Mod initialized successfully!");
    }
}