/*
 * Copyright © 2017 zte and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.p4plugin.core.impl;

import com.google.common.base.Preconditions;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import org.opendaylight.p4plugin.p4info.proto.*;
import java.io.*;

public abstract class Utils {
    /**
     * Parses the device config and runtime resource files generated by compiler.
     * For example:
     * p4c-bm2-ss simple_router.p4 --p4v 14 --p4-runtime-file simple_router.proto.txt --p4runtime-format text
     */
    public static P4Info parseRuntimeInfo(String file) throws IOException {
        Preconditions.checkArgument(file != null, "Runtime Info file is null.");
        Reader reader = null;
        P4Info.Builder info = P4Info.newBuilder();
        try {
            reader = new FileReader(file);
            TextFormat.merge(reader, info);
            return info.build();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Parses the device config file generated by the compiler.
     * For example:
     * p4c-bm2-ss simple_router.p4 --p4v 14 --toJSON simple_router.json
     */
    public static ByteString parseDeviceConfigInfo(String file) throws IOException {
        Preconditions.checkArgument(file !=  null, "Device config file is null.");
        InputStream input = new FileInputStream(new File(file));
        return ByteString.readFrom(input);
    }

    /**
     * Only support ipv4 address, mac address and integer value.
     */
    public static byte[] strToByteArray(String str, int len) {
        String[] strArray = null;
        byte[] byteArray = null;

        /* regular ipv4 address match (1~255).(0~255).(0~255).(0~255) */
        if (str.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]|25[0-5])\\."
                + "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){2}"
                + "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])")) {
            strArray = str.split("\\.");
            byteArray = new byte[strArray.length];
            assert (len == strArray.length);
            for (int i = 0; i < strArray.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(strArray[i]);
            }
        } else if (str.matches("([0-9a-fA-F]{1,2}:){5}[0-9a-fA-F]{1,2}")) { /* mac address,aa:bb:cc:dd:ee:ff,1:2:3:4:5:6 */
            strArray = str.split(":");
            byteArray = new byte[strArray.length];
            assert (len == strArray.length);
            for (int i = 0; i < strArray.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(strArray[i], 16);
            }
        } else {
            int value = Integer.parseInt(str);
            byteArray = new byte[len];
            for (int i = 0; i < len; i++) {
                byteArray[i] = (byte) (value >> ((len - i - 1) * 8) & 0xFF);
            }
        }
        return byteArray;
    }

    public static String byteArrayToStr(byte[] input) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < input.length; i++) {
            buffer.append(String.valueOf(input[i]));
            buffer.append(" ");
        }
        return new String(buffer);
    }
}