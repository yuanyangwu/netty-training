package yuanyangwu.netty.log;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.slf4j.LoggerFactory;

public class MyLoggerFactory extends InternalLoggerFactory {
    @Override
    protected InternalLogger newInstance(String name) {
        return null; //LoggerFactory.getLogger(name);
    }
}
