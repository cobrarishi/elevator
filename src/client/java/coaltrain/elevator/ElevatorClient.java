package coaltrain.elevator;

import coaltrain.elevator.block.entity.ModBlockEntities;
import coaltrain.elevator.client.render.ElevatorDoorBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ElevatorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.ELEVATOR_DOOR_BLOCK_ENTITY,
                ElevatorDoorBlockEntityRenderer::new);
    }
}