package codes.monkey.logging

import ch.qos.logback.core.spi.ContextAwareBase
import ch.qos.logback.core.spi.PropertyDefiner

class BeanStalkPropertyDefiner extends ContextAwareBase implements PropertyDefiner {

    String field

    private static final int TIMEOUT = 500
    private static String DEFAULT_VALUE = 'N/A'

    @Override
    String getPropertyValue() {

        try {
            return new URL("${getBeanstalkMetaBaseUrl()}/$field").getText(
                    connectTimeout: TIMEOUT,
                    readTimeout: TIMEOUT
            )?.replaceAll('\\s','')
        } catch (IOException e){
            return DEFAULT_VALUE
        }

    }

    private String getBeanstalkMetaBaseUrl() {
        System.getProperty("instance.meta.baseUrl") ?: 'http://169.254.169.254/latest/meta-data'
    }

}
