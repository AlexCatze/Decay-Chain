package decaychain.fixes.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Client command /panorama: captures the six faces of a main-menu panorama (panorama_0..5.png, the order the title
 * screen uses: front, right, back, left, up, down) from where the player stands, into screenshots/panorama/.
 * Each face is rendered at a 90 degree field of view without HUD or hand and cropped to the centre square.
 * Stand still and do not touch the mouse while it runs (about two seconds).
 */
public final class PanoramaCapture extends CommandBase {
    private static final int SETTLE_FRAMES = 12;   // frames to wait after turning, so the view is fully drawn
    private static final float[][] FACES = {{0, 0}, {90, 0}, {180, 0}, {-90, 0}, {0, -90}, {0, 90}};   // yaw, pitch offsets

    private static int face = -1;
    private static int wait;
    private static float baseYaw;
    private static float savedYaw, savedPitch, savedFov;
    private static boolean savedHideGui, savedBobbing;
    private static int savedThirdPerson;

    public static void register() {
        ClientCommandHandler.instance.func_71560_a(new PanoramaCapture());
        MinecraftForge.EVENT_BUS.register(PanoramaCapture.class);
    }

    @Override
    public String func_71517_b() {
        return "panorama";
    }

    @Override
    public String func_71518_a(ICommandSender sender) {
        return "/panorama - capture a main menu panorama from here (stand still)";
    }

    @Override
    public int func_82362_a() {
        return 0;
    }

    @Override
    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.func_71410_x();
        EntityPlayerSP player = mc.field_71439_g;
        if (player == null || face >= 0) return;
        GameSettings settings = mc.field_71474_y;
        savedYaw = player.field_70177_z;
        savedPitch = player.field_70125_A;
        savedFov = settings.field_74334_X;
        savedHideGui = settings.field_74319_N;
        savedBobbing = settings.field_74336_f;
        savedThirdPerson = settings.field_74320_O;
        baseYaw = Math.round(player.field_70177_z / 90f) * 90f;   // square to the world axes
        settings.field_74334_X = 90f;
        settings.field_74319_N = true;
        settings.field_74336_f = false;
        settings.field_74320_O = 0;
        mc.func_193033_an().func_191788_b();                       // no toasts in the shots
        face = 0;
        wait = SETTLE_FRAMES;
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (face < 0) return;
        Minecraft mc = Minecraft.func_71410_x();
        EntityPlayerSP player = mc.field_71439_g;
        if (player == null) {
            face = -1;
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            aim(player, baseYaw + FACES[face][0], FACES[face][1]);
            return;
        }
        if (--wait > 0) return;
        save(mc, face);
        if (++face < FACES.length) {
            wait = SETTLE_FRAMES;
            return;
        }
        face = -1;
        GameSettings settings = mc.field_71474_y;
        settings.field_74334_X = savedFov;
        settings.field_74319_N = savedHideGui;
        settings.field_74336_f = savedBobbing;
        settings.field_74320_O = savedThirdPerson;
        aim(player, savedYaw, savedPitch);
        player.func_145747_a(new TextComponentString("Panorama saved to screenshots/panorama/ (panorama_0..5.png)"));
    }

    private static void aim(EntityPlayerSP player, float yaw, float pitch) {
        player.field_70177_z = player.field_70126_B = yaw;
        player.field_70125_A = player.field_70127_C = pitch;
        player.field_70759_as = player.field_70758_at = yaw;
        player.field_70761_aq = player.field_70760_ar = yaw;
    }

    private static void save(Minecraft mc, int index) {
        BufferedImage full = ScreenShotHelper.func_186719_a(mc.field_71443_c, mc.field_71440_d, mc.func_147110_a());
        int side = Math.min(full.getWidth(), full.getHeight());
        BufferedImage square = full.getSubimage((full.getWidth() - side) / 2, (full.getHeight() - side) / 2, side, side);
        File file = new File(mc.field_71412_D, "screenshots/panorama/panorama_" + index + ".png");
        try {
            file.getParentFile().mkdirs();
            ImageIO.write(square, "png", file);
        } catch (IOException e) {
            mc.field_71439_g.func_145747_a(new TextComponentString("Could not save " + file + ": " + e));
        }
    }
}
