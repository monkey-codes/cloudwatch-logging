package codes.monkey.logging

import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.PropertyDefiner

class LoggingTagsPropertyDefiner extends ContextAwareBase implements PropertyDefiner {

    String property

    private static String DEFAULT_VALUE = 'N/A'

    @Lazy static Properties LOGGING_MARKERS = {
        Properties props = new Properties()
        props.load(LoggingTagsPropertyDefiner.class.
                classLoader.getResourceAsStream('logging-tags.properties'))
        props
    }()

    @Override
    String getPropertyValue() {
        LOGGING_MARKERS.get(property, DEFAULT_VALUE)
    }

}
