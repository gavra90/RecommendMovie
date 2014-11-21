/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lsafunctions;

import java.util.HashMap;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Gavra
 */
public class LSA {

    public static double cosinSim(int v1, int v2, RealMatrix Vt) {
        double sim = 0.0;
        double sumNum = 0.0;
        double fdenom = 0.0;
        double sdenom = 0.0;

        for (int j = 0; j < Vt.getRowDimension(); j++) {
            sumNum += Vt.getEntry(j, v1) * Vt.getEntry(j, v2);
            fdenom += Math.pow(Vt.getEntry(j, v1), 2);
            sdenom += Math.pow(Vt.getEntry(j, v2), 2);
        }
        sim = sumNum / (Math.sqrt(fdenom) * Math.sqrt(sdenom));

        return sim;
    }

    public static RealMatrix calculateTfIdf(RealMatrix M) {
        int tf;
        double idf;
        int df;
        double ndf;
        for (int j = 0; j < M.getRowDimension(); j++) {

            df = calcDf(j, M);
            // System.out.println("J:"+j+"  df:"+df);

            for (int k = 0; k < M.getColumnDimension(); k++) {
                tf = (int) M.getEntry(j, k);
                ndf = M.getColumnDimension() / df;
                idf = Math.log(ndf) / Math.log(2);
                M.setEntry(j, k, idf * tf);
            }
        }
        //M.print(NumberFormat.INTEGER_FIELD, M.getColumnDimension());
        M = normalizeMatrix(M);
        return M;
    }

    private static int calcDf(int nRow, RealMatrix M) {
        int df = 0;
        for (int j = 0; j < M.getColumnDimension(); j++) {
            if (M.getEntry(nRow, j) != 0) {
                df++;
            }
        }
        return df;
    }

    private static RealMatrix normalizeMatrix(RealMatrix M) {
        double sumColumn = 0;
//        Matrix row = new Matrix(1, M.getColumnDimension());
        RealMatrix row = MatrixUtils.createRealMatrix(1, M.getColumnDimension());
        for (int j = 0; j < M.getColumnDimension(); j++) {
            sumColumn = 0;
            for (int k = 0; k < M.getRowDimension(); k++) {
                sumColumn += Math.pow(M.getEntry(k, j), 2);
            }
            sumColumn = Math.sqrt(sumColumn);
            row.setEntry(0, j, sumColumn);
        }
        for (int j = 0; j < M.getColumnDimension(); j++) {
            for (int k = 0; k < M.getRowDimension(); k++) {
                M.setEntry(k, j, M.getEntry(k, j) / row.getEntry(0, j));
            }
        }
        return M;
    }

    private static HashMap addToDicry(String[] listLema1, HashMap dicry, int i) {
        for (String item : listLema1) {
            if (!dicry.containsValue(item.toLowerCase())) {
                dicry.put(i, item.toLowerCase());
                i++;
            }
        }
        return dicry;
    }

}
