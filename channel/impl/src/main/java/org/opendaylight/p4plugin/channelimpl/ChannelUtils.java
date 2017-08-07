/*
 * Copyright © 2017 zte and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.p4plugin.channelimpl;

import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import org.opendaylight.p4plugin.p4info.proto.*;
import java.io.*;
import java.net.URL;

/**
 * Created by hll on 7/26/17.
 */
public final class ChannelUtils {
    /**
     * Get the logic and runtime resource files generated by compiler.
     * For example:
     * p4c-bm2-ss simple_router.p4 --p4v 14 --toJSON simple_router.json
     * p4c-bm2-ss simple_router.p4 --p4v 14 --p4-runtime-file simple_router.proto.txt --p4runtime-format text
     */
    public static File getResourceFile(String path) {
        URL url = ChannelUtils.class.getResource(path);
        String  path1 = url.getPath();
        String pp = url.getFile();
        return new File(url.getFile());
    }

    /**
     * Parses the runtime proto file.
     */
    public static P4Info parseRunTimeInfo(URL file) throws IOException {
        InputStream input = file.openStream();
        Reader reader = new InputStreamReader(input);

        try {
            P4Info.Builder info = P4Info.newBuilder();
            TextFormat.merge(reader, info);
            return info.build();
        } finally {
            reader.close();
        }
    }

    /**
     * Parses the device configuration file, for example json for bmv2.
     */
    public static ByteString parseDeviceConfigInfo(URL file) throws IOException {
        InputStream input = file.openStream();
        return ByteString.readFrom(input);
    }

    /**
     *
     * @param str ip or mac or value
     * @param len serialized len
     * @return
     */
    public static byte[] strToByteArray(String str, int len) {
        String[] strArray = null;
        byte[] byteArray = null;

        /* regular ipv4 address match (1~255).(0~255).(0~255).(0~255) */
        if(str.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]|25[0-5])\\."
                + "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){2}"
                + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])")) {
            strArray = str.split("\\.");
            byteArray = new byte[strArray.length];
            assert(len == strArray.length);
            for (int i = 0; i < strArray.length; i++) {
                byteArray[i] = (byte)Integer.parseInt(strArray[i]);
            }
        } else if (str.matches("([0-9a-fA-F]{1,2}:){5}[0-9a-fA-F]{1,2}")){ /* mac address,aa:bb:cc:dd:ee:ff,1:2:3:4:5:6 */
            strArray = str.split(":");
            byteArray = new byte[strArray.length];
            assert(len == strArray.length);
            for (int i = 0; i < strArray.length; i++) {
                byteArray[i] = (byte)Integer.parseInt(strArray[i],16);
            }
        } else {
            int value = Integer.parseInt(str);
            byteArray = new byte[len];
            for(int i = 0; i < len; i++) {
                byteArray[i] = (byte)(value >> ((len - i - 1) * 8) & 0xFF);
            }
        }
        return byteArray;
    }
}
