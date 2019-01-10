package com.gempukku.secsy.gaming.editor.generic;

import com.gempukku.secsy.context.SystemContext;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.AbstractLifeCycleSystem;
import com.gempukku.secsy.entity.JavaPackageProvider;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(profiles = "editor", shared = TypeFieldEditors.class)
public class ReflectionsTypeFieldEditors extends AbstractLifeCycleSystem implements TypeFieldEditors {
    @Inject
    private JavaPackageProvider javaPackageProvider;
    @Inject
    private SystemContext systemContext;

    private Map<Class<?>, TypeFieldEditor> fieldEditorMap;

    @Override
    public void initialize() {
        fieldEditorMap = new HashMap<Class<?>, TypeFieldEditor>();
        Set<URL> contextLocations = new HashSet<URL>();
        for (String javaPackage : javaPackageProvider.getJavaPackages()) {
            contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
        }

        Configuration scanConfiguration = new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(contextLocations);

        Reflections reflections = new Reflections(scanConfiguration);
        Set<Class<? extends TypeFieldEditor>> typeFieldEditors = reflections.getSubTypesOf(TypeFieldEditor.class);
        for (Class<? extends TypeFieldEditor> typeFieldEditor : typeFieldEditors) {
            for (Type type : typeFieldEditor.getGenericInterfaces()) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Type rawType = parameterizedType.getRawType();
                    if (rawType == TypeFieldEditor.class) {
                        Type paramType = parameterizedType.getActualTypeArguments()[0];
                        if (paramType instanceof Class) {
                            try {
                                TypeFieldEditor fieldEditor = typeFieldEditor.newInstance();
                                systemContext.initializeObject(fieldEditor);
                                fieldEditorMap.put(getGeneralParamType((Class) paramType), fieldEditor);
                            } catch (InstantiationException e) {
                                throw new IllegalStateException("Unable to create converter of type " + typeFieldEditor.getSimpleName(), e);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException("Unable to create converter of type " + typeFieldEditor.getSimpleName(), e);
                            }
                        }
                    }
                }
            }
        }
    }

    private Class getGeneralParamType(Class paramType) {
        if (paramType == Float.class)
            return float.class;
        else if (paramType == Integer.class)
            return int.class;
        else if (paramType == Long.class)
            return long.class;
        else if (paramType == Boolean.class)
            return boolean.class;
        return paramType;
    }

    @Override
    public <T> TypeFieldEditor<T> getEditorForField(Class<T> clazz) {
        return fieldEditorMap.get(clazz);
    }
}
