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

    private final String filePath;
    public FileManager(){
        this.filePath = String.valueOf(getUserFileDirectory(""));
    }

    public void addUserDirs(String userEmail){
        File in = new File(filePath + "/" + userEmail + "/in");
        File out = new File(filePath + "/" + userEmail + "/out");

        if(!checkUserExist(userEmail)){
            if (createDirectory(in)) {
                createDirectory(out);
            }
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
        try{
            String path = filePath + "/" + email.getSender() + "/out";
            File f = new File(path);
            if(!f.exists())
                createDirectory(f);

            FileOutputStream fout;
            email.setIsSent(true);

            fout = new FileOutputStream(path + "/" + email.getId());
            ObjectOutputStream objOut = new ObjectOutputStream(fout);

            objOut.writeObject(email);
            objOut.flush();

            objOut.close();
            fout.close();

            List<String> receivers = email.getReceivers();
            for(String rcv : receivers){
                path = filePath + "/" + rcv + "/in";
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

        email.setIsSent(true);
        return email;
    }

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

            if(userDir.listFiles() == null)
                return null;
            for (File f : Objects.requireNonNull(userDir.listFiles())) {
                ObjectInputStream oi;
                FileInputStream fis;
                fis = new FileInputStream(f);
                oi = new ObjectInputStream(fis);
                inbox.add((Email) oi.readObject());
                oi.close();
                fis.close();
            }
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            e.printStackTrace();
            inbox = null;
        }
        return inbox;
    }

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
            ObjectInputStream oi;
            FileInputStream fis;

            for(File f : Objects.requireNonNull(userDir.listFiles())){
                fis = new FileInputStream(f);
                oi = new ObjectInputStream(fis);
                outbox.add((Email) oi.readObject());
                oi.close();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outbox;
    }

    public synchronized void deleteEmail(Email email, String user){
        if(email != null) {
            try {
                if (email.isSent()) {
                    Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/out/" + email.getId()))));
                } else {
                    Files.delete(Path.of(String.valueOf(getUserFileDirectory("/" + user + "/in/" + email.getId()))));
                }
            } catch (IOException e) {
                System.out.println("Filemanager files delete exception");
                e.printStackTrace();
            }
        }
    }

    private boolean createDirectory(File f) {
        return f.mkdirs();
    }

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
