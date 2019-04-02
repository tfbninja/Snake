package snake;

import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 *
 * @author Tim Barber
 */
public class Enigma {
    // works up to 255

    private static int maxAmt = 7;
    private static int additive = 50;

    /*
     * So the old method doesn't work, new method:
     * Unicode(num % 40 + 63)
     * num * 2 + 3
     * Unicode(num % 40 + 67)
     */
    /**
     *
     * @param num the raw number to encode
     * @return the encoded version of num
     */
    public static String encode(int num) {
        String first = String.valueOf((char) (num % 40 + 63));
        String mid = String.valueOf(num * 2 + 3);
        String last = String.valueOf((char) (num % 40 + 67));
        return first + "" + mid + "" + last;
    }

    /**
     *
     * @param encoded the encoded string
     * @return the original integer
     * @throws InvalidObjectException
     */
    public static int decode(String encoded) throws InvalidObjectException {
        boolean longShotLengthCheck = encoded.length() <= 5;
        char first = encoded.charAt(0);
        int mid = Integer.valueOf(encoded.substring(1, encoded.length() - 1));
        char last = encoded.charAt(encoded.length() - 1);
        mid -= 3;
        mid /= 2; // now we have the real value
        boolean firstCharCheck = Integer.valueOf(first) - 63 == mid % 40;
        boolean lastCharCheck = Integer.valueOf(last) - 67 == mid % 40;
        if (!(longShotLengthCheck && firstCharCheck && lastCharCheck)) {
            throw new InvalidObjectException("first char: " + firstCharCheck + " last char: " + lastCharCheck + " length check: " + longShotLengthCheck);
        }
        return mid;
    }

    /**
     *
     * @param num
     * @return
     */
    public static String encodeOld(int num) {
        /*
         * Old method:
         * i^2 + 15 number of chars
         * last character is unicode(i^2 + 17)
         * character i is unicode(i + 9)
         * first character is unicode(multiple of 13)
         * all other characters must be multiples of 2 or 3
         */
        Random random = new Random(LocalDateTime.now().getNano());
        String encoded = "";
        encoded += (char) (65 + random.nextInt(maxAmt) * 13);
        for (int i = 0; i < num - 1; i++) {
            if (random.nextBoolean()) {
                encoded += (char) ((random.nextInt(maxAmt) + additive) * 2);
            } else {
                encoded += (char) ((random.nextInt(maxAmt) + additive) * 3);
            }
        }
        encoded += (char) (num + 9);
        for (int i = 0; i < num * num - num + 13; i++) {
            if (random.nextBoolean()) {
                encoded += (char) ((random.nextInt(maxAmt) + additive) * 2);
            } else {
                encoded += (char) ((random.nextInt(maxAmt) + additive) * 3);
            }
        }
        encoded += (char) (num * num + 17);

        return encoded;
    }

    /**
     *
     * @param encoded
     * @return
     * @throws InvalidObjectException
     */
    public static int decodeOld(String encoded) throws InvalidObjectException {
        /*
         * Old method:
         * i^2 + 15 number of chars
         * last character is unicode(i^2 + 17)
         * character i is unicode(i + 9)
         * first character is unicode(multiple of 13)
         * all other characters must be multiples of 2 or 3
         */
        boolean firstCheck = encoded.charAt(0) % 13 == 0;
        int temp = (int) Math.sqrt(encoded.charAt(encoded.length() - 1) - 17);
        boolean lengthCheck = encoded.length() == temp * temp + 15;
        boolean tempCheck = encoded.charAt(temp) == (char) (temp + 9);
        boolean otherChecks = true;

        int index = 0;
        for (char c : encoded.toCharArray()) {
            if (index != temp && index != 0 && index != encoded.length() - 1) {
                otherChecks = otherChecks && ((int) c % 2 == 0 || (int) c % 3 == 0);
            }
        }
        if (!firstCheck || !lengthCheck || !tempCheck || !otherChecks) {
            throw new InvalidObjectException("first char check: " + firstCheck + " length check: " + lengthCheck + " last char check: " + tempCheck + " other chars check: " + otherChecks);
        }

        return temp;
    }
}


/*
 * The MIT License
 *
 * Copyright (c) 2018 Tim Barber.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
