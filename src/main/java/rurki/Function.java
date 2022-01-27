package rurki;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class Function {
    private final double h;
    private PolynomialFunction functionFormula;

    public Function(double h) {
        this.h = h;
    }

    public void setDerivativeFormula(int functionIntervalId, int wantedIntervalId) {
        if (functionIntervalId == wantedIntervalId) {
            functionFormula = new PolynomialFunction(new double[]{1 / h});
        } else if (functionIntervalId + 1 == wantedIntervalId) {
            functionFormula = new PolynomialFunction(new double[]{-1 / h});
        } else {
            functionFormula = new PolynomialFunction(new double[]{0});
        }
    }

    public void setFormula(int functionIntervalId, int wantedIntervalId) {
        if (functionIntervalId == wantedIntervalId) {
            functionFormula = new PolynomialFunction(new double[]{1 - functionIntervalId, 1/h});
        } else if (functionIntervalId + 1 == wantedIntervalId) {
            functionFormula = new PolynomialFunction(new double[]{1 + functionIntervalId, -1/h});
        } else {
            functionFormula = new PolynomialFunction(new double[]{0});
        }
    }

    public double getFunctionCoefficient() {
        return this.functionFormula.getCoefficients()[0];
    }

    public PolynomialFunction getFunction(){
        return this.functionFormula;
    }

    public void multiplyFormula(double number) {
        this.functionFormula = new PolynomialFunction(new double[]{this.functionFormula.getCoefficients()[0] * number});
    }
}
