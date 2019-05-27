package yuanyangwu.netty.bytebuf;

import io.netty.buffer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.netty.util.ByteProcessor;
import org.junit.jupiter.api.Test;

public class ByteBufTest {
    public void testIndex(AbstractByteBufAllocator allocator) {
        ByteBuf buf = allocator.directBuffer(20, 100);
        assertEquals(20, buf.capacity());
        assertEquals(100, buf.maxCapacity());
        assertEquals(0, buf.readerIndex());
        assertEquals(0, buf.writerIndex());
        assertEquals(0, buf.readableBytes());
        assertEquals(20, buf.writableBytes());
        assertEquals(100, buf.maxWritableBytes());

        buf.writeByte(0x12);
        assertEquals(20, buf.capacity());
        assertEquals(100, buf.maxCapacity());
        assertEquals(0, buf.readerIndex());
        assertEquals(1, buf.writerIndex());
        assertEquals(1, buf.readableBytes());
        assertEquals(19, buf.writableBytes());
        assertEquals(99, buf.maxWritableBytes());

        buf.writeInt(0x3456789A);
        assertEquals(20, buf.capacity());
        assertEquals(100, buf.maxCapacity());
        assertEquals(0, buf.readerIndex());
        assertEquals(5, buf.writerIndex());
        assertEquals(5, buf.readableBytes());
        assertEquals(15, buf.writableBytes());
        assertEquals(95, buf.maxWritableBytes());

        byte b = buf.readByte();
        assertEquals(0x12, b);
        assertEquals(20, buf.capacity());
        assertEquals(100, buf.maxCapacity());
        assertEquals(1, buf.readerIndex());
        assertEquals(5, buf.writerIndex());
        assertEquals(4, buf.readableBytes());
        assertEquals(15, buf.writableBytes());
        assertEquals(95, buf.maxWritableBytes());

        int i = buf.readInt();
        assertEquals(0x3456789A, i);
        assertEquals(20, buf.capacity());
        assertEquals(100, buf.maxCapacity());
        assertEquals(5, buf.readerIndex());
        assertEquals(5, buf.writerIndex());
        assertEquals(0, buf.readableBytes());
        assertEquals(15, buf.writableBytes());
        assertEquals(95, buf.maxWritableBytes());

        buf.release();
        assertEquals(0, buf.refCnt());
    }

    @Test
    public void testPooledByteBufAllocator() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator();
        testIndex(allocator);
    }

    @Test
    public void testUnPooledByteBufAllocator() {
        UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        testIndex(allocator);
    }

    @Test
    public void testWriteIncreaseCapacity() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buf = allocator.heapBuffer(4, 8);
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(0, buf.writerIndex());

        buf.writeInt(0x12345678);
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(4, buf.writerIndex());

        // AbstractByteBuf.writeByte()
        //   AbstractByteBuf.ensureWritable0()
        //     ByteBufAllocator.calculateNewCapacity()
        //     AbstractByteBuf.capacity()
        buf.writeByte(0x9A);
        assertEquals(8, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(5, buf.writerIndex());

        // require 9 byte against maxCapacity == 8
        assertThrows(IndexOutOfBoundsException.class, () -> {buf.writeInt(1);});

        buf.release();
        assertEquals(0, buf.refCnt());
    }

    @Test
    public void testSlice() {
        AbstractByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        ByteBuf buf = allocator.heapBuffer(4, 8);
        buf.writeByte(0x12);
        buf.writeByte(0x34);
        buf.writeByte(0x56);
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(0, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        {
            ByteBuf b1 = buf.slice(1, 2);
            assertEquals(0x34, b1.readByte());
            assertEquals(2, b1.capacity());
            assertEquals(2, b1.maxCapacity());
            assertEquals(1, b1.readerIndex());
            assertEquals(2, b1.writerIndex());
        }

        {
            ByteBuf b1 = buf.slice(1, 3);
            assertEquals(0x34, b1.readByte());
            // UnpooledSlicedByteBuf.capacity()
            //   AbstractByteBuf.maxCapacity()
            assertEquals(3, b1.capacity());
            assertEquals(3, b1.maxCapacity());
            assertEquals(1, b1.readerIndex());

            // set writerIndex to length even if it is not writable
            // AbstractByteBuf.slice(index, length)
            //   new UnpooledSlicedByteBuf(AbstractByteBuf buffer, int index, int length)
            //     AbstractUnpooledSlicedByteBuf(ByteBuf buffer, int index, int length)
            //       AbstractByteBuf.writerIndex(length)
            assertEquals(3, b1.writerIndex());
        }

        buf.release();
        assertEquals(0, buf.refCnt());
    }

    @Test
    public void testDuplicate() {
        AbstractByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        ByteBuf buf = allocator.heapBuffer(4, 8);
        buf.writeByte(0x12);
        buf.writeByte(0x34);
        buf.writeByte(0x56);
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(0, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        ByteBuf b1 = buf.duplicate();
        assertEquals(4, b1.capacity());
        assertEquals(8, b1.maxCapacity());
        assertEquals(0, b1.readerIndex());
        assertEquals(3, b1.writerIndex());

        buf.release();
        assertEquals(0, buf.refCnt());
        assertEquals(0, b1.refCnt());
    }

    @Test
    public void testMarkIndex() {
        AbstractByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        ByteBuf buf = allocator.heapBuffer(4, 8);
        buf.writeByte(0x12);
        buf.writeByte(0x34);
        buf.writeByte(0x56);
        assertEquals(0x12, buf.readByte());
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(1, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        buf.markReaderIndex();
        buf.markWriterIndex();
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(1, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        buf.writeByte(0x78);
        assertEquals(4, buf.writerIndex());

        assertEquals(0x34, buf.readByte());
        assertEquals(2, buf.readerIndex());

        buf.resetReaderIndex();
        buf.resetWriterIndex();
        assertEquals(1, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        buf.clear();
        assertEquals(0, buf.readerIndex());
        assertEquals(0, buf.writerIndex());
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());

        // cannot to set readerIndex to 1 for readableBytes 0
        assertEquals(0, buf.readableBytes());
        assertThrows(IndexOutOfBoundsException.class, () -> {buf.resetReaderIndex();});
        assertEquals(0, buf.readerIndex());

        buf.resetWriterIndex();
        assertEquals(3, buf.writerIndex());

        buf.release();
        assertEquals(0, buf.refCnt());
    }

    @Test
    public void testForEachByte() {
        AbstractByteBufAllocator allocator = new UnpooledByteBufAllocator(true);
        ByteBuf buf = allocator.heapBuffer(4, 8);
        buf.writeByte(1);
        buf.writeByte(2);
        buf.writeByte(3);
        assertEquals(1, buf.readByte());
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(1, buf.readerIndex());
        assertEquals(3, buf.writerIndex());

        buf.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                return false;
            }
        });
    }
}
