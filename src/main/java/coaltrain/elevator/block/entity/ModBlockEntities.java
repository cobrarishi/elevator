package coaltrain.elevator.block.entity;

import coaltrain.elevator.Elevator;
import coaltrain.elevator.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<ElevatorDoorBlockEntity> ELEVATOR_DOOR_BLOCK_ENTITY;

    public static void register() {
        Elevator.LOGGER.info("Registering Block Entities for " + Elevator.MOD_ID);

        ELEVATOR_DOOR_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Elevator.MOD_ID, "elevator_door_block_entity"),
                FabricBlockEntityTypeBuilder.create(ElevatorDoorBlockEntity::new,
                        ModBlocks.ELEVATOR_DOOR_LEFT,
                        ModBlocks.ELEVATOR_DOOR_RIGHT).build()
        );
    }
}