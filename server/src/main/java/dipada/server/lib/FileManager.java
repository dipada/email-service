package dipada.server.lib;

import dipada.server.model.Email;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * This class manage all files of users
 * and split files according to established hierarchy:
 * useremail --> in/out
 *
 * */
public class FileManager {
    public static void main(String[] args) {
        Email e1 = new Email("dan@dipada.it", "oggetto 1", List.of("gio@dipada.it", "pep@dipada.it"),"ciao a tutti da dan", new Date());
        Email e2 = new Email("gio@dipada.it", "oggetto 2", List.of("dan@dipada.it", "pep@dipada.it"),"ciao a tutti da dan", new Date());
        Email e3 = new Email("pep@dipada.it", "oggetto 3", List.of("dan@dipada.it", "gio@dipada.it"),"ciao a tutti da dan", new Date());
        e1 = save(e1);
        e2 = save(e2);
        e3 = save(e3);

        deletgeEmail(e1,"dan@dipada.it");
        //deletgeEmail(e2,"dan@dipada.it");
    }

    /**
     *
     * This method save and email sent in inbox directory of sender
     * and outbox directory of receivers
     *
     * @param email email to save
     * */
    public synchronized static Email save(Email email){
        Email newEmail = null;

        try{
            String path = String.valueOf(getUserFileDirectory("/" + email.getSender() + "/out"));
            File f = new File(path);
            if(!f.exists())
                createDirectory(f);

            FileOutputStream fout;
            newEmail = new Email(email.getSender(), email.getSubject(), email.getReceivers(), email.getMessageText(), email.getDate());
            newEmail.setSent(true);


            fout = new FileOutputStream(path + "/" + email.getId());
            ObjectOutputStream objOut = new ObjectOutputStream(fout);

            objOut.writeObject(newEmail);
            objOut.flush();

            objOut.close();
            fout.close();

            List<String> receivers = email.getReceivers();
            for(String rcv : receivers){
                path = String.valueOf(getUserFileDirectory("/" + rcv + "/in"));
                f = new File(path);
                if(!f.exists())
                    createDirectory(f);

                fout = new FileOutputStream(path + "/" + email.getId());
                objOut = new ObjectOutputStream(fout);

                email.setSent(false);
                objOut.writeObject(email);
                objOut.flush();
                objOut.close();
                fout.close();
            }

            objOut.close();
            fout.close();

        } catch (FileNotFoundException e) {
            System.out.println("FileManager errore fileoutputstream");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Filemanager OBJECToutputstream error");
            e.printStackTrace();
        }

        //System.out.println(path);
        //String path2 = "/home/dan/IdeaProjects/dipadamail/server/src/main/resources/dipada/server";
        //System.out.println(path2);
        return newEmail;
    }

    // TODO caricare email in arrivo
    /**
     *
     * This method retrive inbox user emails and return a list of emails
     * @param user user that request inbox email
     * @return Inbox list of emails
     *
     */
    public synchronized static List<Email> loadInbox(String user) {
        List<Email> inbox = new ArrayList<>();
        try {
            File userDir = new File(String.valueOf(getUserFileDirectory("/" + user + "/in/")));
            ObjectInputStream oi = null;
            FileInputStream fis = null;
            if(userDir.listFiles() == null)
                return null;
            for (File f : Objects.requireNonNull(userDir.listFiles())) {
                fis = new FileInputStream(f);
                oi = new ObjectInputStream(fis);
                inbox.add((Email) oi.readObject());
                oi.close();
                fis.close();
            }
            for(Email e : inbox)
                System.out.println(user + " INBOX: " + e);
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            e.printStackTrace();
            inbox = null;
        } finally {
        }
        return inbox;
    }

    // TODO caricare email in uscita
    /**
     *
     * This method retrive outbox user emails and return a list of emails
     * @param user user that request inbox email
     * @return Outbox list of emails
     */
    public synchronized static List<Email> loadOutbox(String user) {
        List<Email> outbox = new ArrayList<>();
        try {
            File userDir = new File(String.valueOf(getUserFileDirectory("/" + user + "/out/")));
            ObjectInputStream oi = null;
            FileInputStream fis = null;

            for(File f : Objects.requireNonNull(userDir.listFiles())){
                fis = new FileInputStream(f);
                oi = new ObjectInputStream(fis);
                outbox.add((Email) oi.readObject());
                oi.close();
                fis.close();
            }
            for(Email e : outbox)
                System.out.println(user + " OUTBOX: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outbox;
    }

    //TODO delete email
    public synchronized static void deletgeEmail(Email email, String user){
        try{
            System.out.println("Email inviata: "+ email.isSent());
            if(email.isSent()){
                System.out.println("TRUE");
                System.out.println(getUserFileDirectory("/" + user + "/out/" + email.getId() + ".dp"));
                Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/out/" + email.getId() + ".dp"))));
            }else{
                System.out.println("FALSE");
                System.out.println(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/in/" + email.getId() + ".dp"))));
                Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/in/" + email.getId() + ".dp"))));
            }
        } catch (IOException e) {
            System.out.println("Filemanager files.delete exception");
            e.printStackTrace();
        }
    }

    private static boolean createDirectory(File f) {
        return f.mkdirs();
    }

    private static File getUserFileDirectory(String subPath) {
        String path = new File("").getAbsolutePath() + "/server/src/main/resources/dipada/server/file" + subPath;
        return new File(path);
    }




}