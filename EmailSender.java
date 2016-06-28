import java.util.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.io.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender{

   private static String USER_NAME = ""; //Add username (do not add @gmail.com, only add charachters preceding @)
   private static String PASSWORD = ""; //Add password

   public static void main(String [] args){
   
      Properties props = new Properties();
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.socketFactory.port", "587");
      props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.port", "587");
      
      String inFile = "E:/someplace/contactList.txt"; //Add contact list filepath here.
      String attachFile = "E:/someplace/attachmentfile.pdf"; //Add filepath for attachment
      String filename = "contactList.txt";
      String fileUpdate = "";
      
      ArrayList<String> contactList = new ArrayList<String>();
      ArrayList<String> sentList = new ArrayList<String>();
      ArrayList<String> nameList = new ArrayList<String>();
      ArrayList<String> companyList = new ArrayList<String>();
      ArrayList<String> emailList = new ArrayList<String>();
      
      String[] splitString;
      String line;
      
      try {
      
         //Read file
         Scanner scan = new Scanner(new File(inFile));
         scan.nextLine();
         while (scan.hasNextLine()){
            contactList.add(scan.nextLine());        
         }
         scan.close();
         
      } catch(IOException e){
      
         System.out.println(e.getMessage());
      }
      
      //Split contactList by line then by tab and place each item into appropriate lists
      for (int i = 0; i < contactList.size(); i++) {
      
         line = contactList.get(i);
         splitString = line.split("\\t");
         
         for (int p = 0; p < 4; p++){
         
            if (p == 0){
               sentList.add(splitString[p]);
            } else if (p == 1){
               nameList.add(splitString[p].toLowerCase());
            } else if (p == 2){
               companyList.add(splitString[p].toUpperCase());
            } else {
               emailList.add(splitString[p]);
            }
         }
      }
      
     //Send emails
     sentList = mailer(USER_NAME, PASSWORD, sentList, nameList, companyList, emailList, attachFile, props);
     
     //Make updated file
     fileUpdate = "Sent\tName\tCompany\tEmail";
     
     for (int s = 0; s < contactList.size(); s++){
        fileUpdate += "\n" + sentList.get(s) + "\t" + nameList.get(s) + "\t" + companyList.get(s) + "\t" + emailList.get(s);
      }
   
      writeFile(filename, fileUpdate);      
      
   }//End main
   
  //Mail method
  private static ArrayList<String> mailer(final String user, final String pass, ArrayList<String> sList, ArrayList<String> nList, ArrayList<String> cList, ArrayList<String> eList, String attach, Properties props){
      
      String to = "";
      String subject = "";
      String name = "";
      String email_body = "";
      
      //Open email session for sending
      Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(user, pass);
                }});
      
      for (int t = 0; t < sList.size(); t++){
         
         if (sList.get(t).equals("N")){
            //Contact email
            to = eList.get(t);
            
            //Capitalize and set name
            name = Character.toUpperCase(nList.get(t).charAt(0)) + nList.get(t).substring(1);
            
            //Subject - Currently set to "COMPANY NAME: subject"
            subject = cList.get(t) + ": Sample Subject";
            
            //Email body - Put message here
            email_body = "Dear " + name + ", \n\n\tSample Text " + 
            "Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text " + 
            "Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text " +
            "Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text " +
            "Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text Sample Text " + 
            "\nThank you for your time and consideration.\n\nSincerely,\nName";
            
            System.out.println(email_body);
            
            
            try {
              Message message = new MimeMessage(session);
              message.setFrom(new InternetAddress(user));
              message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
              message.setSubject(subject);
              
              BodyPart messageBodyPart = new MimeBodyPart();
              messageBodyPart.setText(email_body);
              
              Multipart multipart = new MimeMultipart();
              multipart.addBodyPart(messageBodyPart);
              
              messageBodyPart = new MimeBodyPart();
              DataSource source = new FileDataSource(attach);
              messageBodyPart.setDataHandler(new DataHandler(source));
              messageBodyPart.setFileName(attach);
              multipart.addBodyPart(messageBodyPart);
              
              message.setContent(multipart);
              
              //Send  COMMENT THIS LINE OUT IF YOU WANT TO TEST MESSAGES BEFORE SENDING.
              Transport.send(message);
			  
              //Replace "N" with "Y" in list if sent.
              sList.set(t, sList.get(t).replace('N', 'Y'));

              System.out.println("Message Sent");

          } catch (Exception e) {
              System.out.println(e);
          }    
         }
      }
      return sList;
  }//End mail method
  
  //Save method
  public static void writeFile(String filename, String fileUpdate){
      try{
        File file = new File (filename);
        BufferedWriter out = new BufferedWriter(new FileWriter(file)); 
        out.write(fileUpdate);
        out.close();
      }catch(Exception e) {
              System.out.println(e);
      }
   }//End save method
}