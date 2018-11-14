import java.util.Arrays;

public class Nav {
    // Variables
    public double angle1 = 0.0;
    public double angle2 = 0.0;
    public double x = 0.0;
    public double y = 0.0;
    public double[] location = new double[2];
    public int iterations = 100;
    private double[] inc = new double[]{0.01, 0.01};
    private double[] tol = new double[]{1.0E-6, 1.0E-6};
    private double[] initial = new double[]{8.0, 8.0};
    public int success = 0;
    public int divergence = 1;
    public int singular = 2;
    public int range = 3;

    public void setAngles(double d1, double d2) {
        angle1 = d1 * 0.01745329251994329;
        angle2 = d2 * 0.01745329251994329;
    } // end setAngles
    public void setCoordinates(double d1, double d2) {
        x = d1;
        y = d2;
    } // end setCoordinates
    public int newtonRaphson() {
        if (angle1 >= 0.7853 && angle1 <= 2.618) {
            if (angle2 >= 0.7853 && angle2 <= 2.618) {
               // variables
                double[][] aD = new double[2][2];
                double[] aD1 = new double[2];
                double[] aD2 = new double[2];
                double[] aD3 = new double[2];
                double[] aD4 = new double[2];
                double[] aD5 = new double[2];
                int[] aI = new int[2];
                boolean m = true;
                int k = 0;

                location = Arrays.copyOf(initial, 2);

                for(int counter = 0; counter < iterations; counter++) {
                    math(location, aD2);
                    for(int j = 0; j < 2; j++) {
                        for(int i = 0; i < 2; i++){
                            aD5[i] = location[i];
                        }
                        aD5[j] = location[j] - inc[j];
                        math(aD5, aD3);
                        aD5[j] = location[j] + inc[j];
                        math(aD5, aD4);
                        for(int i = 0; i < 2; ++i) {
                            aD[i][j] = (aD4[i] - aD3[i]) / (2.0 * inc[j]);
                        }
                    } // end for
                    if (gaussJordan(2, aD, aD2, aD1, aI, 1.0E-15) != 0) {
                        return 2;
                    }
                    for(int i = 0; i < 2; ++i) {
                        location[i] -= aD1[aI[i]];
                        if (Math.abs(aD1[aI[i]]) > tol[i]) {
                            m = false;
                        }
                    } // end for
                    if (m) {
                        break;
                    }
                } // end for
                return k == 100 ? 1 : 0;
            } else {
                return 3;
            }

        } else {
            return 3;
        }
    } // end newtonRaphson
    public double[] getLocation() {
        return location;
    }
    public void math(double[] a1, double[] a2) {
        double d1 = (a1[0] - x) * (a1[0] - x) + a1[1] * a1[1];
        double d2 = Math.sqrt(d1);
        double d3 = a1[0] * a1[0] + (a1[1] - y) * (a1[1] - y);
        double d4 = Math.sqrt(d3);
        double d5 = a1[0] * a1[0] + a1[1] * a1[1];
        double d6 = Math.sqrt(d5);
        a2[0] = d1 + d5 - 2.0 * d2 * d6 * Math.cos(angle1) - x * x;
        a2[1] = d3 + d5 - 2.0 * d4 * d6 * Math.cos(angle2) - y * y;
    } // end math
    public static int gaussJordan(int pI, double[][] aD, double[] a1, double[] a2, int[] aI, double pD) {
        // Variables
        double[] arrayOfDouble = new double[pI];
        double d4;

        for(int i = 0; i < pI; ++i) {
            aI[i] = i;
            arrayOfDouble[i] = Math.abs(aD[i][0]);
            for(int j = 1; j < pI; ++j) {
                d4 = Math.abs(aD[i][j]);
                if (d4 > arrayOfDouble[i]) {
                    arrayOfDouble[i] = d4;
                } // end if
            } // end for
            if (arrayOfDouble[i] < pD) {
                return 1;
            } // end if
        } // end for
        for(int k = 0; k < pI - 1; ++k) {
            int m = k;
            double d3 = Math.abs(aD[aI[k]][k]) / arrayOfDouble[aI[k]];
            for(int i = k + 1; i < pI; ++i) {
                d4 = Math.abs(aD[aI[i]][k]) / arrayOfDouble[aI[i]];
                if (d4 > d3) {
                    d3 = d4;
                    m = i;
                } // end if
            } // end for
            if (d3 < pD) {
                return 1;
            } // end if
            if (m != k) {
                int i1 = aI[m];
                aI[m] = aI[k];
                aI[k] = i1;
            } // end if
            for(int i = k + 1; i < pI; ++i) {
                double d1 = aD[aI[i]][k] / aD[aI[k]][k];
                for(int j = k + 1; j < pI; ++j) {
                    aD[aI[i]][j] -= d1 * aD[aI[k]][j];
                } // end for
                a1[aI[i]] -= d1 * a1[aI[k]];
            } // end for
        } // end for
        for(int i = pI - 1; i >= 0; --i) {
            if (Math.abs(aD[aI[i]][i]) < pD) {
                return 1;
            }

            double d2 = a1[aI[i]];

            for(int j = i + 1; j < pI; ++j) {
                d2 -= aD[aI[i]][j] * a2[aI[j]];
            }

            a2[aI[i]] = d2 / aD[aI[i]][i];
        }

        return 0;
    } // end gJ
} // end class