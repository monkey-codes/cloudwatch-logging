import ch.qos.logback.core.ConsoleAppender
import codes.monkey.logging.BeanStalkPropertyDefiner
import codes.monkey.logging.LoggingTagsPropertyDefiner
import net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender
import net.logstash.logback.stacktrace.ShortenedThrowableConverter
import codes.monkey.logging.TimestampFormatPropertyDefiner.CloudwatchLogstashEncoder
import groovy.json.JsonOutput



def tag = { name -> new LoggingTagsPropertyDefiner(property: name).getPropertyValue() }
def beanstalk = { name -> new BeanStalkPropertyDefiner(field: name).getPropertyValue() }

def loggingMarkers = JsonOutput.toJson([
        app_name   : tag('app.name'),
        app_version: tag('app.version'),
        eb_hostname: beanstalk('hostname'),
        eb_instance_id: beanstalk('instance-id'),
        eb_instance_type: beanstalk('instance-type'),
        eb_ip: beanstalk('local-ipv4')

])


appender('root', LoggingEventAsyncDisruptorAppender) {
    appender(ConsoleAppender) {
        encoder(CloudwatchLogstashEncoder) {
            customFields = loggingMarkers
            shortenedLoggerNameLength = 36
            throwableConverter(ShortenedThrowableConverter) {
                maxDepthPerThrowable = 30
                maxLength = 2048
                shortenedClassNameLength = 20
                excludes = ['sun\\.reflect\\..*\\.invoke.*',
                            'net\\.sf\\.cglib\\.proxy\\.MethodProxy\\.invoke']
                rootCauseFirst = true
            }
        }
    }
}

root(INFO, ['root'])