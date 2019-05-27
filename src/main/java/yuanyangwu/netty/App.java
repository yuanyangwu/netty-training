package yuanyangwu.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ByteBuf byteBuf = Unpooled.wrappedBuffer("hello wo".getBytes());
        System.out.println(ByteBufUtil.prettyHexDump(byteBuf));
        System.out.println( "Hello World!" );
    }
}
