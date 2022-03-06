package Utils;

import java.text.DecimalFormat;
import java.util.Arrays;


public class ByteConvertUtil {

    //byte数组转为String 打印为16进制。
    public static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }

            if (i == 0)
                h = h + temp;
            else
                h = h + " " + temp;
        }
        return h;
    }

    public static String byte2hexInt(int[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
//            String temp = Integer.toHexString(buffer[i] & 0xFFFFFFFF);
            String temp = String.valueOf(buffer[i]);
//            if (temp.length() == 1) {
//                temp = "0" + temp;
//            }

            if (i == 0)
                h = h + temp;
            else
                h = h + " " + temp;
        }
        return h;
    }

    //将double经纬度转换为度分秒格式
    public static String GpsTOString(double data) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String ret_s = "";
        int tmp_i_du = (int) data;
        ret_s = String.valueOf(tmp_i_du) + "°";
        //度小数部分
        double tmp_d_du = data - tmp_i_du;
        int tmp_i_fen = (int) (tmp_d_du * 60);
        ret_s = ret_s.concat(String.valueOf(tmp_i_fen) + "′");
        double tmp_d_fen = tmp_d_du * 60 - tmp_i_fen;
        double tmp_i_miao = tmp_d_fen * 60;
        String formatStr = df.format(tmp_i_miao);//秒部分保留两位小数
        ret_s = ret_s.concat(formatStr + "″");
        return ret_s;
    }

    // 从byte数组的index处的连续8个字节获得一个long
    public static long getLong(byte[] arr) {
        if (arr.length >= 8)
            return getLong(arr, 0);

        byte b[] = Arrays.copyOf(arr, 8);
        long ret = 0;
        for (int i = 0; i < arr.length; i++) {
            ret += (0xFFL << (8 * i)) & ((long) arr[i] << (8 * i));
        }
        return ret;
    }

    /**
     * 注释：short到字节数组的转换！
     *
     * @param number
     * @return
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * 注释：字节数组到short的转换！
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static short byteToShort(byte b1, byte b2) {
        short s = 0;
        short s0 = (short) (b1 & 0xff);// 最低位
        short s1 = (short) (b2 & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static int twoByteToInt(byte b1, byte b2) {
        int s = 0;
        int s0 = (int) (b1 & 0xff);// 最低位
        int s1 = (int) (b2 & 0xff);
        s1 <<= 8;
        s = (int) (s0 | s1);
        return s;
    }

    public static short[] bytesToShorts(byte[] bs) {
        short ss[] = new short[bs.length];
        for (int i = 0; i < bs.length; i++)
            ss[i] = (short) (bs[i] & 0xff);// 最低位
        return ss;
    }


    // 从byte数组的index处的连续4个字节获得一个float
    public static float getFloat(byte[] arr, int index) {
        return Float.intBitsToFloat(getInt(arr, index));
    }

    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 3] << 24)) |
                (0x00ff0000 & (arr[index + 2] << 16)) |
                (0x0000ff00 & (arr[index + 1] << 8)) |
                (0x000000ff & arr[index + 0]);
    }


    // 从byte数组的index处的连续8个字节获得一个double
    public static double getDouble(byte[] arr, int index) {
        return Double.longBitsToDouble(getLong(arr, index));
    }


    // 从byte数组的index处的连续8个字节获得一个long
    public static long getLong(byte[] arr, int index) {
        return (0xff00000000000000L & ((long) arr[index + 7] << 56)) |
                (0x00ff000000000000L & ((long) arr[index + 6] << 48)) |
                (0x0000ff0000000000L & ((long) arr[index + 5] << 40)) |
                (0x000000ff00000000L & ((long) arr[index + 4] << 32)) |
                (0x00000000ff000000L & ((long) arr[index + 3] << 24)) |
                (0x0000000000ff0000L & ((long) arr[index + 2] << 16)) |
                (0x000000000000ff00L & ((long) arr[index + 1] << 8)) |
                (0x00000000000000ffL & (long) arr[index + 0]);
    }

    // float转换为byte[4]数组
    public static byte[] getByteArray(float f) {
        int intbits = Float.floatToIntBits(f);//将float里面的二进制串解释为int整数
        return getByteArray(intbits);
    }

    // int转换为byte[4]数组
    public static byte[] getByteArray(int i) {
        byte[] b = new byte[4];
        b[3] = (byte) ((i & 0xff000000) >> 24);
        b[2] = (byte) ((i & 0x00ff0000) >> 16);
        b[1] = (byte) ((i & 0x0000ff00) >> 8);
        b[0] = (byte) (i & 0x000000ff);
        return b;
    }

    // 大端 将int赋值到byte数组
    public static byte[] getByteArrayBig(int value) {
        byte[] arr = new byte[4];
        arr[3] = (byte) value;
        arr[2] = (byte) (value >> 8);
        arr[1] = (byte) (value >> 16);
        arr[0] = (byte) (value >> 24);
        return arr;
    }

    // long转换为byte[8]数组
    public static byte[] getByteArray(long l) {
        byte b[] = new byte[8];
        b[7] = (byte) (0xff & (l >> 56));
        b[6] = (byte) (0xff & (l >> 48));
        b[5] = (byte) (0xff & (l >> 40));
        b[4] = (byte) (0xff & (l >> 32));
        b[3] = (byte) (0xff & (l >> 24));
        b[2] = (byte) (0xff & (l >> 16));
        b[1] = (byte) (0xff & (l >> 8));
        b[0] = (byte) (0xff & l);
        return b;
    }


    // double转换为byte[8]数组
    public static byte[] getByteArray(double d) {
        long longbits = Double.doubleToLongBits(d);
        return getByteArray(longbits);
    }

    public static String Make_CRC(byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
        }
        return c;
    }

    public static int Make_CRC_INT(byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        for (int i = 0; i < data.length; i++) {
            buf[i] = data[i];
        }
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        return crc;
    }

    public static void main(String[] args) {
        byte data_ptr[] = new byte[]{-56, -43, 22, 0, 32, 0, 3, 0, 1, 0, 1, -64, 0, 0, 81, 47, -25,
                -64, 69, 51, 12, -90, 106, 78, 6, 0, 0, 0, 0, 0};// 37636    65453
        int ret = check16_sum(data_ptr);
        System.out.print("ret = " + ret);

    }

    public static int check16_sum(byte data_ptr[]) {
        int len = data_ptr.length;
        int acc = 0;

        /* dataptr may be at odd or even addresses */
        for (int i = 0; i < len - 1; i += 2) {
            /* declare first octet as most significant
            thus assume network order, ignoring host order */
            /* declare second octet as least significant */
            int src = ((data_ptr[i] & 0xff) << 8) + (data_ptr[i + 1] & 0xff);
            acc += src;
        }
        if (len % 2 == 1) {
            /* accumulate remaining octet */
            acc += ((data_ptr[len - 1] & 0xff) << 8);
        }

        /* add deferred carry bits */
        acc = (acc >> 16) + (acc & 0x0000ffff);
        if ((acc & 0xffff0000) != 0) {
            acc = (acc >> 16) + (acc & 0x0000ffff);
        }
        /* This maybe a little confusing: reorder sum using htons()
        instead of ntohs() since it has a little less call overhead.
        The caller must invert bits for Internet sum ! */
        return acc;
    }

}
