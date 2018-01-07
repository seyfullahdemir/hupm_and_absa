package tr.edu.metu.ceng.ubim;

import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Program_Exp03_Appriori {

    /***
     * improvement: TODO change to sql
     * SELECT ID, aspect, sum(sentiment_score) FROM reviews.classification_final group by ID, aspect order by ID, aspect;
     * @param args
     */

    public static void main(String[] args) throws IOException {
        SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
                "root", "123456");
        AspectsUtil aspectsUtil = new AspectsUtil();

        ResultSet resultSet = sqlConn.executeSelectQuery("select * from amazon_review_results where aspect in (" +
                aspectsUtil.createCommaSeperatedAspects() +
                ")order by review_id");

        Map<String, Transaction_Exp03> transactionsMap = new HashMap();
        int aspectId=1;
        try {
            while (resultSet.next()) {

                //ID is id of review / transaction
                String transactionID = "" + resultSet.getInt("review_id");
                String aspect = resultSet.getString("aspect");
                double sentiment_score = resultSet.getDouble("sentiment_score");

                System.out.println("ID: " + transactionID + " aspect: " + aspect + " sentiment_score: " + sentiment_score);

                //only uncomment consciously
                //this code block should be uncommented for the first time it is being runned, so that it could insert the distinct aspects to db

/*
                ResultSet resultSet1 = sqlConn.executeSelectQuery("select * from aspect_exp03 where name = '" + aspect+"'");
                if(!resultSet1.next()){
                       sqlConn.executeUpdateQuery("insert into aspect_exp03 values(" + aspectId++ + ",'" + aspect + "')");
                }
*/

                if(! transactionsMap.containsKey(transactionID)){
                    Transaction_Exp03 transaction = new Transaction_Exp03(transactionID);
                    //sentimentScore*1 all items equal to 1
                    transaction.addItem(aspect, sentiment_score);
                    transactionsMap.put(transactionID, transaction);
                } else {
                    Transaction_Exp03 transaction = transactionsMap.get(transactionID);
                    if (transaction.hasItem(aspect)) {
                        transaction.increaseLocalUtilityOfItem(aspect, sentiment_score);
                    } else {
                        transaction.addItem(aspect, sentiment_score);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
    }

        try{
            //PrintWriter writer = new PrintWriter("transactions.txt", "UTF-8");
            PrintWriter writer = new PrintWriter("ForApprioriUsingFinalAspects_transactions_exp03.txt", "UTF-8");

            for (Map.Entry<String, Transaction_Exp03> integerTransactionEntry : transactionsMap.entrySet()) {
                System.out.println("ReviewID: " + integerTransactionEntry.getKey());
                System.out.println(integerTransactionEntry.getValue().toString());
                writer.println(integerTransactionEntry.getValue().toApprioriInputForm());
            }


            writer.close();
        } catch (IOException e) {
            // do something
        }

    }
}
