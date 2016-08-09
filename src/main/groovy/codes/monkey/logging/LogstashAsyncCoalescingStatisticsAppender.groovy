package codes.monkey.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.spi.AppenderAttachableImpl
import org.perf4j.GroupedTimingStatistics
import org.perf4j.StopWatch
import org.perf4j.TimingStatistics
import org.perf4j.helpers.GenericAsyncCoalescingStatisticsAppender
import org.perf4j.logback.AsyncCoalescingStatisticsAppender

import java.lang.reflect.Field

import static net.logstash.logback.marker.Markers.appendEntries

class LogstashAsyncCoalescingStatisticsAppender extends AsyncCoalescingStatisticsAppender {

    private AppenderAttachableImpl<LoggingEvent> downstreamAppenders
    private Level logLevel
    public static final String PREFIX = 'perf4j'
    public static final String STDDEV = PREFIX + '_stddev'
    public static final String MEAN = PREFIX + '_mean'
    public static final String COUNT = PREFIX + '_count'
    public static final String MIN = PREFIX + '_min'
    public static final String MAX = PREFIX + '_max'
    public static final String TAG = PREFIX + '_tag'

    public LogstashAsyncCoalescingStatisticsAppender() {
        downstreamAppenders = (AppenderAttachableImpl<LoggingEvent>) getSuperPrivateField('downstreamAppenders', this)
        logLevel = (Level) getSuperPrivateField('downstreamLogLevel', this)
    }

    private static Object getSuperPrivateField(String fieldName, Object target) {
        try {
            Field field = LogstashAsyncCoalescingStatisticsAppender.class.
                    superclass.getDeclaredField(fieldName)
            field.setAccessible(true)
            return field.get(target)
        } catch (NoSuchFieldException e) {
            e.printStackTrace()
        } catch (IllegalAccessException e) {
            e.printStackTrace()
        }
        null
    }


    @Override
    protected GenericAsyncCoalescingStatisticsAppender newGenericAsyncCoalescingStatisticsAppender() {

        return new GenericAsyncCoalescingStatisticsAppender() {
            @Override
            public void start(GenericAsyncCoalescingStatisticsAppender.GroupedTimingStatisticsHandler throwMeAway) {
                super.start(
                        new GenericAsyncCoalescingStatisticsAppender.GroupedTimingStatisticsHandler() {

                            public void handle(GroupedTimingStatistics statistics) {
                                try {
                                    synchronized (downstreamAppenders) {

                                        statistics.getStatisticsByTag().entrySet().each { entry ->
                                            LoggingEvent coalescedLoggingEvent =
                                                    new LoggingEvent(Logger.class.getName(),
                                                            ((LoggerContext) getContext()).getLogger(StopWatch.DEFAULT_LOGGER_NAME),
                                                            logLevel,
                                                            entry.toString(),
                                                            null, null)
                                            TimingStatistics timingStatistics = entry.getValue()

                                            Map<String, Object> entries = [
                                                    (TAG)   : entry.key,
                                                    (MAX)   : timingStatistics.max,
                                                    (MIN)   : timingStatistics.min,
                                                    (COUNT) : timingStatistics.count,
                                                    (MEAN)  : timingStatistics.mean,
                                                    (STDDEV): timingStatistics.standardDeviation

                                            ]
                                            coalescedLoggingEvent.setMarker(appendEntries(entries))
                                            downstreamAppenders.appendLoopOnAppenders(coalescedLoggingEvent)
                                        }

                                    }
                                } catch (Exception e) {
                                    addError('Exception calling append with GroupedTimingStatistics on downstream appender',
                                            e)
                                }
                            }

                            public void error(String errorMessage) {
                                addError(errorMessage)
                            }
                        })
            }
        }
    }


}
