import java.sql.*;
import java.util.*;

// |------------------------------------------------------------------------------------------------------------------|
// | Kyle Welsh                                                                                                       |
// | Homework Four                                                                                                    |
// | Professor St. Clair                                                                                              |
// |------------------------------------------------------------------------------------------------------------------|
// | Your program must provide a menu driven interface that allows a user to run queries 1, 2, 3, 4, and 5.           |
// | The user should be allowed to run these queries as often as they wish without having to restart your program.    |  
// | The user should be allowed to enter data values for queries asking “for a particular …”.                         |
// | The output from your queries must appear in a tabular format that is well formatted.                             |
// |------------------------------------------------------------------------------------------------------------------|

public class realtorProgram 
{
    // pre: 1st parm is scanner used for input
    // post: query string is returned
    public static String queryOne(Scanner scan) 
    {
        String userInput;

        System.out.println("Enter Land Buying Client (CID): ");
        userInput = scan.nextLine();
        System.out.println("+----------------------------------------------------------+");

        String queryOne = 
                  " SELECT *"
                + " FROM landListingWithAllInfo"
                + " WHERE EXISTS"
                + " (SELECT w.minacres, w.maxacres"
                + " FROM bclientland w"
                + " WHERE acres <= w.maxacres AND acres >= minacres AND cid = " + userInput + ");";

        return queryOne;
    }

    // pre: 1st parm is scanner used for input
    // post: query string is returned
    public static String queryTwo(Scanner scan) 
    {
        String userInput;

        System.out.println("Enter House Buying Client (CID): ");
        userInput = scan.nextLine();
        System.out.println("+----------------------------------------------------------+");

        String queryTwo = 
                  " SELECT *"
                + "     FROM houseListingWithAllInfo hlwai"
                + "     WHERE EXISTS"
                + "     (SELECT w.style, w.beds, w.baths"
                + "         FROM bclienthouse w"
                + "         WHERE hlwai.style = w.style AND hlwai.beds = w.beds" 
                + "             AND hlwai.baths = w.baths AND cid = " + userInput + ");";

        return queryTwo;
    }    
    
    // pre: 1st parm is connection to SQL
    // post: shows the property id and all individuals 
	//       involved in each transaction (buying realtor, selling realtor, seller, buyer)
    public static void queryThree(Connection ctSQL) throws SQLException
    {
        Statement statement = ctSQL.createStatement();
        ResultSet returnedResult = statement.executeQuery(
                                      " SELECT pid, buyingrid, sellingrid, buyingclient, sellingclient" 
                                    + "     FROM allTransactionsWithSellPrice"
                                    + "     ORDER BY pid");

        System.out.println("|                          Result                          |");
        System.out.println("+----------------------------------------------------------+");
        System.out.format("%15s%15s%15s%15s%15s\n", "PID", "BUYINGRID", "SELLINGRID", "BCLIENT", "SCLIENT");

        while(returnedResult.next()) 
        {
            System.out.format("%15s%15s%15s%15s%15s\n", returnedResult.getString("pid")
            , returnedResult.getString("buyingrid")
            , returnedResult.getString("sellingrid")
            , returnedResult.getString("buyingclient")
            , returnedResult.getString("sellingclient"));
        }
    }    
    
    // pre: 1st parm is connection to SQL
    // post: shows all properties that sold for more than the listing price
    public static void queryFour(Connection ctSQL) throws SQLException
    {
        Statement statement = ctSQL.createStatement();
        ResultSet returnedResult = statement.executeQuery(
                                      "	SELECT * " 
                                    + "     FROM transactionWithPrices"
                                    + "    	WHERE sellprice > lprice");

        System.out.println("|                          Result                          |");
        System.out.println("+----------------------------------------------------------+");
        System.out.format("%15s%15s%15s%15s%15s\n", "PID", "BUYINGRID", "SELLINGRID", "LPRICE", "SELLPRICE");

        while(returnedResult.next()) 
        {
            System.out.format("%15s%15s%15s%15s%15s\n", returnedResult.getString("pid")
            , returnedResult.getString("buyingrid")
            , returnedResult.getString("sellingrid")
            , returnedResult.getString("lprice")
            , returnedResult.getString("sellprice"));
        }
    }
    
    // pre: 1st parm is connection to SQL
    // post: shows the realtor id and name for any realtor involved in more than three transactions
    //       as the buying realtor 
    public static void queryFive(Connection ctSQL) throws SQLException
    {
        Statement statement = ctSQL.createStatement();
        ResultSet returnedResult = statement.executeQuery(
                                      " SELECT buyingrid AS rid " 
                                    + "     FROM allTransactionsWithSellPrice"
                                    + "    	GROUP BY buyingrid"
                                    + "    	    HAVING COUNT(buyingrid) >= 3");

        System.out.println("|                          Result                          |");
        System.out.println("+----------------------------------------------------------+");
        System.out.format("%15s\n", "RID");

        while(returnedResult.next()) 
        {
            System.out.format("%15s\n", returnedResult.getString("rid"));
        }
    }
    
