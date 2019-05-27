# ByteBuf - Increase Capacity for Write

Test steps

| Step              | WriteIndex | ByteBuf Capacity | ByteBuf MaxCapacity |
|-------------------|------------|------------------|---------------------|
| 0: Create ByteBuf | 0          | 4                | 8                   |
| 1: Write 4 bytes  | 4          | 4                | 8                   |
| 2: Write 4 bytes  | 8          | 8                | 8                   |
| 3: Write 1 byte   | 8          | 8                | 8                   |

Note

- Step 2 increases ByteBuf capacity by 4 bytes for new data
- Step 3 throws exception for ByteBuf cannot increase capacity (Capacity reaches MaxCapacity)

```text
    @Test
    public void testWriteIncreaseCapacity() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buf = allocator.heapBuffer(4, 8);   // 0
        assertEquals(4, buf.capacity());
        assertEquals(8, buf.maxCapacity());
        assertEquals(0, buf.writerIndex());

        buf.writeInt(0x12345678);
        assertEquals(4, buf.capacity());    // 1
        assertEquals(8, buf.maxCapacity());
        assertEquals(4, buf.writerIndex());

        buf.writeByte(0x9A);
        assertEquals(8, buf.capacity());    // 2
        assertEquals(8, buf.maxCapacity());
        assertEquals(5, buf.writerIndex());

        // require 9 byte against maxCapacity == 8
        assertThrows(IndexOutOfBoundsException.class, () -> {buf.writeInt(1);}); // 3

        buf.release();
        assertEquals(0, buf.refCnt());
    }
```

Code about increasing ByteBuf when Capacity cannot hold new data

```text
AbstractByteBuf.writeByte()
    AbstractByteBuf.ensureWritable0()
        ByteBufAllocator.calculateNewCapacity() // calculate new capacity
        AbstractByteBuf.capacity() // set new capacity
```
