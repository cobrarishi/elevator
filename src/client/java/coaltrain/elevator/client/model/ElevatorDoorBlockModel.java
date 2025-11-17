package coaltrain.elevator.client.model;

import coaltrain.elevator.Elevator;
import coaltrain.elevator.block.entity.ElevatorDoorBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ElevatorDoorBlockModel extends GeoModel<ElevatorDoorBlockEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.of(Elevator.MOD_ID, "geo/elevator_door.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.of("minecraft", "textures/block/iron_block.png");
    }

    @Override
    public Identifier getAnimationResource(ElevatorDoorBlockEntity animatable) {
        return Identifier.of(Elevator.MOD_ID, "animations/elevator_door.animation.json");
    }
}