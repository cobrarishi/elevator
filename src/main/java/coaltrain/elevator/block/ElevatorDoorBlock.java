package coaltrain.elevator.block;

import coaltrain.elevator.block.entity.ElevatorDoorBlockEntity;
import coaltrain.elevator.block.entity.ModBlockEntities;
import coaltrain.elevator.item.ModItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class ElevatorDoorBlock extends BlockWithEntity {
    public static final MapCodec<ElevatorDoorBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    DoorPart.CODEC.fieldOf("door_part").forGetter(block -> block.doorPart),
                    createSettingsCodec()
            ).apply(instance, ElevatorDoorBlock::new)
    );
    public static final EnumProperty<DoorPart> PART = EnumProperty.of("part", DoorPart.class);
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<VerticalHalf> HALF = EnumProperty.of("half", VerticalHalf.class);

    private final DoorPart doorPart;

    private static final VoxelShape CLOSED_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public ElevatorDoorBlock(DoorPart part, Settings settings) {
        super(settings);
        this.doorPart = part;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(PART, part)
                .with(FACING, Direction.NORTH)
                .with(HALF, VerticalHalf.BOTTOM)
                .with(OPEN, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PART, OPEN, FACING, HALF);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        Direction facing = ctx.getHorizontalPlayerFacing();

        if (pos.getY() >= world.getHeight() - 1) {
            return null;
        }

        BlockPos topPos = pos.up();
        BlockPos otherBottom = getOppositeBottomPos(pos, facing, doorPart);
        BlockPos otherTop = otherBottom.up();

        if (!world.getBlockState(topPos).canReplace(ctx)) {
            return null;
        }
        if (!world.getBlockState(otherBottom).canReplace(ctx)) {
            return null;
        }
        if (!world.getBlockState(otherTop).canReplace(ctx)) {
            return null;
        }

        return this.getStateManager().getDefaultState()
                .with(PART, doorPart)
                .with(FACING, facing)
                .with(HALF, VerticalHalf.BOTTOM)
                .with(OPEN, false);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient || state.get(HALF) != VerticalHalf.BOTTOM || doorPart != DoorPart.LEFT) {
            return;
        }

        Direction facing = state.get(FACING);
        BlockPos otherBottomPos = getOppositeBottomPos(pos, facing, doorPart);
        BlockPos topPos = pos.up();
        BlockPos otherTopPos = otherBottomPos.up();

        world.setBlockState(topPos, state.with(HALF, VerticalHalf.TOP), Block.NOTIFY_ALL);

        Block otherBlock = doorPart == DoorPart.LEFT ? ModBlocks.ELEVATOR_DOOR_RIGHT : ModBlocks.ELEVATOR_DOOR_LEFT;
        DoorPart otherPart = doorPart == DoorPart.LEFT ? DoorPart.RIGHT : DoorPart.LEFT;
        BlockState otherState = otherBlock.getStateManager().getDefaultState()
                .with(PART, otherPart)
                .with(FACING, facing)
                .with(HALF, VerticalHalf.BOTTOM)
                .with(OPEN, false);

        world.setBlockState(otherBottomPos, otherState, Block.NOTIFY_ALL);
        world.setBlockState(otherTopPos, otherState.with(HALF, VerticalHalf.TOP), Block.NOTIFY_ALL);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            toggleDoor(world, pos, state);
        }
        return ActionResult.SUCCESS;
    }

    private void toggleDoor(World world, BlockPos pos, BlockState state) {
        boolean isOpen = state.get(OPEN);
        Direction facing = state.get(FACING);
        VerticalHalf half = state.get(HALF);
        DoorPart part = state.get(PART);

        BlockPos bottomPos = half == VerticalHalf.TOP ? pos.down() : pos;
        BlockPos topPos = half == VerticalHalf.BOTTOM ? pos.up() : pos;

        BlockPos otherBottomPos = part == DoorPart.LEFT
                ? bottomPos.offset(facing.rotateYClockwise())
                : bottomPos.offset(facing.rotateYCounterclockwise());
        BlockPos otherTopPos = otherBottomPos.up();

        boolean newState = !isOpen;

        setDoorOpen(world, bottomPos, newState);
        setDoorOpen(world, topPos, newState);
        setDoorOpen(world, otherBottomPos, newState);
        setDoorOpen(world, otherTopPos, newState);

        BlockPos basePos = getLeftBottomPos(bottomPos, facing, part);
        if (world.getBlockEntity(basePos) instanceof ElevatorDoorBlockEntity doorBlockEntity) {
            doorBlockEntity.setOpenState(newState);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            Direction facing = state.get(FACING);
            VerticalHalf half = state.get(HALF);
            DoorPart part = state.get(PART);

            BlockPos bottomPos = half == VerticalHalf.TOP ? pos.down() : pos;
            BlockPos topPos = half == VerticalHalf.BOTTOM ? pos.up() : pos;

            BlockPos otherBottomPos = part == DoorPart.LEFT
                    ? bottomPos.offset(facing.rotateYClockwise())
                    : bottomPos.offset(facing.rotateYCounterclockwise());
            BlockPos otherTopPos = otherBottomPos.up();

            dropStack(world, pos, new ItemStack(ModItems.ELEVATOR_DOOR));

            if (!pos.equals(bottomPos)) world.setBlockState(bottomPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            if (!pos.equals(topPos)) world.setBlockState(topPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            if (!pos.equals(otherBottomPos)) world.setBlockState(otherBottomPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            if (!pos.equals(otherTopPos)) world.setBlockState(otherTopPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, net.minecraft.util.math.random.Random random) {
        VerticalHalf half = state.get(HALF);

        if (direction.getAxis() == Direction.Axis.Y) {
            if ((direction == Direction.UP && half == VerticalHalf.BOTTOM) ||
                    (direction == Direction.DOWN && half == VerticalHalf.TOP)) {
                if (!(neighborState.getBlock() instanceof ElevatorDoorBlock)) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        if (direction.getAxis().isHorizontal()) {
            Direction facing = state.get(FACING);
            DoorPart part = state.get(PART);
            Direction expectedNeighbor = part == DoorPart.LEFT
                    ? facing.rotateYClockwise()
                    : facing.rotateYCounterclockwise();

            if (direction == expectedNeighbor && !(neighborState.getBlock() instanceof ElevatorDoorBlock)) {
                return Blocks.AIR.getDefaultState();
            }
        }

        return state;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        return aboveState.isReplaceable() || aboveState.isAir() ||
                aboveState.getBlock() instanceof ElevatorDoorBlock;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return hasBlockEntity(state) ? new ElevatorDoorBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (!hasBlockEntity(state)) {
            return null;
        }
        return validateTicker(type, ModBlockEntities.ELEVATOR_DOOR_BLOCK_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    public boolean hasBlockEntity(BlockState state) {
        return state.get(PART) == DoorPart.LEFT && state.get(HALF) == VerticalHalf.BOTTOM;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, WorldView world, BlockPos pos) {
        return state.get(OPEN) ? VoxelShapes.empty() : CLOSED_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, WorldView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? VoxelShapes.empty() : CLOSED_SHAPE;
    }

    private static BlockPos getOppositeBottomPos(BlockPos pos, Direction facing, DoorPart part) {
        return part == DoorPart.LEFT ? pos.offset(facing.rotateYClockwise()) : pos.offset(facing.rotateYCounterclockwise());
    }

    private static BlockPos getLeftBottomPos(BlockPos bottomPos, Direction facing, DoorPart part) {
        return part == DoorPart.LEFT ? bottomPos : bottomPos.offset(facing.rotateYCounterclockwise());
    }

    private static void setDoorOpen(World world, BlockPos pos, boolean open) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ElevatorDoorBlock) {
            world.setBlockState(pos, state.with(OPEN, open), Block.NOTIFY_LISTENERS);
        }
    }

    public enum DoorPart implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        public static final com.mojang.serialization.Codec<DoorPart> CODEC =
                StringIdentifiable.createCodec(DoorPart::values);

        private final String name;

        DoorPart(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }

    public enum VerticalHalf implements StringIdentifiable {
        BOTTOM("bottom"),
        TOP("top");

        private final String name;

        VerticalHalf(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}