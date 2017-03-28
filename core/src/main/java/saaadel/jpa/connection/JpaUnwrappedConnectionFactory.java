package saaadel.jpa.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import saaadel.jpa.connection.internal.VersionUtils;

import java.text.MessageFormat;

public enum JpaUnwrappedConnectionFactory {
    ;

    private static final Logger LOGGER = LogManager.getLogger(JpaUnwrappedConnectionFactory.class);

    public static JpaUnwrappedConnection getInstance(final ClassLoader classLoader) throws IllegalStateException {
        for (final JpaImplementation vendor : JpaImplementation.values()) {
            if (vendor.isLinkable(classLoader)) {
                final Class<?> aClass;
                try {
                    aClass = classLoader.loadClass(vendor.implementationClassName);
                } catch (ClassNotFoundException ex) {
                    LOGGER.debug("JpaUnwrappedConnection interface implementation not found: {}", vendor.implementationClassName);
                    LOGGER.debug("", ex);
                    // JPA implementation present, but not JpaUnwrappedConnection implementation
                    continue;
                }

                @SuppressWarnings("all") final Class<? extends JpaUnwrappedConnection> clazz = (Class<? extends JpaUnwrappedConnection>) aClass;
                try {
                    return clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new IllegalStateException(MessageFormat.format("Incorrect {0} implementation", JpaUnwrappedConnection.class.getName()), ex);
                }
            }
        }
        throw new IllegalStateException(MessageFormat.format("No available {0} implementation", JpaUnwrappedConnection.class.getName()));
    }

    public enum JpaImplementation {
        //TODO for: OpenJPA 2.0+, DataNucleus 2.1.0+

        EclipseLinkJpa2("saaadel.jpa.connection.impl.EclipseLinkJpa2UnwrappedConnection") {
            @Override
            public boolean isLinkable(final ClassLoader classLoader) {
                final VersionUtils.VersionParsed versionParsed = VersionUtils.getVersionMethodResultParsed(classLoader, "org.eclipse.persistence.Version", "getVersion");
                //EclipseLink 2.0+
                return versionParsed != null && versionParsed.ge(2, 0);
            }
        },
        HibernateJpa2("saaadel.jpa.connection.impl.HibernateJpa2UnwrappedConnection") {
            @Override
            public boolean isLinkable(final ClassLoader classLoader) {
                final VersionUtils.VersionParsed versionParsed = VersionUtils.getVersionMethodResultParsed(classLoader, "org.hibernate.Version", "getVersionString");
                // Hibernate EntityManager 3.5+
                return versionParsed != null && versionParsed.ge(3, 5);
            }
        },
        EclipseLinkJpa1("saaadel.jpa.connection.impl.EclipseLinkJpa1UnwrappedConnection") {
            @Override
            public boolean isLinkable(final ClassLoader classLoader) {
                final VersionUtils.VersionParsed versionParsed = VersionUtils.getVersionMethodResultParsed(classLoader, "org.eclipse.persistence.Version", "getVersion");
                //EclipseLink &lt; 2.0
                return versionParsed != null && versionParsed.lt(2, 0);
            }
        };

        private final String implementationClassName;

        JpaImplementation(String implementationClassName) {
            this.implementationClassName = implementationClassName;
        }

        public abstract boolean isLinkable(final ClassLoader classLoader);
    }
}
