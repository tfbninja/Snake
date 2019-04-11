package snake;

import java.io.InvalidObjectException;

/**
 *
 * @author Tim Barber
 */
public class Enigma {

    private static int maxAmt = 7;
    private static int additive = 50;

    /*
     * Algorithm by character(s):
     * First char: Unicode(num % 40 + 63)
     * Middle chars: num * 2 + 3
     * End char: Unicode(num % 40 + 67)
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
