package coaltrain.elevator.block;

import coaltrain.elevator.Elevator;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // Declare blocks without initialization
    public static Block ELEVATOR_DOOR_LEFT;
    public static Block ELEVATOR_DOOR_RIGHT;

    public static void register() {
        Elevator.LOGGER.info("Registering blocks for " + Elevator.MOD_ID);

        // Now create and register the blocks
        ELEVATOR_DOOR_LEFT = registerBlock("elevator_door_left",
                new ElevatorDoorBlock(
                        ElevatorDoorBlock.DoorPart.LEFT,
                        AbstractBlock.Settings.create()
                                .registryKey(RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Elevator.MOD_ID, "elevator_door_left")))
                                .mapColor(MapColor.IRON_GRAY)
                                .requiresTool()
                                .strength(5.0f, 6.0f)
                                .sounds(BlockSoundGroup.METAL)
                                .pistonBehavior(PistonBehavior.BLOCK)
                                .nonOpaque()));

        ELEVATOR_DOOR_RIGHT = registerBlock("elevator_door_right",
                new ElevatorDoorBlock(
                        ElevatorDoorBlock.DoorPart.RIGHT,
                        AbstractBlock.Settings.create()
                                .registryKey(RegistryKey.of(Registries.BLOCK.getKey(), Identifier.of(Elevator.MOD_ID, "elevator_door_right")))
                                .mapColor(MapColor.IRON_GRAY)
                                .requiresTool()
                                .strength(5.0f, 6.0f)
                                .sounds(BlockSoundGroup.METAL)
                                .pistonBehavior(PistonBehavior.BLOCK)
                                .nonOpaque()));
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Elevator.MOD_ID, name), block);
    }
}