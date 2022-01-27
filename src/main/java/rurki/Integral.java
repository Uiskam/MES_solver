package rurki;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class Integral {
    private final int n = 2;
    private final double weight = 1.0;
    private final double[] points = {-1 / Math.sqrt(3), 1 / Math.sqrt(3)};
    //private final double[] interval = {0, 0};

    /*public rurki.Integral(double begin, double end) {
        this.interval[0] = begin;
        this.interval[1] = end;
    }*/

    public double integrate(double begin, double end, PolynomialFunction function) {
        double result = 0;
        double coefficient =  (end - begin) / 2.0;
        double coefficient1 =  (end + begin) / 2.0;
        for(double point : points){
            result += weight * function.value(coefficient * point + coefficient1);
        }
        result *= coefficient;
        return result;

    }

}
