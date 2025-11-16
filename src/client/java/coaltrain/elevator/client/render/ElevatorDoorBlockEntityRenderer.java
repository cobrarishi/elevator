package coaltrain.elevator.client.render;

import coaltrain.elevator.block.entity.ElevatorDoorBlockEntity;
import coaltrain.elevator.client.model.ElevatorDoorBlockModel;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ElevatorDoorBlockEntityRenderer extends GeoBlockRenderer<ElevatorDoorBlockEntity> {
    public ElevatorDoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new ElevatorDoorBlockModel());
    }
}