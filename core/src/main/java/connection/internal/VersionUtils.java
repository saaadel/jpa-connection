package saaadel.jpa.connection.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionUtils {
    private static Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(?:\\.(\\d+))?.*$\n");

    private VersionUtils() {
    }

    public static VersionParsed getVersionMethodResultParsed(final ClassLoader classLoader, final String versionClassName, final String versionMethodName) {
        return getVersionMethodResultParsed(classLoader, versionClassName, versionMethodName, null);
    }

    public static VersionParsed getVersionMethodResultParsed(final ClassLoader classLoader, final String versionClassName, final String versionMethodName, Function<String, String> versionStringMapperFunction) {
        try {
            try {
                final Class<?> versionClazz = classLoader.loadClass(versionClassName);
                final Method versionMethod = versionClazz.getMethod(versionMethodName);
                final Object versionMethodResult;
                try {
                    versionMethodResult = versionMethod.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    return null;
                }

                return versionMethodResult == null ? null : parseVersion("" + versionMethodResult);

            } catch (NoSuchMethodException e) {
                return null;
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static VersionParsed getVersionFieldParsed(final ClassLoader classLoader, final String versionClassName, final String versionFieldName) {
        return getVersionFieldParsed(classLoader, versionClassName, versionFieldName, null);
    }

    public static VersionParsed getVersionFieldParsed(final ClassLoader classLoader, final String versionClassName, final String versionFieldName, Function<String, String> versionStringMapperFunction) {
        try {
            final Class<?> versionClass = classLoader.loadClass(versionClassName);
            final Field versionField;
            try {
                versionField = versionClass.getField(versionFieldName);
            } catch (NoSuchFieldException e) {
                return null;
            }

            final Object versionFieldValue;
            try {
                versionFieldValue = versionField.get(Modifier.isStatic(versionField.getModifiers()) ? null : versionClass.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                return null;
            }

            return versionFieldValue == null ? null : parseVersion("" + versionFieldValue);

        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static VersionParsed parseVersion(final String versionString) {
        final Matcher versionMatcher = VERSION_PATTERN.matcher(versionString);
        if (versionMatcher.matches()) {
            final Long major = Long.valueOf(versionMatcher.group(1), 10);
            Long minor = null;
            try {
                minor = Long.valueOf(versionMatcher.group(1), 10);
            } catch (NumberFormatException ignored) {
            }
            return new VersionParsed(major, minor);
        }
        return null;
    }

    public static final class VersionParsed {
        public final long major;
        public final Long minor;

        private VersionParsed(Long major, Long minor) {
            this.major = major;
            this.minor = minor;
        }

        public boolean after(long major, long minor) {
            return after(major, minor, true);
        }

        public boolean after(long major, long minor, boolean minorNullAsZero) {
            return this.major > major || (this.major == major && (this.minor != null ? this.minor > minor : !minorNullAsZero || 0 > minor));
        }

        public boolean afterOrEquals(long major, long minor) {
            return afterOrEquals(major, minor, true);
        }

        public boolean afterOrEquals(long major, long minor, boolean minorNullAsZero) {
            return this.major > major || (this.major == major && (this.minor != null ? this.minor >= minor : !minorNullAsZero || 0 >= minor));
        }
    }


}
