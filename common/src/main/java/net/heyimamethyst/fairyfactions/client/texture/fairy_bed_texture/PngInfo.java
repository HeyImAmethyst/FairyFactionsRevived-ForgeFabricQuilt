package net.heyimamethyst.fairyfactions.client.texture.fairy_bed_texture;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class PngInfo
{
    public final int width;
    public final int height;

    public PngInfo(Supplier<String> supplier, InputStream inputStream) throws IOException {
        MemoryStack memoryStack = MemoryStack.stackPush();

        try {
            StbReader stbReader = createCallbacks(inputStream);

            try {
                Objects.requireNonNull(stbReader);
                STBIReadCallback sTBIReadCallback = STBIReadCallback.create(stbReader::read);

                try {
                    Objects.requireNonNull(stbReader);
                    STBISkipCallback sTBISkipCallback = STBISkipCallback.create(stbReader::skip);

                    try {
                        Objects.requireNonNull(stbReader);
                        STBIEOFCallback sTBIEOFCallback = STBIEOFCallback.create(stbReader::eof);

                        try {
                            STBIIOCallbacks sTBIIOCallbacks = STBIIOCallbacks.mallocStack(memoryStack);
                            sTBIIOCallbacks.read(sTBIReadCallback);
                            sTBIIOCallbacks.skip(sTBISkipCallback);
                            sTBIIOCallbacks.eof(sTBIEOFCallback);
                            IntBuffer intBuffer = memoryStack.mallocInt(1);
                            IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                            IntBuffer intBuffer3 = memoryStack.mallocInt(1);
                            if (!STBImage.stbi_info_from_callbacks(sTBIIOCallbacks, 0L, intBuffer, intBuffer2, intBuffer3)) {
                                String var10002 = (String)supplier.get();
                                throw new IOException("Could not read info from the PNG file " + var10002 + " " + STBImage.stbi_failure_reason());
                            }

                            this.width = intBuffer.get(0);
                            this.height = intBuffer2.get(0);
                        } catch (Throwable var17) {
                            if (sTBIEOFCallback != null) {
                                try {
                                    sTBIEOFCallback.close();
                                } catch (Throwable var16) {
                                    var17.addSuppressed(var16);
                                }
                            }

                            throw var17;
                        }

                        if (sTBIEOFCallback != null) {
                            sTBIEOFCallback.close();
                        }
                    } catch (Throwable var18) {
                        if (sTBISkipCallback != null) {
                            try {
                                sTBISkipCallback.close();
                            } catch (Throwable var15) {
                                var18.addSuppressed(var15);
                            }
                        }

                        throw var18;
                    }

                    if (sTBISkipCallback != null) {
                        sTBISkipCallback.close();
                    }
                } catch (Throwable var19) {
                    if (sTBIReadCallback != null) {
                        try {
                            sTBIReadCallback.close();
                        } catch (Throwable var14) {
                            var19.addSuppressed(var14);
                        }
                    }

                    throw var19;
                }

                if (sTBIReadCallback != null) {
                    sTBIReadCallback.close();
                }
            } catch (Throwable var20) {
                if (stbReader != null) {
                    try {
                        stbReader.close();
                    } catch (Throwable var13) {
                        var20.addSuppressed(var13);
                    }
                }

                throw var20;
            }

            if (stbReader != null) {
                stbReader.close();
            }
        } catch (Throwable var21) {
            if (memoryStack != null) {
                try {
                    memoryStack.close();
                } catch (Throwable var12) {
                    var21.addSuppressed(var12);
                }
            }

            throw var21;
        }

        if (memoryStack != null) {
            memoryStack.close();
        }

    }

    private static StbReader createCallbacks(InputStream inputStream) {
        return (StbReader)(inputStream instanceof FileInputStream ? new StbReaderSeekableByteChannel(((FileInputStream)inputStream).getChannel()) : new StbReaderBufferedChannel(Channels.newChannel(inputStream)));
    }

    @Environment(EnvType.CLIENT)
    private abstract static class StbReader implements AutoCloseable {
        protected boolean closed;

        StbReader() {
        }

        int read(long l, long m, int i) {
            try {
                return this.read(m, i);
            } catch (IOException var7) {
                this.closed = true;
                return 0;
            }
        }

        void skip(long l, int i) {
            try {
                this.skip(i);
            } catch (IOException var5) {
                this.closed = true;
            }

        }

        int eof(long l) {
            return this.closed ? 1 : 0;
        }

        protected abstract int read(long l, int i) throws IOException;

        protected abstract void skip(int i) throws IOException;

        public abstract void close() throws IOException;
    }

    @Environment(EnvType.CLIENT)
    private static class StbReaderSeekableByteChannel extends StbReader {
        private final SeekableByteChannel channel;

        StbReaderSeekableByteChannel(SeekableByteChannel seekableByteChannel) {
            this.channel = seekableByteChannel;
        }

        public int read(long l, int i) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(l, i);
            return this.channel.read(byteBuffer);
        }

        public void skip(int i) throws IOException {
            this.channel.position(this.channel.position() + (long)i);
        }

        public int eof(long l) {
            return super.eof(l) != 0 && this.channel.isOpen() ? 1 : 0;
        }

        public void close() throws IOException {
            this.channel.close();
        }
    }

    @Environment(EnvType.CLIENT)
    static class StbReaderBufferedChannel extends StbReader {
        private static final int START_BUFFER_SIZE = 128;
        private final ReadableByteChannel channel;
        private long readBufferAddress = MemoryUtil.nmemAlloc(128L);
        private int bufferSize = 128;
        private int read;
        private int consumed;

        StbReaderBufferedChannel(ReadableByteChannel readableByteChannel) {
            this.channel = readableByteChannel;
        }

        private void fillReadBuffer(int i) throws IOException {
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer(this.readBufferAddress, this.bufferSize);
            if (i + this.consumed > this.bufferSize) {
                this.bufferSize = i + this.consumed;
                byteBuffer = MemoryUtil.memRealloc(byteBuffer, this.bufferSize);
                this.readBufferAddress = MemoryUtil.memAddress(byteBuffer);
            }

            byteBuffer.position(this.read);

            while(i + this.consumed > this.read) {
                try {
                    int j = this.channel.read(byteBuffer);
                    if (j == -1) {
                        break;
                    }
                } finally {
                    this.read = byteBuffer.position();
                }
            }

        }

        public int read(long l, int i) throws IOException {
            this.fillReadBuffer(i);
            if (i + this.consumed > this.read) {
                i = this.read - this.consumed;
            }

            MemoryUtil.memCopy(this.readBufferAddress + (long)this.consumed, l, (long)i);
            this.consumed += i;
            return i;
        }

        public void skip(int i) throws IOException {
            if (i > 0) {
                this.fillReadBuffer(i);
                if (i + this.consumed > this.read) {
                    throw new EOFException("Can't skip past the EOF.");
                }
            }

            if (this.consumed + i < 0) {
                int var10002 = this.consumed + i;
                throw new IOException("Can't seek before the beginning: " + var10002);
            } else {
                this.consumed += i;
            }
        }

        public void close() throws IOException {
            MemoryUtil.nmemFree(this.readBufferAddress);
            this.channel.close();
        }
    }
}
