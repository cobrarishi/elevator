package coaltrain.elevator.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import coaltrain.elevator.block.ElevatorDoorBlock;

public class ElevatorDoorBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isOpen = false;

    public ElevatorDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEVATOR_DOOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::animationController));
    }

    protected PlayState animationController(AnimationTest<ElevatorDoorBlockEntity> test) {
        if (isOpen) {
            return test.setAndContinue(RawAnimation.begin().thenLoop("animation.elevator_door.open"));
        } else {
            return test.setAndContinue(RawAnimation.begin().thenLoop("animation.elevator_door.closed"));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void toggleAnimation() {
        if (world != null && !world.isClient) {
            this.isOpen = !this.isOpen;
            markDirty();
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world != null && !world.isClient) {
            boolean stateOpen = state.get(ElevatorDoorBlock.OPEN);
            if (this.isOpen != stateOpen) {
                this.isOpen = stateOpen;
                markDirty();
            }
        }
    }
}