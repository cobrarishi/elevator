package coaltrain.elevator.client.model;

import coaltrain.elevator.Elevator;
import coaltrain.elevator.block.entity.ElevatorDoorBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ElevatorDoorBlockModel extends GeoModel<ElevatorDoorBlockEntity> {
    @Override
    public Identifier getModelResource(ElevatorDoorBlockEntity animatable) {
        return Identifier.of(Elevator.MOD_ID, "geo/elevator_door.geo.json");
    }

    @Override
    public Identifier getTextureResource(ElevatorDoorBlockEntity animatable) {
        return Identifier.of("minecraft", "textures/block/iron_block.png");
    }

    @Override
    public Identifier getAnimationResource(ElevatorDoorBlockEntity animatable) {
        return Identifier.of(Elevator.MOD_ID, "animations/elevator_door.animation.json");
    }
}