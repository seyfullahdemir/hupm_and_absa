package tr.edu.metu.ceng.ubim;

import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {
    private int id;
    private String transactionID;
    Map<String, Utility> localUtilitiesPerItemMap;

    static SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
            "root", "123456");


    public Transaction(int id) {
        this.id = id;
        localUtilitiesPerItemMap = new HashMap();
    }

    public Transaction(String transactionID) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();



        List<Double> valueList = new ArrayList();
        for (Map.Entry<String, Utility> stringStringEntry : localUtilitiesPerItemMap.entrySet()) {
            String key = stringStringEntry.getKey();
            Utility value = stringStringEntry.getValue();
            valueList.add(value.getValue());

            ResultSet resultSet = sqlConn.executeSelectQuery("select id from aspect where name = '" + key +"'");

            try {
                resultSet.next();
                sb.append(resultSet.getInt("id") + " ");
            } catch (SQLException e) {
                sb.append(key + " ");
            }

        }
        sb.deleteCharAt(sb.toString().length()-1);
        sb.append(":");
        sb.append(""+ (1)*(int)valueList.stream().mapToDouble(i->i).sum());
        /*negation*/ //sb.append(""+ (-1)*(int)valueList.stream().mapToDouble(i->i).sum());
        sb.append(":");
        for (Double utility : valueList) {
            //sb.append(utility.intValue() + " ");
            sb.append(utility.intValue() * (1) + " ");
            /*negation*/ //sb.append(utility.intValue() * (-1) + " ");
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
