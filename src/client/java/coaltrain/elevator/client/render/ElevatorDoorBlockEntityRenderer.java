package coaltrain.elevator.client.render;

import coaltrain.elevator.block.ElevatorDoorBlock;
import coaltrain.elevator.block.entity.ElevatorDoorBlockEntity;
import coaltrain.elevator.client.model.ElevatorDoorBlockModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ElevatorDoorBlockEntityRenderer extends GeoBlockRenderer<ElevatorDoorBlockEntity> {
    public ElevatorDoorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new ElevatorDoorBlockModel());
    }

    @Override
    public void render(ElevatorDoorBlockEntity animatable, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (animatable.getCachedState() == null) {
            return;
        }

        matrices.push();
        if (!(animatable.getCachedState().getBlock() instanceof ElevatorDoorBlock)) {
            matrices.pop();
            return;
        }

        if (animatable.getCachedState().get(ElevatorDoorBlock.PART) != ElevatorDoorBlock.DoorPart.LEFT
                || animatable.getCachedState().get(ElevatorDoorBlock.HALF) != ElevatorDoorBlock.VerticalHalf.BOTTOM) {
            matrices.pop();
            return;
        }

        matrices.translate(0.5, 0, 0.5);
        float rotation = animatable.getCachedState().get(ElevatorDoorBlock.FACING).asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
        matrices.translate(-0.5, 0, -0.5);

        super.render(animatable, tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }
}