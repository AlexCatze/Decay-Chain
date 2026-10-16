import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Turns a LaunchWrapper class dump (-Dlegacy.debugClassLoadingSave=true, CLASSLOADER_TEMP) into mc-srg-dump.jar, a
 * compile-only jar of Minecraft/Forge classes with SRG names:
 * - inner class entries get their real simple names (the deobfuscation keeps the obfuscated ones, e.g. Biome$c);
 * - interfaces, fields and methods that other mods' mixins added (types outside Minecraft/Forge/libraries) are dropped;
 * - method bodies are dropped.
 * Usage: java -cp asm-9.x.jar;. StripDump <CLASSLOADER_TEMP dir> <out.jar>
 */
public class StripDump {
    private static final String[] KEEP = {"net/minecraft/", "net/minecraftforge/", "java/", "javax/", "com/mojang/",
            "io/netty/", "com/google/", "org/apache/", "it/unimi/", "org/lwjgl/", "joptsimple/", "paulscode/", "oshi/",
            "com/sun/", "sun/", "org/objectweb/", "gnu/trove/"};

    public static void main(String[] args) throws IOException {
        Path root = Paths.get(args[0]);
        List<Path> classes;
        try (Stream<Path> walk = Files.walk(root)) {
            classes = walk.filter(p -> p.toString().endsWith(".class")).filter(p -> {
                String rel = root.relativize(p).toString().replace('\\', '/');
                return rel.startsWith("net/minecraft/") || rel.startsWith("net/minecraftforge/");
            }).collect(Collectors.toList());
        }
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(Paths.get(args[1])))) {
            for (Path path : classes) {
                byte[] bytes = Files.readAllBytes(path);
                if (bytes.length == 0) continue;   // classes the dump could not write
                ClassReader reader = new ClassReader(bytes);
                ClassWriter writer = new ClassWriter(0);
                reader.accept(new Strip(writer), ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);
                out.putNextEntry(new JarEntry(root.relativize(path).toString().replace('\\', '/')));
                out.write(writer.toByteArray());
                out.closeEntry();
            }
        }
        System.out.println("wrote " + classes.size() + " classes to " + args[1]);
    }

    static boolean foreign(String text) {
        if (text == null) return false;
        int i = 0;
        while ((i = text.indexOf('L', i)) >= 0) {
            int end = text.indexOf(';', i);
            int lt = text.indexOf('<', i);
            if (end < 0) break;
            if (lt >= 0 && lt < end) end = lt;
            String name = text.substring(i + 1, end);
            if (name.contains("/") && !kept(name)) return true;
            i = end;
        }
        return false;
    }

    static boolean kept(String internalName) {
        for (String prefix : KEEP) if (internalName.startsWith(prefix)) return true;
        return false;
    }

    static final class Strip extends ClassVisitor {
        Strip(ClassVisitor next) {
            super(Opcodes.ASM9, next);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            String[] keep = Stream.of(interfaces).filter(StripDump::kept).toArray(String[]::new);
            super.visit(version, access, name, keep.length == interfaces.length ? signature : null, superName, keep);
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            if (innerName != null && outerName != null && name.startsWith(outerName + "$")) {
                innerName = name.substring(outerName.length() + 1);
            }
            super.visitInnerClass(name, outerName, innerName, access);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return foreign(descriptor) ? null : super.visitAnnotation(descriptor, visible);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (foreign(descriptor) || foreign(signature)) return null;
            return super.visitField(access, name, descriptor, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            if (foreign(descriptor) || foreign(signature)) return null;
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
