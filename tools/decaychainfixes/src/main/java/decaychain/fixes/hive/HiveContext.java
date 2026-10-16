package decaychain.fixes.hive;

import java.util.ArrayDeque;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Where the SRP code that is running right now happens. SRP asks for "the" evolution phase of a dimension; the context
 * mixins wrap SRP methods that know an entity or block position, so the phase (and any points earned) can be resolved
 * to the hive at that position. Client-side calls push an empty frame.
 */
public final class HiveContext {
    static final class Frame {
        final World world;
        final BlockPos pos;

        Frame(World world, BlockPos pos) {
            this.world = world;
            this.pos = pos;
        }
    }

    private static final Frame NONE = new Frame(null, null);
    private static final ThreadLocal<ArrayDeque<Frame>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private HiveContext() {
    }

    public static void push(World world, BlockPos pos) {
        STACK.get().push(world == null || pos == null || world.field_72995_K ? NONE : new Frame(world, pos));
    }

    public static void push(Entity entity) {
        if (entity == null) push(null, null);
        else push(entity.field_70170_p, entity.func_180425_c());
    }

    public static void push(TileEntity tile) {
        if (tile == null) push(null, null);
        else push(tile.func_145831_w(), tile.func_174877_v());
    }

    public static void pop() {
        ArrayDeque<Frame> stack = STACK.get();
        if (!stack.isEmpty()) stack.pop();
    }

    /** The innermost frame with a position, or null outside any context (or inside an empty frame). */
    static Frame current() {
        Frame top = STACK.get().peek();
        return top == null || top.world == null ? null : top;
    }
}
