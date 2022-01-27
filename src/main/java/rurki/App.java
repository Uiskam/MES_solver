package rurki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.linear.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;
import static java.lang.System.out;


public class App extends Application {
    public static int findIntervalId(double pointOfIntrest, double h, int n) {
        for (int intervalID = 1; intervalID <= n; intervalID++) {
            double intervalBegin = h * (intervalID - 1), intervalEnd = h * (intervalID);
            if (intervalBegin <= pointOfIntrest && pointOfIntrest <= intervalEnd) {
                return intervalID;
            }
        }
        return 1;
    }

    private final List<Double[]> dataLines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        int n = 10, begin = 0, end = 3;
        double G = 6.674e-11;
        double h = (end - begin) / (double) n;
        double[][] matrixB = new double[n - 1][n - 1];
        double[] matrixL = new double[n - 1];
        Integral integral = new Integral();
        Function function0 = new Function(h);
        Function function1 = new Function(h);
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (i == j) {
                    function0.setDerivativeFormula(i, j);
                    function0.multiplyFormula(function0.getFunctionCoefficient());
                    matrixB[i][j] = (-1) * integral.integrate(h * (i), h * (i + 1), function0.getFunction());
                    matrixB[i][j] *= 2;

                } else if (i == j + 1 || i + 1 == j) {
                    PolynomialFunction derivativeProduct = new PolynomialFunction(new double[]{-1 / (h * h)});
                    matrixB[i][j] = (-1) * integral.integrate(h, h + h, derivativeProduct);
                } else {
                    matrixB[i][j] = 0;
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            double beginCur = h * (i), middleCur = h * (i + 1), endCur = h * (i + 2);
            matrixL[i] = 0;
            if (1 <= middleCur && beginCur <= 2) {
                function0.setFormula(i + 1, i + 1);
                matrixL[i] = integral.integrate(Math.max(1, beginCur), Math.min(2, middleCur), function0.getFunction());
            }
            if (1 <= endCur && middleCur <= 2) {
                function0.setFormula(i + 1, i + 2);
                matrixL[i] += integral.integrate(Math.max(1, middleCur), Math.min(2, endCur), function0.getFunction());
            }

            function0.setDerivativeFormula(i, i);
            function0.multiplyFormula(-1 / 3.0);
            matrixL[i] -= (-1) * integral.integrate(h * i, h * (i + 1), function0.getFunction());
            function0.setDerivativeFormula(i, i + 1);
            function0.multiplyFormula(-1 / 3.0);
            matrixL[i] -= (-1) * integral.integrate(h * (i + 1), h * (i + 2), function0.getFunction());
            matrixL[i] *= Math.PI * 4 * G;
        }

        RealMatrix B = new Array2DRowRealMatrix(matrixB);
        RealVector L = new ArrayRealVector(matrixL);
        DecompositionSolver solver = new LUDecomposition(B).getSolver();
        RealVector solution = new ArrayRealVector(new double[]{0});
        solution = solver.solve(L);
        PolynomialFunction uHatted = new PolynomialFunction(new double[]{5, -1 / 3.0});
        double phi, pointOfIntrest;
        for (int counter = 0; counter < 10000; counter++) {
            double increment = 3 / 10000.0;
            pointOfIntrest = increment * counter;
            int intervalID = findIntervalId(pointOfIntrest, h, n);
            if (intervalID == 1) {
                function0.setFormula(1, 1);
                phi = solution.getEntry(0) * function0.getFunction().value(pointOfIntrest) + uHatted.value(pointOfIntrest);
            } else if (intervalID == n) {
                function0.setFormula(n - 1, n);
                phi = solution.getEntry(n - 2) * function0.getFunction().value(pointOfIntrest) + uHatted.value(pointOfIntrest);
            } else {
                function0.setFormula(intervalID - 1, intervalID);
                function1.setFormula(intervalID, intervalID);
                phi = solution.getEntry(intervalID - 2) * function0.getFunction().value(pointOfIntrest);
                phi += solution.getEntry(intervalID - 1) * function1.getFunction().value(pointOfIntrest);
                phi += uHatted.value(pointOfIntrest);
            }
            dataLines.add(new Double[]{pointOfIntrest, phi});
        }
        //defining the axes
        primaryStage.setTitle("Line Chart Sample");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("x");
        yAxis.setLabel("phi(x)");
        //creating the chart
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();
        for (Double[] dataPair : dataLines) {
            series.getData().add(new XYChart.Data(dataPair[0], dataPair[1]));
        }
        Scene scene = new Scene(lineChart, 1000, 1000);
        lineChart.getData().add(series);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
