# ByteBuf - slice() always sets writeIndex to capacity()

ByteBuf.slice() is used to get a "thin copy" new ByteBuf of original ByteBuf.
Instead of a full copy, new ByteBuf refers to original ByteBuf data with its own index/capacity.



```text
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
```