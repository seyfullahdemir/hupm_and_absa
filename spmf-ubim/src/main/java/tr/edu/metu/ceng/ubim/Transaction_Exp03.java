package tr.edu.metu.ceng.ubim;

import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Transaction_Exp03 {
    private int id;
    private String transactionID;
    Map<String, Utility> localUtilitiesPerItemMap;

    static SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
            "root", "123456");


    public Transaction_Exp03(int id) {
        this.id = id;
        localUtilitiesPerItemMap = new HashMap();
    }

    public Transaction_Exp03(String transactionID) {
        this.transactionID = transactionID;
        localUtilitiesPerItemMap = new HashMap();
    }


    public boolean hasItem(String item){
        return localUtilitiesPerItemMap.containsKey(item);
    }

    public void addItem(String item, double itemUtility){
        localUtilitiesPerItemMap.put(item, new Utility(itemUtility));
    }

    public void increaseLocalUtilityOfItem(String item, double itemUtility){
        Utility utility = localUtilitiesPerItemMap.get(item);
        utility.setValue(utility.getValue() + itemUtility);
    }

    public String toApprioriInputForm(){
        StringBuilder sb = new StringBuilder();


        Map<Integer, Utility> sortedMap = new TreeMap();
        for (Map.Entry<String, Utility> stringStringEntry : localUtilitiesPerItemMap.entrySet()) {
            String key = stringStringEntry.getKey();
            Utility value = stringStringEntry.getValue();
            ResultSet resultSet = sqlConn.executeSelectQuery("select id from aspect_exp03 where name = '" + key +"'");
            try {
                resultSet.next();
                final int aspectId = resultSet.getInt("id");
                sortedMap.put(aspectId, value);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<Integer, Utility> integerUtilityEntry : sortedMap.entrySet()) {
            Integer key = integerUtilityEntry.getKey();
            sb.append(key + " ");
        }

        sb.deleteCharAt(sb.toString().length()-1);

        return sb.toString();

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();


        Map<Integer, Utility> sortedMap = new TreeMap();
        for (Map.Entry<String, Utility> stringStringEntry : localUtilitiesPerItemMap.entrySet()) {
            String key = stringStringEntry.getKey();
            Utility value = stringStringEntry.getValue();
            ResultSet resultSet = sqlConn.executeSelectQuery("select id from aspect_exp03 where name = '" + key +"'");
            try {
                resultSet.next();
                final int aspectId = resultSet.getInt("id");
                sortedMap.put(aspectId, value);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        List<Double> valueList = new ArrayList();
        for (Map.Entry<Integer, Utility> integerUtilityEntry : sortedMap.entrySet()) {
            Integer key = integerUtilityEntry.getKey();
            Utility value = integerUtilityEntry.getValue();
            valueList.add(value.getValue());
            sb.append(key + " ");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        sb.append(":");
        /*negation*/ sb.append(""+ (-1)*(int)valueList.stream().filter(i -> i<0).mapToDouble(i->i).sum());
      //  sb.append(""+ (1)* (int)valueList.stream().filter(i -> i>=0).mapToDouble(i->i).sum());
        sb.append(":");
        for (Double utility : valueList) {
           /*negation*/ sb.append(utility.intValue()* (-1) + " ");
           //sb.append(utility.intValue() * (1) + " ");
        }
        sb.deleteCharAt(sb.toString().length()-1);


        /*
        for (Map.Entry<String, Utility> stringUtilityEntry : localUtilitiesPerItemMap.entrySet()) {
            sb.append(stringUtilityEntry.getKey() + " " + stringUtilityEntry.getValue().getValue() + "\n");
        }
        */
        return sb.toString();
    }
}
