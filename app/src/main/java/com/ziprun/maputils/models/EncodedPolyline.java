/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ziprun.maputils.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encoded Polylines are used by the API to represent paths.
 * <p/>
 * <p>See <a href="https://developers.google
 * .com/maps/documentation/utilities/polylinealgorithm">Encoded Polyline Algorithm</a> for more
 * detail on the protocol.
 */
public class EncodedPolyline {
    private final String points;

    /**
     * @param encodedPoints A string representation of a path, encoded with the Polyline Algorithm.
     */
    public EncodedPolyline(String encodedPoints) {
        this.points = encodedPoints;
    }

    /**
     * @param points A path as a collection of {@code LatLng} points.
     */
    public EncodedPolyline(List<LatLng> points) {
        this.points = PolylineEncoding.encode(points);
    }

    public String getEncodedPath() {
        return points;
    }

    public List<LatLng> decodePath() {
        return PolylineEncoding.decode(points);
    }


    /**
     * Utility class that encodes and decodes Polylines.
     *
     * <p>See {@url https://developers.google.com/maps/documentation/utilities/polylinealgorithm} for
     * detailed description of this format.
     */
    public static class PolylineEncoding {
        /**
         * Decodes an encoded path string into a sequence of LatLngs.
         */
        public static List<LatLng> decode(final String encodedPath) {

            int len = encodedPath.length();

            final List<LatLng> path = new ArrayList<LatLng>(len / 2);
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int result = 1;
                int shift = 0;
                int b;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                result = 1;
                shift = 0;
                do {
                    b = encodedPath.charAt(index++) - 63 - 1;
                    result += b << shift;
                    shift += 5;
                } while (b >= 0x1f);
                lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

                path.add(new LatLng(lat * 1e-5, lng * 1e-5));
            }

            return path;
        }

        /**
         * Encodes a sequence of LatLngs into an encoded path string.
         */
        public static String encode(final List<LatLng> path) {
            long lastLat = 0;
            long lastLng = 0;

            final StringBuffer result = new StringBuffer();

            for (final LatLng point : path) {
                long lat = Math.round(point.latitude * 1e5);
                long lng = Math.round(point.longitude * 1e5);

                long dLat = lat - lastLat;
                long dLng = lng - lastLng;

                encode(dLat, result);
                encode(dLng, result);

                lastLat = lat;
                lastLng = lng;
            }
            return result.toString();
        }

        private static void encode(long v, StringBuffer result) {
            v = v < 0 ? ~(v << 1) : v << 1;
            while (v >= 0x20) {
                result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
                v >>= 5;
            }
            result.append(Character.toChars((int) (v + 63)));
        }

        /**
         * Encodes an array of LatLngs into an encoded path string.
         */
        public static String encode(LatLng[] path) {
            return encode(Arrays.asList(path));
        }
    }
}
