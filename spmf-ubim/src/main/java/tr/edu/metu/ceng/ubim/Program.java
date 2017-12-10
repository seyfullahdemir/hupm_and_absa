package tr.edu.metu.ceng.ubim;

import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Program {

    /***
     * improvement: TODO change to sql
     * SELECT ID, aspect, sum(sentiment_score) FROM reviews.classification_final group by ID, aspect order by ID, aspect;
     * @param args
     */

    public static void main(String[] args) {
        SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
                "root", "123456");
        ResultSet resultSet = sqlConn.executeSelectQuery("select * from classification_final order by id");

        Map<String, Transaction> transactionsMap = new HashMap();
        int aspectId=1;
        try {
            while (resultSet.next()) {

                //TOPIC_ID + ID is id of review / transaction
                int reviewID = resultSet.getInt("ID");
                int topicID = resultSet.getInt("TopicID");
                String aspect = resultSet.getString("aspect");
                String sentiment_word = resultSet.getString("sentiment_word");
                double sentiment_score = resultSet.getDouble("sentiment_score");

                System.out.println("ID: " + reviewID +"_" + topicID + " aspect: " + aspect +
                        " sentiment_word: " + sentiment_word + " sentiment_score: " + sentiment_score);



                //only uncomment consciously
                //this code block should be uncommented for the first time it is being runned, so that it could insert the distinct aspects to db
                /*

                ResultSet resultSet1 = sqlConn.executeSelectQuery("select * from aspect where name = '" + aspect+"'");
                if(!resultSet1.next()){
                       sqlConn.executeUpdateQuery("insert into aspect values(" + aspectId++ + ",'" + aspect + "')");
                }
                */


                String transactionID = reviewID + "_" + topicID;
                if(! transactionsMap.containsKey(transactionID)){
                    Transaction transaction = new Transaction(transactionID);
                    //sentimentScore*1 all items equal to 1
                    transaction.addItem(aspect, sentiment_score);
                    transactionsMap.put(transactionID, transaction);
                } else {
                    Transaction transaction = transactionsMap.get(transactionID);
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
            PrintWriter writer = new PrintWriter("transactions_exp01.txt", "UTF-8");

            for (Map.Entry<String, Transaction> integerTransactionEntry : transactionsMap.entrySet()) {
                System.out.println("ReviewID: " + integerTransactionEntry.getKey());
                System.out.println(integerTransactionEntry.getValue().toString());
                writer.println(integerTransactionEntry.getValue().toString());
            }


            writer.close();
        } catch (IOException e) {
            // do something
        }
    }
}
