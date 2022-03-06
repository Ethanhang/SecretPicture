package Utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {


    /**
     * short[] 转 byte[]
     */
    public static byte[] toBytes(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i]);
            dest[i * 2 + 1] = (byte) (src[i] >> 8);
        }

        return dest;
    }

    /**
     * 以大端模式将byte[]转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    // 大端 将int赋值到byte数组
    public static void setInt1(byte[] arr, int index, int value) {
        arr[3 + index] = (byte) value;
        arr[2 + index] = (byte) (value >> 8);
        arr[1 + index] = (byte) (value >> 16);
        arr[0 + index] = (byte) (value >> 24);
    }

    /**
     * byte数组到int的转换(大端)
     * @param bytes
     * @return
     */
    public static int bytes2IntBig(byte[] bytes )
    {
        int int1=bytes[3]&0xff;
        int int2=(bytes[2]&0xff)<<8;
        int int3=(bytes[1]&0xff)<<16;
        int int4=(bytes[0]&0xff)<<24;

        return int1|int2|int3|int4;
    }


    /**
     * short[] 转 byte[]
     */
    public static byte[] toBytes(short src) {
        byte[] dest = new byte[2];
        dest[0] = (byte) (src);
        dest[1] = (byte) (src >> 8);

        return dest;
    }

    /**
     * int 转 byte[]
     */
    public static byte[] toBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0xff);
        b[1] = (byte) ((i >> 8) & 0xff);
        b[2] = (byte) ((i >> 16) & 0xff);
        b[3] = (byte) ((i >> 24) & 0xff);
        return b;
    }


    /**
     * String 转 byte[]
     */
    public static byte[] toBytes(String str) {
        return str.getBytes();
    }

    /**
     * long类型转成byte数组
     */
    public static byte[] toBytes(long number) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, number);
        return buffer.array();
    }

    public static int toInt(byte[] src, int offset) {
        return ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
    }

    public static int toInt(byte[] src) {
        return toInt(src, 0);
    }

    /**
     * 字节数组到long的转换.
     */
    public static long toLong(byte[] b) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(b, 0, b.length);
        return buffer.getLong();
    }

    /**
     * byte[] 转 short[]
     * short： 2字节
     */
    public static short[] toShorts(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) ((src[i * 2] & 0xff) | ((src[2 * i + 1] & 0xff) << 8));
        }
        return dest;
    }

    public static byte[] merger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static int[] mergerIntArrary(int[] bt1, int[] bt2) {
        int[] bt3 = new int[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static String toString(byte[] b) {
        return Arrays.toString(b);
    }
}
