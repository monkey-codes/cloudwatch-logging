package codes.monkey.logging

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.DeferredProcessingAware
import ch.qos.logback.core.spi.PropertyDefiner
import net.logstash.logback.composite.CompositeJsonFormatter
import net.logstash.logback.composite.FormattedTimestampJsonProvider
import net.logstash.logback.encoder.LogstashAccessEncoder
import net.logstash.logback.encoder.LogstashEncoder

public class TimestampFormatPropertyDefiner extends ContextAwareBase implements PropertyDefiner {

    public static final CLOUDWATCH_FORMAT = "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ"

    @Override
    String getPropertyValue() {
        return CLOUDWATCH_FORMAT
    }

    public static <T extends DeferredProcessingAware> CompositeJsonFormatter<T> changeTimestampFormat(CompositeJsonFormatter<T> formatter) {
        def find = formatter.providers.providers.find {
            it instanceof FormattedTimestampJsonProvider
        }
        find.setPattern(CLOUDWATCH_FORMAT)
        formatter
    }

    public static class CloudwatchAccessLogstashEncoder extends LogstashAccessEncoder {

        @Override
        protected CompositeJsonFormatter<IAccessEvent> createFormatter() {
            changeTimestampFormat(super.createFormatter())
        }
    }

    public static class CloudwatchLogstashEncoder extends LogstashEncoder {

        @Override
        protected CompositeJsonFormatter<ILoggingEvent> createFormatter() {
            changeTimestampFormat(super.createFormatter())
        }
    }


}
