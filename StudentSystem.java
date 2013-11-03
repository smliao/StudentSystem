/**
 * Student System
 * 
 * By Steven Liao, Tieu Nguyen, Raj Powar
 * CS157A
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.SpringLayout;


public class StudentSystem extends JFrame 
{
   // JDBC driver, database URL, username and password
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   static final String USERNAME= "root";
   static final String PASSWORD= "password";
   static final String DATABASE_URL = "jdbc:mysql://localhost/StudentSystem";
   static final String DB_URL = "jdbc:mysql://localhost/";
   
   //Connections
   private static Connection conn = null;
   private static Statement statement = null;

   
   // default query retrieves all data from authors table
   static final String DEFAULT_QUERY = "SELECT * "
   									+ "FROM STUDENT natural join EXAMRESULT natural join SEMESTER natural join EXAMINFO natural join ATTENDANCE natural join LABRESULT";
   
   private static ResultSetTableModel tableModel;
   private static JTextArea queryArea;
   
   // create ResultSetTableModel and GUI
   public StudentSystem() 
   {   
      super( "Student System" );
        
      // create ResultSetTableModel and display database table
      try 
      {
         // create TableModel for results of query SELECT * FROM authors
         tableModel = new ResultSetTableModel( JDBC_DRIVER, DATABASE_URL, 
            USERNAME, PASSWORD, DEFAULT_QUERY );

         // set up JTextArea in which user types queries
         queryArea = new JTextArea( "", 3, 100 );
         queryArea.setWrapStyleWord( true );
         queryArea.setLineWrap( true );
         
         JScrollPane scrollPane = new JScrollPane( queryArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
         
         // set up JButton for submitting queries
         JButton submitButton = new JButton( "Submit Query" );

         // create Box to manage placement of queryArea and 
         // submitButton in GUI
         Box box = Box.createHorizontalBox();
         box.add( scrollPane );
         box.add( submitButton );

         // create JTable delegate for tableModel 
         JTable resultTable = new JTable( tableModel );
         
         // place GUI components on content pane
         add( box, BorderLayout.NORTH );
         add( new JScrollPane( resultTable ), BorderLayout.CENTER );

         // create event listener for submitButton
         submitButton.addActionListener( 
         
            new ActionListener() 
            {
               // pass query to table model
               public void actionPerformed( ActionEvent event )
               {
                  // perform a new query
                  try 
                  {
                     tableModel.setQuery( queryArea.getText() );
                  } // end try
                  catch ( SQLException sqlException ) 
                  {
                     JOptionPane.showMessageDialog( null, 
                        sqlException.getMessage(), "Database error", 
                        JOptionPane.ERROR_MESSAGE );
                     
                     // try to recover from invalid user query 
                     // by executing default query
                     try 
                     {
                        tableModel.setQuery( DEFAULT_QUERY );
                        queryArea.setText( "" );
                     } // end try
                     catch ( SQLException sqlException2 ) 
                     {
                        JOptionPane.showMessageDialog( null, 
                           sqlException2.getMessage(), "Database error", 
                           JOptionPane.ERROR_MESSAGE );
         
                        // ensure database connection is closed
                        tableModel.disconnectFromDatabase();
         
                        System.exit( 1 ); // terminate application
                     } // end inner catch                   
                  } // end outer catch
               } // end actionPerformed
            }  // end ActionListener inner class          
         ); // end call to addActionListener

         setSize( 1200, 250 ); // set window size
         setVisible( true ); // display window  
      } // end try
      catch ( ClassNotFoundException classNotFound ) 
      {
         JOptionPane.showMessageDialog( null, 
            "MySQL driver not found", "Driver not found",
            JOptionPane.ERROR_MESSAGE );
         
         System.exit( 1 ); // terminate application
      } // end catch
      catch ( SQLException sqlException ) 
      {
         JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
            "Database error", JOptionPane.ERROR_MESSAGE );
               
         // ensure database connection is closed
         tableModel.disconnectFromDatabase();
         
         System.exit( 1 );   // terminate application
      } // end catch
      
      // dispose of window when user quits application (this overrides
      // the default of HIDE_ON_CLOSE)
      setDefaultCloseOperation( DISPOSE_ON_CLOSE );
      
      // ensure database connection is closed when user quits application
      addWindowListener(
      
         new WindowAdapter() 
         {
            // disconnect from database and exit when window has closed
            public void windowClosed( WindowEvent event )
            {
               tableModel.disconnectFromDatabase();
               System.exit( 0 );
            } // end method windowClosed
         } // end WindowAdapter inner class
      ); // end call to addWindowListener
   } // end DisplayQueryResults constructor
   
   private static void createDatabase() throws SQLException{
	   //Open a connection
	   System.out.println("Connecting to database...");
	   conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
	   String queryDrop = "DROP DATABASE IF EXISTS StudentSystem";
	   Statement stmtDrop = conn.createStatement();
	   stmtDrop.execute(queryDrop);
	   
	   //Create a database named StudentSystem
	   System.out.println("Creating database...");
	   statement = conn.createStatement();
	   
	   String sql = "CREATE DATABASE StudentSystem";
	   statement.executeUpdate(sql);
	   System.out.println("Database created successfully");
   }
   
   private static void createTable() throws SQLException
   {
	   //Open a connection and select the database named StudentSystem
	   System.out.println("Connecting to database...");
	   conn = DriverManager.getConnection(DB_URL+"StudentSystem", USERNAME, PASSWORD);
	   statement = conn.createStatement();
	   
	   //Drop all existing tables
	   String queryDropStudent = "DROP TABLE IF EXISTS STUDENT";
	   String queryDropExamResult = "DROP TABLE IF EXISTS EXAMRESULT";
	   String queryDropSemester = "DROP TABLE IF EXISTS SEMESTER";
	   String queryDropExamInfo = "DROP TABLE IF EXISTS EXAMINFO";
	   String queryDropAttendance = "DROP TABLE IF EXISTS ATTENDANCE";
	   String queryDropLabReport = "DROP TABLE IF EXISTS LABRESULT";
	   
	   Statement stmtDrop = conn.createStatement();
	   stmtDrop.execute(queryDropStudent);
	   stmtDrop.execute(queryDropExamResult);
	   stmtDrop.execute(queryDropSemester);
	   stmtDrop.execute(queryDropExamInfo);
	   stmtDrop.execute(queryDropAttendance);
	   stmtDrop.execute(queryDropLabReport);
	   
	   //Creating all tables for the StudentSystem database
	   String createStudent = "CREATE TABLE STUDENT("
			   + "ID INTEGER NOT NULL , "
			   + "FirstName VARCHAR(20), "
			   + "LastName VARCHAR(20), "
			   + "Gender VARCHAR(6), "
			   + "Grade VARCHAR(15), "
			   + "PRIMARY KEY (ID))";
	   statement.execute(createStudent);
	   
	   String createTableExamResult = "CREATE TABLE EXAMRESULT("
			   + "ID INTEGER NOT NULL, "
			   + "Score INTEGER DEFAULT 0, "
			   + "LetterGrade CHAR NOT NULL DEFAULT 'E', "
			   + "PRIMARY KEY (ID))";
			   
	   statement.execute(createTableExamResult);
	   
	   String createTableSemester = "CREATE TABLE SEMESTER("
			   + "ID INTEGER NOT NULL REFERENCES STUDENT(ID), "
			   + "Term VARCHAR(10) DEFAULT 'SPRING', "
			   + "Year INTEGER DEFAULT 2013, "
			   + "PRIMARY KEY (ID))";
	   statement.execute(createTableSemester);
	   
	   String createTableExamInfo = "CREATE TABLE EXAMINFO("
			   + "ID INTEGER NOT NULL, "
			   + "Version INTEGER DEFAULT 1, "
			   + "Date DATE DEFAULT '2013-10-01', "
			   + "PRIMARY KEY (ID))";
	   statement.execute(createTableExamInfo);
	   
	   String createTableAttendance = "CREATE TABLE ATTENDANCE("
			   + "ID INTEGER NOT NULL , "
			   + "Present BOOLEAN DEFAULT 0, "
			   + "Absences INTEGER DEFAULT 0, "
			   + "PRIMARY KEY (ID))";
	   statement.execute(createTableAttendance);
	   
	   String createTableLabReport = "CREATE TABLE LABRESULT("
			   + "ID INTEGER NOT NULL, "
			   + "LabLow INTEGER DEFAULT 0, "
			   + "LabHigh INTEGER DEFAULT 0, "
			   + "LabPassFail BOOLEAN DEFAULT 0, "
			   + "PRIMARY KEY (ID))";
			
	   statement.execute(createTableLabReport);
	   
	   System.out.println("Table called STUDENT, EXAMRESULT, SEMESTER, EXAMINFO, ATTENDANCE, LABRESULT created successfully...");

   }
   
   private static void CreateTriggers() throws SQLException
   {
	   String trigger1 = "DROP Trigger IF EXISTS Updates "
	 		+ "delimiter // "
	   		+ "CREATE Trigger Updates "
	   		+ "AFTER INSERT of ID ON STUDENT "
	   		+ "WHEN (new.ID not in (Select ID FROM STUDENT) "
	   		+ "BEGIN "
	   		+ "insert into EXAMINFO values (new.ID, 1, '2013-10-01'); "
	   		+ "END;";
	   statement.execute(trigger1);
	   
	   
	   String trigger2 = "DROP TRIGGER IF EXISTS UpdatePassFail; "
	   		+ "delimiter // "
	   		+ "CREATE TRIGER UpdatePassFail; "
	   		+ "AFTER INSERT ON ATTENDANCE "
	   		+ "FOR EACH ROW "
	   		+ "BEGIN "
	   		+ "IF new.present = 0 AND Exam.Date = '2012-09-12' "
	   		+ "THEN INSERT INTO EXAM RESULTS VALUES (0, 'F') "
	   		+ "WHERE EXAMRESULT.ID = STUDENT.ID; "
	   		+ "END IF; "
	   		+ "END; "
	   		+ "//delimiter; ";
	   statement.execute(trigger2);
	   
	   String trigger3 = "DROP TRIGGER IF EXISTS ExtraCredit; "
	   		+ "//delimiter "
	   		+ "CREATE TRIGGER ExtraCredit; "
	   		+ "AFTER UPDATE ON EXAMRESULT "
	   		+ "FOR EACH ROW "
	   		+ "BEGIN IF SCORE = 10 AND Score <> 11 "
	   		+ "THEN UPDATE EXAMRESULT "
	   		+ "SET SCORE = 11 "
	   		+ "WHERE EXAMRESULT.ID = STUDENT.ID; "
	   		+ "END IF; "
	   		+ "END; "
	   		+ "//delimiter; ";
	   statement.execute(trigger3);
	   		
	   		
			   
   }
   
   //Load Data from previously created text file
   private static void loadDataIntoTable() throws SQLException
   {
	   System.out.println("Load data from a file student.txt and examresult.txt");
	   String loadDataSQLStudent = "LOAD DATA LOCAL INFILE 'C:/JDBC/student.txt' INTO TABLE STUDENT";
	   statement.execute(loadDataSQLStudent);
	   String loadDataSQLExamResult = "LOAD DATA LOCAL INFILE 'C:/JDBC/examresult.txt' INTO TABLE EXAMRESULT";
	   statement.execute(loadDataSQLExamResult);
	   String loadDataSQLSemester = "LOAD DATA LOCAL INFILE 'C:/JDBC/semester.txt' INTO TABLE SEMESTER";
	   statement.execute(loadDataSQLSemester);
	   String loadDataSQLExamInfo = "LOAD DATA LOCAL INFILE 'C:/JDBC/examinfo.txt' INTO TABLE EXAMINFO";
	   statement.execute(loadDataSQLExamInfo);
	   String loadDataSQLAttendance = "LOAD DATA LOCAL INFILE 'C:/JDBC/attendance.txt' INTO TABLE ATTENDANCE";
	   statement.execute(loadDataSQLAttendance);
	   String loadDataSQLLabReport = "LOAD DATA LOCAL INFILE 'C:/JDBC/labreport.txt' INTO TABLE LABRESULT";
	   statement.execute(loadDataSQLLabReport);
   }
   
   //GUI of commands
   public static void Buttons(){
	   JFrame f = new JFrame("Student System Controls");
	   JButton b1 = new JButton("Insert Student Information");
	   JButton b2 = new JButton("Delete Student");
	   JButton b3 = new JButton("View Detailed Info");
	   JButton b4 = new JButton("View Student");
	   JButton b5 = new JButton("View Exam Average Score");
	   JButton b6 = new JButton("Number Registered Students");
	   JButton b7 = new JButton("View Lab Scores");
	   
	   
	   b1.addActionListener(new ActionListener(){

		public void actionPerformed(ActionEvent arg0) {
			CreateInputStudent();	
		}
		   
	   });
	   
	   b2.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				CreateDeleteStudent();	
			}
			   
		   });
				
		 b3.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {	
					try {
						tableModel.setQuery("SELECT * "
									+ "FROM STUDENT natural join EXAMRESULT natural join SEMESTER natural join EXAMINFO natural join ATTENDANCE natural join LABRESULT");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
				   
			   });
	   
		 b4.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {	
					try {
						tableModel.setQuery("SELECT * "
									+ "FROM STUDENT");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
				   
			   });
	   
		 b5.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {	
					try {
						tableModel.setQuery("SELECT avg(score) "
									+ "FROM EXAMRESULT natural join Student");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
				   
			   });
		 
		 b6.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {	
					try {
						tableModel.setQuery("SELECT count(distinct ID) "
									+ "FROM Student");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
				   
			   });
		 
		 b7.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent arg0) {	
					try {
						tableModel.setQuery("SELECT ID, LabLow, LabHigh "
								+ "FROM LABRESULT");
					} catch (IllegalStateException | SQLException e) {
						e.printStackTrace();
					}
				}
				   
			   });

	   f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   Container content = f.getContentPane();
	   content.setLayout(new FlowLayout());
	   content.add(b1);
	   content.add(b2);
	   content.add(b3);
	   content.add(b4);
	   content.add(b5);
	   content.add(b6);
	   content.add(b7);
	   f.pack();
	   f.setSize(500, 200);
	   f.setVisible(true); 
   }
   
   
   public static void CreateInputStudent(){
	   final JFrame insertframe = new JFrame("Student Insert");
	   final String[] labels = {"ID: ", "FirstName: ", "LastName: ", "Gender: ", "Grade: ", "Term: ", "Year: ", "Present(0=no, 1=yes): ", "Number of Absences: "};
	 	final JTextField[] inputs = new JTextField[labels.length];
	 	JPanel p = new JPanel(new SpringLayout());
	 	
		for(int i = 0; i < labels.length; i++){
	 		JLabel l = new JLabel(labels[i], JLabel.TRAILING);
	 		p.add(l);
	 		inputs[i] = new JTextField(15);
	 		l.setLabelFor(inputs[i]);
	 		p.add(inputs[i]);
	 	} 
	 	
		JButton button = new JButton("Submit");
		p.add(new JLabel());
		p.add(button);
		
	 	
	 	SpringUtilities.makeCompactGrid(p, labels.length + 1, 2, 7, 7, 7, 7);
	 	
	 	button.addActionListener(new ActionListener(){
	 		public void actionPerformed(ActionEvent e){
	 			
	 			String[] studentInput = new String[labels.length];
	 			for(int i = 0; i < labels.length; i++){
	 				studentInput[i] = inputs[i].getText().toString();
	 			}
	 			
	 			
	 			String sql = null;
	 			String sql2 = null;
	 			String sql3 = null;
	 			
	 			sql = "INSERT INTO STUDENT "
	 				+ "VALUES (" + Integer.parseInt(studentInput[0])
	 				+ ", '" + studentInput[1]
	 				+ "', '" + studentInput[2]
	 				+ "', '" + studentInput[3]
	 				+ "', '" + studentInput[4]
	 				+ "')";
	 			
	 			sql2 = "INSERT INTO SEMESTER "
		 				+ "VALUES (" + Integer.parseInt(studentInput[0])
		 				+ ", '" + studentInput[5]
		 				+ "', '" + Integer.parseInt(studentInput[6])
		 				+ "')";
	 			
	 			sql3 = "INSERT INTO ATTENDANCE "
		 				+ "VALUES (" + Integer.parseInt(studentInput[0])
		 				+ ", '" + Integer.parseInt(studentInput[7])
		 				+ "', '" + Integer.parseInt(studentInput[8])
		 				+ "')";
	 			try {
					statement.executeUpdate(sql);
					statement.executeUpdate(sql2);
					statement.executeUpdate(sql3);
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	 			insertframe.dispose();
	 			
	 		}
	 	});
	 	
	 	
	 	insertframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	insertframe.setContentPane(p);
	 	insertframe.pack();
	 	insertframe.setSize(500, 500);
	 	insertframe.setVisible(true); 	
}
   
   public static void CreateDeleteStudent(){
	   final JFrame deleteframe = new JFrame("Student Delete");
	   final String[] labels = {"FirstName: ", "LastName: "};
	 	final JTextField[] deletes = new JTextField[labels.length];
	 	JPanel p = new JPanel(new SpringLayout());
	 	
		for(int i = 0; i < labels.length; i++){
	 		JLabel l = new JLabel(labels[i], JLabel.TRAILING);
	 		p.add(l);
	 		deletes[i] = new JTextField(15);
	 		l.setLabelFor(deletes[i]);
	 		p.add(deletes[i]);
	 	} 
	 	
		JButton button = new JButton("Submit");
		p.add(new JLabel());
		p.add(button);
	 	
	 	SpringUtilities.makeCompactGrid(p, labels.length + 1, 2, 5, 5, 6, 6);
	 	
	 	
	 	
	 	button.addActionListener(new ActionListener(){
	 		public void actionPerformed(ActionEvent e){
	 			String[] studentDelete = new String[labels.length];
	 			
	 			for(int i = 0; i < labels.length; i++){
	 				studentDelete[i] = deletes[i].getText().toString();
	 			}
	 			
	 			
	 			String sql = null;
	 			
	 			sql = "DELETE FROM STUDENT "
	 				+ "WHERE FirstName='"
	 				+ studentDelete[0]
	 				+ "' and LastName='"
	 				+ studentDelete[1]
	 				+ "';";
	 			try {
					statement.executeUpdate(sql);
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	 			deleteframe.dispose();
	 			
	 		}
	 	});
	 	
	 	
	 	deleteframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	deleteframe.setContentPane(p);
	 	deleteframe.pack();
	 	deleteframe.setSize(300, 300);
	 	deleteframe.setVisible(true); 	
}   
   
   
   
   // execute application
   public static void main( String args[] ) throws SQLException
   {
      try{
    	  Class.forName(JDBC_DRIVER); //Register JDBC Driver
    	  createDatabase();
    	  createTable();
    //	  CreateTriggers();
    	  loadDataIntoTable();
    	  new StudentSystem();
    	  Buttons();
      }
      catch(SQLException se){se.printStackTrace(); } 
      catch (Exception e) { e.printStackTrace(); }
      finally{
    	try {if(statement == null) statement.close(); } //FORCE ==
      	catch(SQLException se2) { }
      
      	try { if(conn == null) conn.close(); }
      	catch(SQLException se) { se.printStackTrace(); } //FORCE ==
      }
      System.out.println("Goodbye!");
   } // end main
} // end class DisplayQueryResults



/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
