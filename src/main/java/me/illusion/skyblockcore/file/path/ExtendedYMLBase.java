package me.illusion.skyblockcore.file.path;

import me.illusion.utilities.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ExtendedYMLBase extends YMLBase {

    public ExtendedYMLBase(JavaPlugin plugin, String name) {
        super(plugin, name);
    }

    public ExtendedYMLBase(JavaPlugin plugin, File file, boolean existsOnSource) {
        super(plugin, file, existsOnSource);
    }

    protected <T> T load(T t, ConfigurationSection section) {
        Class<?> clazz = t.getClass();

        for(Field field : clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(TargetPath.class))
                continue;

            TargetPath target = field.getAnnotation(TargetPath.class);
            String path = target.path();

            if(path.equalsIgnoreCase(""))
                path = field.getName();

            Class<?> targetClass = field.getType();

            boolean accessible = field.isAccessible();

            if(!accessible)
                field.setAccessible(true);

            try {
                Object cast = section.get(path);

                if(cast == null)
                    continue;

                Class<?> primitive = PrimitiveUnboxer.unbox(cast.getClass());

                if(primitive.isPrimitive() && primitive == targetClass) {
                    cast = cast.getClass().getDeclaredMethod(primitive.getName() + "Value").invoke(cast);
                }

                field.set(t, cast);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return t;
    }
}