    // pre: none
    // post: menu is printed
    public static void printMenu()
    {
        System.out.println("+----------------------------------------------------------+");
        System.out.println("|                 Realtor Information Menu                 |");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| 1) Particular Land Buying CID Intrests                   |");
        System.out.println("| 2) Particular House Buying CID Intrests                  |");
        System.out.println("| 3) Show All Transactions                                 |");
        System.out.println("| 4) Sold For More Than Listing Price                      |");
        System.out.println("| 5) Involved In Three Or More Buying Transactions (RID)   |");                         
        System.out.println("| 6) Exit                                                  |");
        System.out.println("+----------------------------------------------------------+");
    }   

    public static void main(String[] args)
    {   
        try
        {   
            String toRun;
            String userInput;
            int userInputNum;
            PreparedStatement preparedStatement;
            ResultSet result;

            // ESTABLISH CONNECTION
            Connection connectionToSQL = DriverManager.getConnection(
            "jdbc:mysql://localhost/realtor", "root", "");

            printMenu();

            // GET USER INPUT
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter a Choice From the Menu: ");
            userInput = scan.nextLine();
            userInputNum = Integer.parseInt(userInput);
            System.out.println("+----------------------------------------------------------+");
            
            while (userInputNum != 6)
            {
                // INPUT VALIDATION
                while ((userInputNum < 1) || (userInputNum > 6))
                {
                    printMenu();
                    System.out.println("Enter a Choice From the Menu: ");
                    userInput = scan.nextLine();
                    userInputNum = Integer.parseInt(userInput);
                    System.out.println("+----------------------------------------------------------+");
                }
                
                // LAND CLIENT INTRESTS
                if (userInputNum == 1)
                {
                    toRun = queryOne(scan);
                    preparedStatement = connectionToSQL.prepareStatement(toRun);
                    result = preparedStatement.executeQuery();

                    System.out.println("|                          Result                          |");
                    System.out.println("+----------------------------------------------------------+");
                    System.out.format("%15s%15s%15s%15s%15s%15s%15s\n", "PID", "ACRES", "ANUM", "STREET"
                    , "CITY", "STATE", "LPRICE");

                    while(result.next()) 
                    {
                        System.out.format("%15s%15s%15s%15s%15s%15s%15s\n", result.getString("pid")
                        , result.getString("acres")
                        , result.getString("anum")
                        , result.getString("astreet")
                        , result.getString("acity")
                        , result.getString("astate")
                        , result.getString("lprice"));
                    }
                }
                // HOME CLIENT INTRESTS
                else if (userInputNum == 2)
                {
                    toRun = queryTwo(scan);
                    preparedStatement = connectionToSQL.prepareStatement(toRun);
                    result = preparedStatement.executeQuery();

                    System.out.println("|                          Result                          |");
                    System.out.println("+----------------------------------------------------------+");
                    System.out.format("%15s%15s%15s%15s%15s%15s%15s%15s%15s\n", "PID", "STYLE", "BEDS", "BATHS"
                    , "ANUM", "STREET", "CITY", "STATE", "LPRICE");

                    while(result.next()) 
                    {
                        System.out.format("%15s%15s%15s%15s%15s%15s%15s%15s%15s\n", result.getString("pid")
                        , result.getString("style")
                        , result.getString("beds")
                        , result.getString("baths")
                        , result.getString("anum")
                        , result.getString("astreet")
                        , result.getString("acity")
                        , result.getString("astate")
                        , result.getString("lprice"));
                    }
                }
                // ALL TRANSACTIONS
                else if (userInputNum == 3)
                {
                    queryThree(connectionToSQL);
                }
                // SOLD MORE THAN LISTING PRICE
                else if (userInputNum == 4)
                {
                    queryFour(connectionToSQL);
                }
                // THREE OR MORE BUYING TRANSACTIONS
                else if (userInputNum == 5)
                {
                    queryFive(connectionToSQL);
                }

                // RESET IF NOT WANTING TO QUIT
                if (userInputNum != 6)
                {
                    userInputNum = 0;
                }
            } 
        }
        // CATCH SQL ERRORS FROM TRY BLOCK
        catch (SQLException SQLError)
        {
            System.out.println(SQLError.getMessage() + " Can't connect to database");
            while(SQLError != null)
            {
                System.out.println("Message: " + SQLError.getMessage());
                SQLError = SQLError.getNextException();
            }
        }
        // CATCH ANY OTHER ERRORS
        catch (Exception error)
        {
            System.out.println("Other Error");
        }
    }
}
