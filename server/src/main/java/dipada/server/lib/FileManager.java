package dipada.server.lib;

import dipada.server.model.Email;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Date;
import java.util.List;

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
        save(e1);
        save(e2);
        save(e3);
    }

    public synchronized static Email save(Email email){
        boolean result;
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

    private static boolean createDirectory(File f) {
        return f.mkdirs();
    }

    private static File getUserFileDirectory(String subPath) {
        String path = new File("").getAbsolutePath() + "/server/src/main/resources/dipada/server/file" + subPath;
        return new File(path);
    }


}
