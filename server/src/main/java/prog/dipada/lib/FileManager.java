package prog.dipada.lib;

import prog.dipada.model.Email;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * This class manage all files of users
 * and split files according to established hierarchy:
 * useremail --> in/out
 *
 * */
public class FileManager {

    public static void main(String[] args) {
        Email e1 = new Email("daniele@dipada.it", "oggetto 1", List.of("giovanni@dipada.it", "peppino@dipada.it"),"ciao a tutti da dan", new Date());
        Email e2 = new Email("giovanni@dipada.it", "oggetto 2", List.of("daniele@dipada.it", "peppino@dipada.it"),"ciao a tutti da dan", new Date());
        Email e3 = new Email("peppino@dipada.it", "oggetto 3", List.of("daniele@dipada.it", "giovanni@dipada.it"),"ciao a tutti da dan", new Date());


        FileManager fileManager = new FileManager();

        fileManager.addUserDirs("daniele@dipada.it");
        fileManager.addUserDirs("giovanni@dipada.it");


        e1 = fileManager.save(e1);
        e2 = fileManager.save(e2);
        e3 = fileManager.save(e3);


        //deletgeEmail(e1,"dan@dipada.it");
        //deletgeEmail(e2,"dan@dipada.it");
    }
    private final String filePath;
    public FileManager(){
        this.filePath = String.valueOf(getUserFileDirectory(""));
        System.out.println("File path " + filePath);
    }

    public boolean addUserDirs(String userEmail){
        File in = new File(filePath + "/" + userEmail + "/in");
        File out = new File(filePath + "/" + userEmail + "/out");

        if(!checkUserExist(userEmail)){
            System.out.println("Creo le cartelle per " + userEmail);
            return createDirectory(in) && createDirectory(out);
        }else{
            System.out.println("Utente già esistente");
            return true;
        }
    }

    /**
     *
     * This method save and email sent in inbox directory of sender
     * and outbox directory of receivers
     *
     * @param email email to save
     * */
    public synchronized Email save(Email email){
        Email newEmail = null;

        try{
            String path = filePath + "/" + email.getSender() + "/out";
            //String path = String.valueOf(getUserFileDirectory("/" + email.getSender() + "/out"));
            File f = new File(path);
            if(!f.exists())
                createDirectory(f);

            FileOutputStream fout;
            //newEmail = new Email(email.getSender(), email.getSubject(), email.getReceivers(), email.getMessageText(), email.getDate());
            //newEmail.setIsSent(true);
            email.setIsSent(true);

            fout = new FileOutputStream(path + "/" + email.getId());
            ObjectOutputStream objOut = new ObjectOutputStream(fout);

            //objOut.writeObject(newEmail);
            objOut.writeObject(email);
            objOut.flush();

            objOut.close();
            fout.close();

            List<String> receivers = email.getReceivers();
            for(String rcv : receivers){
                path = filePath + "/" + rcv + "/in";
                //path = String.valueOf(getUserFileDirectory("/" + rcv + "/in"));
                f = new File(path);
                if(!f.exists())
                    createDirectory(f);

                fout = new FileOutputStream(path + "/" + email.getId());
                objOut = new ObjectOutputStream(fout);

                email.setIsSent(false);
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
        email.setIsSent(true);
        return email;
        //return newEmail;
    }

    // TODO caricare email in arrivo
    /**
     *
     * This method retrive inbox user emails and return a list of emails
     * @param user user that request inbox email
     * @return Inbox list of emails
     *
     */
    public synchronized List<Email> loadInbox(String user) {
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
    public synchronized List<Email> loadOutbox(String user) {
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
            //for(Email e : outbox)
              //  System.out.println(user + " OUTBOX: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outbox;
    }

    //TODO delete email
    public synchronized void deleteEmail(Email email, String user){
        if(email != null) {
            try {
                System.out.println("Email inviata: " + email.isSent());
                if (email.isSent()) {
                    System.out.println("TRUE");
                    System.out.println(getUserFileDirectory("/" + user + "/out/" + email.getId()));
                    Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/out/" + email.getId()))));
                } else {
                    System.out.println("FALSE");
                    System.out.println(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/in/" + email.getId()))));
                    Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/in/" + email.getId()))));
                }
            } catch (IOException e) {
                System.out.println("Filemanager files.delete exception");
                e.printStackTrace();
            }
        }
    }

    private boolean createDirectory(File f) {
        return f.mkdirs();
    }

    // TODO creare cartella user

    private File getUserFileDirectory(String subPath) {
        String path = new File("").getAbsolutePath() + "/server/src/main/resources/prog/dipada/file" + subPath;
        return new File(path);
    }

    /**
     *
     * Check if a userEmail exists, by verifying
     * if exists its folder
     *
     *  */
    public boolean checkUserExist(String userEmail){
        String[] dir = (getUserFileDirectory("").list());
        return dir != null && dir.length != 0 && Arrays.stream(dir).toList().contains(userEmail);
    }
}
