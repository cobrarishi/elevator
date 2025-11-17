package coaltrain.elevator.block.entity;

import coaltrain.elevator.block.ElevatorDoorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ElevatorDoorBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final String OPEN_KEY = "Open";
    private static final RawAnimation OPEN_ANIMATION = RawAnimation.begin()
            .then("animation.elevator_door.open", LoopType.PLAY_ONCE);
    private static final RawAnimation CLOSE_ANIMATION = RawAnimation.begin()
            .then("animation.elevator_door.close", LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isOpen;
    private boolean animationInitialized;
    private boolean lastAnimatedState;

    public ElevatorDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEVATOR_DOOR_BLOCK_ENTITY, pos, state);
        this.isOpen = state.get(ElevatorDoorBlock.OPEN);
        this.lastAnimatedState = this.isOpen;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::handleAnimation));
    }

    private PlayState handleAnimation(AnimationState<ElevatorDoorBlockEntity> state) {
        AnimationController<ElevatorDoorBlockEntity> controller = state.getController();

        if (!animationInitialized) {
            controller.setAnimation(this.isOpen ? OPEN_ANIMATION : CLOSE_ANIMATION);
            animationInitialized = true;
            lastAnimatedState = this.isOpen;
        }

        if (lastAnimatedState != this.isOpen) {
            controller.forceAnimationReset();
            controller.setAnimation(this.isOpen ? OPEN_ANIMATION : CLOSE_ANIMATION);
            lastAnimatedState = this.isOpen;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setOpenState(boolean open) {
        if (this.isOpen != open) {
            this.isOpen = open;
            markDirty();
            sync();
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        boolean blockOpen = state.get(ElevatorDoorBlock.OPEN);
        if (blockOpen != this.isOpen) {
            this.isOpen = blockOpen;
            markDirty();
            sync();
        }
    }

    private void sync() {
        if (this.world != null && !this.world.isClient) {
            BlockState state = getCachedState();
            this.world.updateListeners(this.pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean(OPEN_KEY, this.isOpen);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.isOpen = nbt.getBoolean(OPEN_KEY);
        this.lastAnimatedState = this.isOpen;
        this.animationInitialized = false;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}