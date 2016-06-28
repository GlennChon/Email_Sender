# Email_Sender
Java, email sender w/ attachment

By: Glenn Chon

Takes a tab delimited file with the following headers in the first row:
Sent	Name	Company	Email

I suggest using excel and saving the file as a .txt for ease.

This program will run with simple customizations in the USERNAME, PASSWORD, FILEPATHS for contactList.txt and attachment, SUBJECT and BODY fields.
If you would like to test before sending all emails, simply comment out the Transport.send(message); on line 160.
