/**
 * Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.smartenit.unada.commons.constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The UnadaConstants class. It includes global uNaDa constants.
 * 
 * @author George Petropoulos
 * @version 3.1
 * 
 */
public class UnadaConstants {

    public static final String APP_ID = "1498548153749732";
    public static final String APP_SECRET = "c7a252dc74a210009819e02d833ff87e";
    public static final String PERMISSIONS = "public_profile,"
            + "user_about_me," + "user_friends," + "read_stream";
    public static final String UNADA_IP_ADDRESS = "192.168.40.1";
    public static final String REDIRECT_URI = "http://" + UNADA_IP_ADDRESS
            + ":8181";
    public static final int INITIAL_DELAY = 0;
    public static String UNADA_OWNER;
    public static String UNADA_OWNER_MD5;
    public static int OVERLAY_STATUS = 3;

    public static String md5Hash(String originalString) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(originalString.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

    }
}
